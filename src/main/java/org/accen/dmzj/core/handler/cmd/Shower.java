package org.accen.dmzj.core.handler.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.annotation.FuncSwitchGroup;
import org.accen.dmzj.core.handler.CmdShower;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Shower implements CmdAdapter{

	@Autowired
	private CmdShower cmdShower;
	
	private static final String SIGN = "/";
	private Set<String> menuSign = Set.of("菜单","功能","Menu","MENU","menu");
	
	private final Pattern showerPattern = Pattern.compile(" *"+SIGN+" *(.*) *$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		Matcher showerMatcher = showerPattern.matcher(qmessage.getMessage());
		if(showerMatcher.matches()) {
			String sign = showerMatcher.group(1);
			if(menuSign.contains(sign)) {
				return new GeneralTask(qmessage.getMessageType(), qmessage.getGroupId(), printMenu(), selfQnum);
			}else {
				for(FuncSwitchGroup group:cmdShower.getCmds().keySet()) {
					if(group.title().equals(sign)||Arrays.stream(group.matchSigns()).anyMatch(matchSign->sign.equals(matchSign))) {
						return new GeneralTask(qmessage.getMessageType(), qmessage.getGroupId(), printFuncGroup(group), selfQnum);
					}
				}
			}
		}
		return null;
	}
	
	protected String printMenu() {
		List<FuncSwitchGroup> groups = cmdShower.getCmdGroups();
		StringBuffer menuBuffer = new StringBuffer();
		menuBuffer.append("#菜单#\n");
		menuBuffer.append("--------------------\n");
		for(int index = 0;index<groups.size();index++) {
			menuBuffer.append(index+1)
			.append(". /")
			.append(groups.get(index).title())
			.append("\n");
			menuBuffer.append("--------------------\n");
		}
		return menuBuffer.toString();
	}
	protected String printFuncGroup(FuncSwitchGroup group) {
		List<FuncSwitch> cmds = cmdShower.getCmds().get(group);
		StringBuffer groupBuffer = new StringBuffer();
		groupBuffer.append("#").append(group.title()).append("#\n");
		groupBuffer.append("--------------------\n");
		for(int index = 0;index<cmds.size();index++) {
			groupBuffer.append(index+1)
					.append(". ")
					.append(cmds.get(index).title())
					.append(">>>发送【")
					.append(cmds.get(index).format())
					.append("】")
					.append("\n")
					.append("--------------------\n");
		}
		return groupBuffer.toString();
	}
}
