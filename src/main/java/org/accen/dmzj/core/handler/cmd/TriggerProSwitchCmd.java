package org.accen.dmzj.core.handler.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@FuncSwitch("cmd_trigger")
@Component
public class TriggerProSwitchCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "设置触发词条几率";
	}

	@Override
	public String example() {
		return "设置几率50%";
	}

	
	private final static Pattern pattern = Pattern.compile("^设置几率([1-9]?\\d|100)%$");
	private Map<String, Integer> groupPro = new HashMap<String, Integer>();
	private final static String FUZZY_PRO = "FUZZY_PRO";
	@Autowired
	private CfgConfigValueMapper configMapper;
	//默认为70
	public int triggerPro(String groupId) {
//		return groupPro.containsKey(groupId)?groupPro.get(groupId):70;
		if(groupPro.get(groupId)==null) {
			CfgConfigValue config = configMapper.selectByTargetAndKey("group", groupId, FUZZY_PRO);
			if(config!=null) {
				groupPro.put(groupId, Integer.valueOf(config.getConfigValue()));
			}
		}
		return groupPro.containsKey(groupId)?groupPro.get(groupId):70;
		
	}
	private void putPro(String groupId,int pro) {
		groupPro.put(groupId, pro);
		CfgConfigValue config = configMapper.selectByTargetAndKey("group", groupId, FUZZY_PRO);
		if(config!=null) {
			config.setConfigValue(""+pro);
			configMapper.updateValue(config);
		}else {
			config = new CfgConfigValue();
			config.setTargetType("group");config.setTarget(groupId);config.setConfigKey(FUZZY_PRO);config.setConfigValue(""+pro);
			configMapper.insert(config);
		}
	}
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			Integer pro = Integer.valueOf(matcher.group(1));
//			groupPro.put(qmessage.getGroupId(), pro);
			putPro(qmessage.getGroupId(), pro);
			
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			task.setMessage(CQUtil.at(qmessage.getUserId())+"词条触发概率已设置为"+pro+"%了喵~");
			return task;
		}
		return null;
	}

}
