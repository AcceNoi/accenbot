package org.accen.dmzj.core.handler;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.accen.dmzj.core.annotation.HandlerChain;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.task.api.QqNickApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@HandlerChain(postType = "notice")
public class NoticeEventHandler implements EventHandler{

	/**
	 * 群成员增加
	 */
	private static final String NOTICE_TYPE_GROUP_INCREASE="group_increase";
	/**
	 * 退群
	 */
	private static final String NOTICE_TYPE_GROUP_DECREASE="group_decrease";
	/**
	 * 进群的配置key
	 */
	public static final String REPLY_GROUP_INCREASE = "reply_group_increase";
	private static List<String> REPLY_GROUP_DECREASE_VALUES = List.of("精神失常","精神错乱","去泰国做变性手术","去迪拜买房"
			,"感冒","憋尿","天梯十年跪","十连无货","打不过我","掉线","看不懂群友说话","看本子过多","要画本子","守护bilibili");
	
	@Autowired
	private CfgConfigValueMapper configMapper;
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private QqNickApiClient qqNickApiClient;
	@Override
	public void handle(Map<String, Object> event) {
		String noticeType = event.get("notice_type").toString();
		if(NOTICE_TYPE_GROUP_INCREASE.equals(noticeType)) {
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
		}else if(NOTICE_TYPE_GROUP_DECREASE.equals(noticeType)){
			//随机一个退群理由
			String reply = REPLY_GROUP_DECREASE_VALUES.get(RandomUtil.randomInt(REPLY_GROUP_DECREASE_VALUES.size()));
			String nick = (String) qqNickApiClient.qqNick(new BigDecimal((Double)event.get("user_id")).stripTrailingZeros().toPlainString()).get("nick");
			String nickDecoded = URLDecoder.decode(nick, Charset.forName("UTF-8"));
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(event.get("selfQnum").toString());
			task.setTargetId(new BigDecimal((Double)event.get("group_id")).stripTrailingZeros().toPlainString());
			task.setType("group");
			task.setMessage(nickDecoded+"因为"+reply+"离开本群");
			taskManager.addGeneralTask(task);
		}
	}

}
