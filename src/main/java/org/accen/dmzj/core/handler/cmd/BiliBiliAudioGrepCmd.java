package org.accen.dmzj.core.handler.cmd;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.exception.BiliBiliCookieNeverInit;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.task.api.bilibili.ApiBiliBiliApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.FfmpegUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BiliBiliAudioGrepCmd implements CmdAdapter {

	@Value("${sys.static.html.music}")
	private String tempMusicPath;//usr/local/niginx/music/
//	@Value("${sys.static.url}")
//	private String staticUrl;//http://localhost:80/
	@Value("${sys.static.url.music:music/}")
	private String staticMusic;
	@Autowired
	private FfmpegUtil ffmpegUtil;
	@Autowired
	private ApiBiliBiliApiClient apiClient;
	@Autowired
	private CfgResourceMapper cfgResourceMapper;
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	
	@Value("${coolq.biligrep.coin.decrease:10}")
	private int coinDescrease;
	@Value("${coolq.fuzzymsg.coin.decrease:3}")
	private int decrease = 3;
	@Autowired
	private CheckinCmd checkinCmd;
	
	@Autowired
	private TaskManager taskManager;
	
	@Override
	public String describe() {
		return "抽取B站音频";
	}

	@Override
	public String example() {
		return "抽取B站[www.bilibili.com/video/av64689940]从[00:00:00]到[00:01:59]音乐，设置名称[小狐狸]";
	}

	private static final String KEY_PREFFIX = "audio_bilibili_";
	
	private final static Pattern grepPattern = Pattern.compile("^抽取B站(.+)?从(.+)?到(.+)?(音乐|语音)，设置名称(.+)?$");
	@Override
	public synchronized GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = grepPattern.matcher(message);
		if(matcher.matches()) {
			
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			//金币检验
			int curCoin = checkinCmd.getCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			if(curCoin<0) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未绑定哦，暂时无法添加歌曲，发送[绑定]即可绑定个人信息喵~");
			}else if(curCoin-coinDescrease<0) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您库存金币不够了哦，暂无法添加词条喵~");
			}else {
				String url = matcher.group(1);
				String ss = matcher.group(2);
				String tt = matcher.group(3);
				String type = matcher.group(4);
				String name = matcher.group(5);
				
				int diff = ffmpegUtil.checkTimeIllegalEx(ss, tt);
				if(diff<=0) {
					task.setMessage("时间格式输入错误喵~");
					return task;
				}else if(diff>120&&"语音".equals(type)){
					//如果大于120且为语音，则不给过
					task.setMessage("抽取的语音时长不能超过120秒的说~");
					return task;
				}
				try {
					
					taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), "视频["+url+"]解析中~");
					
					String[] rs = apiClient.downLoadAdaptive(url, 360);
					String videoFile = rs[0];
					//剪切音频
					String target = tempMusicPath+KEY_PREFFIX+name+".aac";
					String audio = ffmpegUtil.convertVideo2Audio(videoFile, target, "aac", ss, tt);
					if(audio==null) {
						task.setMessage("视频剪切失败喵~");
						return task;
					}
					if("音乐".equals(type)) {
						CfgResource cr = new CfgResource();
						cr.setCfgKey(KEY_PREFFIX+name);
						cr.setCfgResource(staticMusic+KEY_PREFFIX+name+".aac");
						cr.setResourceType("music");
						cr.setTitle(name);
						cr.setContent(rs[2]);
						cr.setImage(rs[1]);
						cr.setOriginResource(rs[3]);
						cr.setCreateUserId(qmessage.getUserId());
						String createNickName = (String) ((Map<String, Object>)qmessage.getEvent().get("sender")).get("nickname");
						String createCard = (String) ((Map<String, Object>)qmessage.getEvent().get("sender")).get("card");//群名片
						cr.setCreateUserName(StringUtils.isEmpty(createCard)?createNickName:createCard);
						cr.setCreateTime(new Date());
						cfgResourceMapper.insert(cr);
						
						//消耗金币
						int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -coinDescrease);
						task.setMessage(CQUtil.at(qmessage.getUserId())+" 视频解析完成喵~触发词条为[B站点歌"+name+"]，本次消耗金币："+coinDescrease+"，剩余："+newCoin+"。Tips：每次点歌成功后会回赠1枚金币喵~");
					}else {
						CfgQuickReply reply = new CfgQuickReply();
						reply.setMatchType(2);
						reply.setPattern(".*?"+StringUtil.transferPattern(name)+".*");
						reply.setReply("[CQ:record,file=file:///"+audio+"]");
						reply.setApplyType(2);
						reply.setApplyTarget(qmessage.getGroupId());
						reply.setNeedAt(2);
						reply.setCreateTime(new Date());
						reply.setCreateUserId(qmessage.getUserId());
						reply.setStatus(1);
						cfgQuickReplyMapper.insert(reply);
						//消耗金币
						int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -decrease);
						task.setMessage(CQUtil.at(qmessage.getUserId())+" 视频解析完成喵~添加词条编号："+reply.getId()+"，本次消耗金币："+decrease+"，剩余："+newCoin);
					}
					
					
					return task;
				} catch (BiliBiliCookieNeverInit e) {
					e.printStackTrace();
				}
			}
			
			
			
		}
		return null;
	}

}
