package org.accen.dmzj.core.handler.cmd;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@FuncSwitch("cmd_repeat_mode")
@Component
public class RepeatModeSwitchCmd implements CmdAdapter {

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

	@Value("${coolq.manager}")
	private String manager = "1339633536";//管理员qq
	
	//只保存开启复读模式的，也就是说默认不开启复读
	private Set<String> allowGroup = new HashSet<String>(4);
	/**
	 * 判断一个群是否开启复读模式
	 * @param groupId
	 * @return
	 */
	public boolean modeOpen(String groupId) {
//		return allowGroup.contains(groupId);
		return true;
	}
	
	private static final Pattern pattern = Pattern.compile("^(开启|关闭)复读模式$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String role = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("role");
		if((!manager.equals(qmessage.getUserId()))&&(!"owner".equals(role))&&(!"admin".equals(role))) {
			return null;
		}
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			String type = matcher.group(1);
			if("开启".equals(type)) {
				allowGroup.add(qmessage.getGroupId());
				task.setMessage("复读模式开启！我也是复读机喵~");
			}else if("关闭".equals(type)) {
				allowGroup.remove(qmessage.getGroupId());
				task.setMessage("复读模式关闭！狗头保命喵~（doge");
			}
			return task;
		}
		return null;
	}

}
