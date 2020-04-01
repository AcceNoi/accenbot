package org.accen.dmzj.core.handler.listen;

import java.util.List;
import java.util.Map;

import org.accen.dmzj.core.handler.cmd.RandomRepeatPropModifyCmd;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class RandomRepeatListener implements ListenAdpter {
	@Autowired
	private RandomRepeatPropModifyCmd randomRepeatPropModifyCmd;
	@Autowired
	private TaskManager taskManager;
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		int prop = randomRepeatPropModifyCmd.getRandomRepeatProp(qmessage.getGroupId());
		if(RandomUtil.randomInt(100)<prop) {
			taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), modifyRepeat(qmessage) );
		}
		return null;
	}
	
	private String modifyRepeat(Qmessage qmessage) {
		String createNickName = (String) ((Map<String, Object>)qmessage.getEvent().get("sender")).get("nickname");
		String createCard = (String) ((Map<String, Object>)qmessage.getEvent().get("sender")).get("card");//群名片
		String newMsg = qmessage.getMessage();
		newMsg = newMsg.replaceAll("我", "##").replaceAll("你", "我").replaceAll("##", "你");
		return newMsg.replaceAll("(狐狸|bot)", StringUtils.isEmpty(createCard)?createNickName:createCard);
	}
}
