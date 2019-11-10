package org.accen.dmzj.core.handler;

import java.math.BigDecimal;
import java.util.Map;

import org.accen.dmzj.core.annotation.HandlerChain;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@HandlerChain(postType = "notice")
public class NoticeEventHandler implements EventHandler{

	/**
	 * 群成员增加
	 */
	private static final String NOTICE_TYPE="group_increase";
	/**
	 * 进群的配置key
	 */
	public static final String REPLY_GROUP_INCREASE = "reply_group_increase";
	
	@Autowired
	private CfgConfigValueMapper configMapper;
	@Autowired
	private TaskManager taskManager;
	@Override
	public void handle(Map<String, Object> event) {
		String noticeType = event.get("notice_type").toString();
		if(NOTICE_TYPE.equals(noticeType)) {
			//新人加群
			CfgConfigValue config = configMapper.selectByTargetAndKey("group", new BigDecimal((Double)event.get("group_id")).stripTrailingZeros().toPlainString(), REPLY_GROUP_INCREASE);
			if(config!=null&&!StringUtils.isEmpty(config.getConfigValue())) {
				String userId = new BigDecimal((Double)event.get("user_id")).stripTrailingZeros().toPlainString();
				
				GeneralTask task = new GeneralTask();
				task.setSelfQnum(event.get("selfQnum").toString());
				task.setTargetId(new BigDecimal((Double)event.get("group_id")).stripTrailingZeros().toPlainString());
				task.setType("group");
				task.setMessage(CQUtil.at(userId)+" "+config.getConfigValue());
				taskManager.addGeneralTask(task);
			}
		}
	}

}
