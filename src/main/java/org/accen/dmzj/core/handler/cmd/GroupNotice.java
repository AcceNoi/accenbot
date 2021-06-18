package org.accen.dmzj.core.handler.cmd;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.CmdNotice;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.api.QqNickApiClient;
import org.accen.dmzj.core.meta.NoticeType;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GroupNotice{
	/**
	 * 进群的配置key
	 */
	public static final String REPLY_GROUP_INCREASE = "reply_group_increase";
	private static List<String> REPLY_GROUP_DECREASE_VALUES = List.of("精神失常","精神错乱","去泰国做变性手术","去迪拜买房"
			,"感冒","憋尿","天梯十年跪","十连无货","打不过我","掉线","看不懂群友说话","看本子过多","要画本子","守护bilibili");
	
	@Autowired
	private CfgConfigValueMapper configMapper;
	@Autowired
	private QqNickApiClient qqNickApiClient;
	
	@CmdNotice(noticeType = NoticeType.GROUP_INCREASE)
	@GeneralMessage
	public String increase(@AutowiredParam long userId,@AutowiredParam long groupId) {
		//新人加群
		CfgConfigValue config = configMapper.selectByTargetAndKey("group", ""+groupId, REPLY_GROUP_INCREASE);
		if(config!=null&&StringUtils.hasLength(config.getConfigValue())) {
			return CQUtil.at(""+userId)+" "+config.getConfigValue();
		}
		return null;
	}
	@CmdNotice(noticeType = NoticeType.GROUP_DECREASE)
	@GeneralMessage
	public String decrease(@AutowiredParam long userId) {
		String reply = REPLY_GROUP_DECREASE_VALUES.get(RandomUtil.randomInt(REPLY_GROUP_DECREASE_VALUES.size()));
		String nick = (String) qqNickApiClient.qqNick(""+userId).get("nick");
		String nickDecoded = URLDecoder.decode(nick, Charset.forName("UTF-8"));
		return nickDecoded+"因为"+reply+"离开本群";
	}

}
