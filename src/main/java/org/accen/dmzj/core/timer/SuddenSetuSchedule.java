package org.accen.dmzj.core.timer;

import org.accen.dmzj.core.handler.GroupMessageEventhandler;
import org.accen.dmzj.core.handler.cmd.Setu;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.QmessageUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class SuddenSetuSchedule {
	@Autowired
	private QmessageUtil qmessageUtil;
	@Autowired
	private GroupMessageEventhandler groupMessageEventhandler;
	@Autowired
	private Setu setuCmd;
	@Value("${coolq.bot}")
	private String botId;
	@Autowired
	private TaskManager taskManager;
	@Scheduled(cron = "0 0/10 9,10,11,12,14,15,16,17,18,19,20,21,22,23 * * *")
	public void suddenSetu() {
		//随机找一个群
		String[] groupArr = qmessageUtil.groupList().toArray(String[]::new);
		String groupId = groupArr[RandomUtil.randomInt(groupArr.length)];
		if(groupMessageEventhandler.isActiveGroup(groupId)) {
			//伪造一个qmessage
			Qmessage qmessage = new Qmessage();
			qmessage.setMessage("随机涩图");qmessage.setMessageType("group");qmessage.setGroupId(groupId);
			
			GeneralTask task = setuCmd.cmdAdapt(qmessage, botId);
			if(task!=null) {
				taskManager.addGeneralTask(task);
			}
		}
	}
}
