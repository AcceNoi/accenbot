package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;
import org.springframework.stereotype.Component;

import feign.Headers;
import feign.RequestLine;

@FeignApi(host="http://localhost:5700",maxPeriod = 15000,maxAttempts = 1)
@Headers({"Content-Type: application/json","Accept: application/json","Authorization: Bearer kSLuTF2GC2Q4q4ugm3"})
@Component
public interface CqhttpClient {
	/**
	 * 发送群组消息 
	 * @param body
	 * <pre> {
	 * group_id
	 * message
	 * auto_escape
	 * }
	 * </pre>
	 * @return
	 */
	@RequestLine("POST /send_group_msg")
	public Map<String,Object> sendGroupMsg(Map<String, Object> body);
	/**
	 * 群组禁言
	 * @param body
	 * <pre>{
	 * group_id
	 * user_id
	 * duration 禁言时长 s
	 * }
	 * </pre>
	 * @return
	 */
	@RequestLine("POST /set_group_ban")
	public Map<String,Object> setGroupBan(Map<String,Object> body);
	/**
	 * 获取图片
	 * @param body
	 * <pre>{
	 * file cq file参数
	 * }
	 * </pre>
	 * @return
	 */
	@RequestLine("POST /get_image")
	public Map<String,Object> getImage(Map<String,Object> body);
}
