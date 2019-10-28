package org.accen.dmzj.core.handler.cmd;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.exception.BiliBiliCookieNeverInit;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.task.api.bilibili.ApiBiliBiliApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.FfmpegUtil;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
	private TaskManager taskManager;
	
	@Override
	public String describe() {
		return "抽取B站音频";
	}

	@Override
	public String example() {
		return "抽取B站[www.bilibili.com/video/av64689940]从[00:00:00]到[00:01:59]音频，设置名称[小狐狸]";
	}

	private static final String KEY_PREFFIX = "audio_bilibili_";
	
	private final static Pattern grepPattern = Pattern.compile("^抽取B站(.+)?从(.+)?到(.+)?音频，设置名称(.+)?$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = grepPattern.matcher(message);
		if(matcher.matches()) {
			
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			String url = matcher.group(1);
			String ss = matcher.group(2);
			String tt = matcher.group(3);
			String name = matcher.group(4);
			
			if(!ffmpegUtil.checkTimeIllegal(ss, tt)) {
				task.setMessage("时间格式输入错误喵~");
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
				CfgResource cr = new CfgResource();
				cr.setCfgKey(KEY_PREFFIX+name);
				cr.setCfgResource(staticMusic+KEY_PREFFIX+name+".aac");
				cr.setResourceType("music");
				cr.setTitle(name);
				cr.setContent(rs[2]);
				cr.setImage(rs[1]);
				cr.setOriginResource(rs[3]);
				cfgResourceMapper.insert(cr);
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 视频解析完成喵~触发词条为[B站点歌"+name+"]");
				return task;
			} catch (BiliBiliCookieNeverInit e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

}
