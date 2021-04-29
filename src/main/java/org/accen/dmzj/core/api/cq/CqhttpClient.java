package org.accen.dmzj.core.api.cq;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @see <a href="https://github.com/howmanybots/onebot">OneBot 标准</a>
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignClient(
		name="cqhttp",
		url="http://localhost:5700",
		configuration = CqhttpConfiguration.class)
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
	@PostMapping(value="/send_group_msg",headers= {"Content-Type: application/json","Accept: application/json"})
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
	@PostMapping(value="/set_group_ban",headers= {"Content-Type: application/json","Accept: application/json"})
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
	@PostMapping(value="/get_image",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> getImage(Map<String,Object> body);
	/**
	 * 获取群列表
	 * @return
	 */
	@GetMapping(value="/get_group_list",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> groupList();
}
