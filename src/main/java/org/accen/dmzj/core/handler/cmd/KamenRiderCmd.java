package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class KamenRiderCmd implements CmdAdapter{

	@Autowired
	private TaskManager taskManager;
	
	@Autowired
	private CfgResourceMapper cfgResourceMapper;
	
	private static final String KEY_PREFFIX = "record_kamenrider_";
	private static final Pattern pattern = Pattern.compile("^(假面骑士|卡面来打|仮面ライダー)(.*)?(变身|henshin|Henshin|変身).*");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			
			taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), "変身！");
			
			GeneralTask task =  new GeneralTask();
		
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			String riderName = matcher.group(2).trim();
			String key = KEY_PREFFIX+riderName;
			CfgResource cr = cfgResourceMapper.selectByKey(key);
			if(cr==null) {
				//如果找不到，则发送失败音效
				key = KEY_PREFFIX+"Error";
				cr = cfgResourceMapper.selectByKey(key);
			}
			task.setMessage(CQUtil.recordUrl(cr.getCfgResource()));
			return task;
		}
		return null;
	}

}
