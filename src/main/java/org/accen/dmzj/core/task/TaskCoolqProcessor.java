package org.accen.dmzj.core.task;

import java.util.HashMap;
import java.util.Map;

import org.accen.dmzj.core.task.api.CqhttpClient;
import org.accen.dmzj.util.ApplicationContextUtil;

public class TaskCoolqProcessor {
	public String processs(GeneralTask task) {
		return "";
	}
	public Map<String, Object> sendGroupMsg(String groupId,String message,boolean autoEscape) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("group_id", groupId);
		request.put("message", message);
		request.put("auto_escape", autoEscape);
		CqhttpClient client = ApplicationContextUtil.getBean(CqhttpClient.class);
		return client.sendGroupMsg(request);
	}
}
