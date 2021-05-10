package org.accen.dmzj.core.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.cmd.CmdAdapter;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CmdManager{
	/**
	 * 处理事件的功能
	 */
	@Autowired
	private List<CmdAdapter> allCmds;
	@Autowired
	private FuncSwitchUtil funcSwitchUtil;
	@Autowired
	private TaskManager taskManager;
	/**
	 * 受{@link FuncSwitch}控制的功能
	 */
	Map<String, CmdAdapter> allShowableCmds = new HashMap<>(16);
	public void accept(Qmessage qmessage) {
		allCmds.forEach(cmd->{
			if(funcSwitchUtil.isCmdPass(cmd.getClass(), qmessage.getMessageType(), qmessage.getGroupId())) {
				taskManager.addGeneralTask(cmd.cmdAdapt(qmessage, qmessage.getEvent().get("selfQnum").toString()));
			}
		});
	}
}
