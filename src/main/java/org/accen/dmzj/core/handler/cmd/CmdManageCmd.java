package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class CmdManageCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "展示所有的功能";
	}

	@Override
	public String example() {
		return "";
	}

	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		
		initCmds();
		
		String message = qmessage.getMessage().trim();
		if("功能".equals(message)) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			task.setMessage(
					cmds.stream()
						.map(cmd->cmds.indexOf(cmd)+1+"."+cmd.describe()+"示例："+cmd.example())
						.collect(Collectors.joining("\n")));
			return task;
		}
		return null;
		
	}
	private List<CmdAdapter> cmds = null;
	/**
	 * 初始化功能列表，但要排除自身
	 */
	private void initCmds() {
		if(cmds!=null) {
			synchronized (cmds) {
				if(cmds!=null) {
					Map<String, CmdAdapter> cmdMap = ApplicationContextUtil.getBeans(CmdAdapter.class);
					cmds = cmdMap.values().stream()
						.filter(cmd -> cmd.getClass()!=CmdManageCmd.class)
						.collect(Collectors.toList());
				}
			}
		}
	}
}
