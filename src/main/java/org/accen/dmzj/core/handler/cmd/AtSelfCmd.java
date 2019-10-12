package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.stereotype.Component;
@Component
public class AtSelfCmd implements CmdAdapter {

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String example() {
		// TODO Auto-generated method stub
		return null;
	}

	private final static Pattern pattern = Pattern.compile("^\\[CQ:at,qq=(\\d+)\\]$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			if(selfQnum.equals(matcher.group(1))) {
				GeneralTask task =  new GeneralTask();
				
				task.setSelfQnum(selfQnum);
				task.setType(qmessage.getMessageType());
				task.setTargetId(qmessage.getGroupId());
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 干嘛？");
				return task;
			}
		}
		return null;
	}

}
