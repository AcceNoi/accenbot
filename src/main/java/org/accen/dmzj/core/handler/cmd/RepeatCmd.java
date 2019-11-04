package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.stereotype.Component;
@FuncSwitch("cmd_repeat")
@Component
public class RepeatCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "重复消息";
	}

	@Override
	public String example() {
		return "说debu!";
	}

	private final static Pattern pattern = Pattern.compile("^老婆说(.+)");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task =  new GeneralTask();
			
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			task.setMessage(matcher.group(1).replaceAll("我", "##").replaceAll("你", "我").replaceAll("##", "你"));
			return task;
		}
		return null;
	}

}
