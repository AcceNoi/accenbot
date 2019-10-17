package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.LoliconApiClientPk;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetuCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "随机获取网上的一张p站图";
	}

	@Override
	public String example() {
		return "随机涩图";
	}
	@Autowired
	private LoliconApiClientPk loliconApiClientPk;

	private static final Pattern pattern = Pattern.compile("^随机(色图|瑟图|涩图)$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			String imageUrl = loliconApiClientPk.setu();
			if(imageUrl!=null) {
				GeneralTask task =  new GeneralTask();
				
				task.setSelfQnum(selfQnum);
				task.setType(qmessage.getMessageType());
				task.setTargetId(qmessage.getGroupId());
				task.setMessage(CQUtil.imageUrl(imageUrl));
				return task;
			}
			
			
			
		}
		return null;
	}

}
