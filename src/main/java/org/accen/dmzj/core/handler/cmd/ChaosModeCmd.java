package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.ModeCmd;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.QmessageUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@FuncSwitch("cmd_chaos")
@Component
public class ChaosModeCmd extends ModeCmd implements CmdAdapter {

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
	private static final Pattern switchPattern = Pattern.compile("^(开启|关闭)混乱模式$");
	@Autowired
	private QmessageUtil qmessageUtil;
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		if(qmessageUtil.isManagerOrGroupManagerOrGroupOwner(qmessage)) {
			Matcher matcher = switchPattern.matcher(qmessage.getMessage());
			if(matcher.matches()) {
				GeneralTask task = new GeneralTask();
				task.setSelfQnum(selfQnum);
				task.setType(qmessage.getMessageType());
				task.setTargetId(qmessage.getGroupId());
				String func = matcher.group(1);
				if("开启".equals(func)) {
					addGroup(qmessage.getGroupId());
					task.setMessage("CHAOS mode set up!");
				}else if("关闭".equals(func)){
					removeGroup(qmessage.getGroupId());
					task.setMessage("CHAOS mode shutdown!");
				}
				return task;
			}
		}
		return null;
	}

	@Override
	public boolean modeOpen(String groupId) {
		return super.hasGroup(groupId);
	}

}
