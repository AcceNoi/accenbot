package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.MusicApiClient;
import org.accen.dmzj.core.task.api.vo.Music163Result;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

	private static final String KEY_PREFFIX = "audio_bilibili_";
	
	private final static Pattern pattern = Pattern.compile("^(网易|qq|QQ|Qq|qQ|虾米|B站)点歌(.+)");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			String musicName = matcher.group(2);
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			if("网易".equals(matcher.group(1))) {
				Music163Result result = musicApiClient.music163Search(musicName, 0, 1, 1);
				if(result!=null&&"200".equals(result.getCode())&&result.getResult().getSongs().length>0) {
					long songId = result.getResult().getSongs()[0].getId();
					
					//增加好感度
					int curFav = checkinCmd.modifyFav(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), increase);
					
					task.setMessage(CQUtil.music("163", ""+songId)+(curFav<0?"（绑定可以增加好感度哦喵~）":("增加"+increase+"点好感喵~，当前好感度:"+curFav)));
					return task;
				}
			}else if("B站".equals(matcher.group(1))) {
				CfgResource cr  = cfgResourceMapper.selectByKey(KEY_PREFFIX+matcher.group(2));
				if(cr!=null) {
					task.setMessage(CQUtil.selfMusic(cr.getCfgResource(), cr.getTitle(), cr.getContent(), ""));
					return task;
				}
				
			}
			
		}
		return null;
	}

}
