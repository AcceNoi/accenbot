package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.MusicApiClient;
import org.accen.dmzj.core.task.api.vo.Music163Result;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MusicShareCmd implements CmdAdapter {

	@Autowired
	private MusicApiClient musicApiClient;
	
	@Override
	public String describe() {
		return "分享歌曲";
	}

	@Override
	public String example() {
		return "[网易|qq|虾米]点歌 恋爱循环";
	}

	private final static Pattern pattern = Pattern.compile("^(网易|qq|QQ|Qq|qQ|虾米)点歌(.+)");
	
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
					
					task.setMessage(CQUtil.music("163", ""+songId));
					return task;
				}
			}
			
		}
		return null;
	}

}
