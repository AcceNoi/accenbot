package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.api.MusicApiClient;
import org.accen.dmzj.core.api.vo.Music163Result;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@FuncSwitch("cmd_music_share")
@Transactional
@Component
public class MusicShareCmd implements CmdAdapter {

	@Autowired
	private MusicApiClient musicApiClient;
	@Autowired
	private CfgResourceMapper cfgResourceMapper;
	
	@Value("${coolq.musicshare.fav.increase:1}")
	private int increase = 1;
	
	@Autowired
	private CheckinCmd checkinCmd;
	
	
	@Override
	public String describe() {
		return "分享歌曲";
	}

	@Override
	public String example() {
		return "[网易|qq|虾米|B站]点歌 恋爱循环";
	}

	@Value("${coolq.music.list.pageSize:10}")
	private int musicListSize;
	
	private static final String KEY_PREFFIX = "audio_bilibili_";
	
	private final static Pattern pattern = Pattern.compile("^(网易|qq|QQ|Qq|qQ|虾米|B站)(随机){0,1}点歌(.+)");
	private final static Pattern listPattern = Pattern.compile("^B站歌曲列表(\\d*)$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		Matcher listMatcher = listPattern.matcher(message);
		if(matcher.matches()) {
			String musicName = matcher.group(3);
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			if("网易".equals(matcher.group(1))) {
				Music163Result result = musicApiClient.music163Search(musicName, 0, 1, 1);
				if(result!=null&&"200".equals(result.code())&&result.result().songs().length>0) {
					long songId = result.result().songs()[0].id();
					
					//增加好感度
					int curFav = checkinCmd.modifyFav(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), increase);
					
					task.setMessage(CQUtil.music("163", ""+songId)+(curFav<0?"（绑定可以增加好感度哦喵~）":("增加"+increase+"点好感喵~，当前好感度:"+curFav)));
					return task;
				}
			}else if("B站".equals(matcher.group(1))) {
				String isRandom = matcher.group(2);
				CfgResource cr  = null;
				if(StringUtils.hasLength(isRandom)) {
					List<CfgResource> crs = cfgResourceMapper.findByKey(KEY_PREFFIX, matcher.group(3));
					if(crs!=null&&!crs.isEmpty()) {
						cr = crs.get(RandomUtil.randomInt(crs.size()));
					}
				}else {
					cr  = cfgResourceMapper.selectByKey(KEY_PREFFIX+matcher.group(3));
				}
				
				if(cr!=null) {
					task.setMessage(CQUtil.selfMusic(cr.getOriginResource(),cr.getCfgResource(), cr.getTitle(), cr.getContent(), cr.getImage()));
					
					//点歌成功，为创建者增加金币1
					if(cr.getCreateUserId()!=null) {
						checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), cr.getCreateUserId(), 1);
					}
					
					
					return task;
				}
				
			}
			
		}else if(listMatcher.matches()) {
			String pageNoStr = listMatcher.group(1);
			int pageNo = !StringUtils.hasLength(pageNoStr)?1:Integer.parseInt(pageNoStr);
			int offset = (pageNo-1)*musicListSize;
			List<CfgResource> musics = cfgResourceMapper.findBMusicLimit(offset, musicListSize);
			if(musics!=null&&!musics.isEmpty()) {
				StringBuffer listBuffer = new StringBuffer("当前B站歌曲分页："+(pageNo));
				listBuffer.append("\n");
				for(int index = 0;index<musics.size();index++) {
					listBuffer.append(index+1)
								.append(". 【")
								.append(musics.get(index).getCfgKey().substring(15))
								.append("】 by ")
								.append(musics.get(index).getCreateUserName())
								.append("\n"+StringUtil.SPLIT);
					
				}
				int maxPage = (cfgResourceMapper.countBMusic()-1)/musicListSize+1;
				
				if(maxPage>5) {
					//大于5，则中间以省略号展示
					listBuffer.append("[1] [2]···["+(maxPage-1)+"] ["+maxPage+"]");
				}else {
					//小于等于5，就全部展示了
					for(int i=1;i<=maxPage;i++) {
						listBuffer.append("["+i+"]");
						if(i<maxPage) {
							listBuffer.append(" ");
						}
					}
				}
				listBuffer.append("\n"+StringUtil.SPLIT_FOOT);
				listBuffer.append("发送 B站歌曲列表+[分页]即可查看可点歌曲喵~");
				GeneralTask task = new GeneralTask();
				task.setSelfQnum(selfQnum);
				task.setType(qmessage.getMessageType());
				task.setTargetId(qmessage.getGroupId());
				task.setMessage(listBuffer.toString());
				return task;
			}
		}
		
		return null;
	}

}
