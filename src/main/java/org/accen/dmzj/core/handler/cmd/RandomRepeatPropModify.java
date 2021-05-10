package org.accen.dmzj.core.handler.cmd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@FuncSwitch(title = "设置复读几率")
@Component
public class RandomRepeatPropModify implements CmdAdapter {

	private Map<String, Integer> groupProp = new HashMap<String, Integer>();//各个群组的随机几率
	
	@Autowired
	private CfgConfigValueMapper configMapper;
	private final static String CONFIG_KEY_PREFIX = "random_repeat_";
	@Value("${coolq.manager}")
	private String manager = "1339633536";//管理员qq
	private final static Pattern ctrlPattern = Pattern.compile("设置随机复读几率([1-5]?\\d)%");
	/**
	 * 当前某个群的复读几率
	 * @param groupId
	 * @return
	 */
	public int getRandomRepeatProp(String groupId) {
		if(groupProp.containsKey(groupId)) {
			return groupProp.get(groupId);
		}else {
			String key = CONFIG_KEY_PREFIX+groupId;
			CfgConfigValue repeat = configMapper.selectByTargetAndKey("group", groupId, key);
			if(repeat!=null) {
				groupProp.put(groupId, Integer.valueOf(repeat.getConfigValue()));
				return Integer.valueOf(repeat.getConfigValue());
			}else {
				return 0;
			}
		}
	}
	/**
	 * 设置复读几率
	 * @param groupId
	 * @param prop
	 */
	private void setRandomRepeatProp(String groupId,int prop) {
		String key = CONFIG_KEY_PREFIX+groupId;
		groupProp.put(groupId, prop);
		CfgConfigValue repeat = configMapper.selectByTargetAndKey("group", groupId, key);
		if(repeat!=null) {
			repeat.setConfigValue(""+prop);
			repeat.setUpdateTime(new Date());
			configMapper.updateValue(repeat);
		}else {
			repeat = new CfgConfigValue();
			repeat.setConfigKey(key);
			repeat.setTarget(groupId);repeat.setTargetType("group");
			repeat.setConfigValue(""+prop);
			configMapper.insert(repeat);
		}
	}
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String role = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("role");
		if((!manager.equals(qmessage.getUserId()))&&(!"owner".equals(role))&&(!"admin".equals(role))) {
			return null;
		}
		String message = qmessage.getMessage().trim();
		Matcher matcher = ctrlPattern.matcher(message);
		if(matcher.matches()) {
			setRandomRepeatProp(qmessage.getGroupId(), Integer.valueOf(matcher.group(1)));
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			task.setMessage("复读几率已经设置为"+matcher.group(1)+"%了喵~");
			return task;
		}
		return null;
	}

}
