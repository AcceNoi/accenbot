package org.accen.dmzj.core.handler.cmd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.handler.NoticeEventHandler;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SetWelcomCmd implements CmdAdapter {

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
	@Autowired
	private CfgConfigValueMapper configMapper;
	@Value("${coolq.welcom.maxlength:15}")
	private int welcomLength;
	private static final Pattern pattern = Pattern.compile("^设置欢迎词(.+)");
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
			
			String wlcm = matcher.group(1);
			if(wlcm.length()>welcomLength) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 都说了欢迎词不要超过"+welcomLength+"个字啦喵！！再这样就不理你了喵~");
			}else {
				CfgConfigValue configo = configMapper.selectByTargetAndKey("group",qmessage.getGroupId(), NoticeEventHandler.REPLY_GROUP_INCREASE);
				if(configo==null) {
					CfgConfigValue config = new CfgConfigValue();
					config.setTargetType(qmessage.getMessageType());
					config.setTarget(qmessage.getGroupId());
					config.setConfigKey(NoticeEventHandler.REPLY_GROUP_INCREASE);
					config.setConfigValue(wlcm);
					configMapper.insert(config);
				}else {
					configo.setConfigValue(wlcm);
					configo.setUpdateTime(new Date());
					configo.setUpdateUserId(qmessage.getUserId());
					configMapper.updateValue(configo);
				}
				task.setMessage("设置欢迎词成功！赶紧找个人退群加群试试吧喵！");
			}
			return task;
		}
		return null;
	}

}
