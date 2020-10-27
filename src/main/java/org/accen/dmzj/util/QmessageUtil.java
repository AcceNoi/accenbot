package org.accen.dmzj.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.api.CqhttpClient;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class QmessageUtil {
	@Value("${coolq.manager}")
	public String manager = "1339633536";//管理员qq
	@Autowired
	private CqhttpClient cqhttpClient;
	/**
	 * 发送者是qq管理员
	 * @param qmessage
	 * @return
	 */
	public boolean isManager(Qmessage qmessage) {
		return manager.equals(qmessage.getUserId());
	}
	/**
	 * 发送者是群管理员
	 * @param qmessage
	 * @return
	 */
	public boolean isGroupManager(Qmessage qmessage) {
		String role = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("role");
		return "admin".equals(role);
	}
	/**
	 * 发送者是群主
	 * @param qmessage
	 * @return
	 */
	public boolean isGroupOwner(Qmessage qmessage) {
		String role = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("role");
		return "owner".equals(role);
	}
	public boolean isManagerOrGroupManagerOrGroupOwner(Qmessage qmessage) {
		return isManager(qmessage)||isGroupManager(qmessage)||isGroupOwner(qmessage);
	}
	/**
	 * 获取当前bot的群列表
	 * @return
	 */
	public Set<String> groupList(){
		List<Map<String, Object>> groupListInfo = (List<Map<String, Object>>) cqhttpClient.groupList().get("data");
		return groupListInfo.parallelStream()
								.map(map->new BigDecimal((Double)map.get("group_id")).stripTrailingZeros().toPlainString())
								.collect(Collectors.toSet());
	}
}
