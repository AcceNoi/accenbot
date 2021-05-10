package org.accen.dmzj.core.handler.listen;

import java.util.List;
import java.util.Set;

import org.accen.dmzj.core.handler.cmd.ChaosMode;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.QmessageUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class ChaosListener implements ListenAdpter{
	@Autowired
	private ChaosMode chaosModeCmd;
	@Autowired
	private QmessageUtil qmessageUtil;
	@Value("${coolq.chaosmod.prop:0.05}")
	private float prop;
	@Autowired
	private TaskManager taskManager;
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		Set<String> groups = qmessageUtil.groupList();
		groups.remove(qmessage.getGroupId());
		if(groups.size()>0 && RandomUtil.randomPass(prop)) {
			String group = (String) (groups.toArray())[RandomUtil.randomInt(groups.size())];
			if(chaosModeCmd.modeOpen(group)) {
				taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), group, qmessage.getMessage());
			}
		}
		return null;
	}

}
