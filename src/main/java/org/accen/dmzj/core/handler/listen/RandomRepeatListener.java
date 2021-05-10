package org.accen.dmzj.core.handler.listen;

import java.util.List;
import java.util.Map;

import org.accen.dmzj.core.handler.cmd.RandomRepeatPropModify;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class RandomRepeatListener implements ListenAdpter {
	@Autowired
	private RandomRepeatPropModify randomRepeatPropModifyCmd;
	@Autowired
	private TaskManager taskManager;
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		int prop = randomRepeatPropModifyCmd.getRandomRepeatProp(qmessage.getGroupId());
		if(RandomUtil.randomInt(100)<prop) {
			String modifiedMsg = modifyRepeat(qmessage);
			if(modifiedMsg!=null) {
				taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), modifiedMsg);	
			}
			
		}
		return null;
	}
	private final static String[] rdmOralHabits = new String[] {"有一说一，",
																"听我说，老伙计，",
																"哦!上帝！",
																"我跟你打赌，",
																"向圣母玛利亚起誓，",
																"嘿，没错，",
																"真该死，",
																"我发誓，",
																"该死，真是活见鬼，",
																"噢，上帝作证，"};
	private String modifyRepeat(Qmessage qmessage) {
		
		if(!CQUtil.hasImg(qmessage.getMessage())) {
			//现在不会复读含图片的信息
			String createNickName = (String) ((Map<String, Object>)qmessage.getEvent().get("sender")).get("nickname");
			String createCard = (String) ((Map<String, Object>)qmessage.getEvent().get("sender")).get("card");//群名片
			String newMsg = qmessage.getMessage();
			
			newMsg = newMsg.replaceAll("(我|咱|俺)", "##");
			newMsg = newMsg.replaceAll("(Bot|狐狸|bot|你)", StringUtils.isEmpty(createCard)?createNickName:createCard);
			newMsg = newMsg.replaceAll("##", "你");
			//随机加个前缀
			if(RandomUtil.randomPass(0.5)) {
				newMsg = rdmOralHabits[RandomUtil.randomInt(rdmOralHabits.length)]+newMsg;
			}
			return newMsg;
		}else {
			return null;
		}
		
	}
}
