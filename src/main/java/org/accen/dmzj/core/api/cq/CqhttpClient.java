package org.accen.dmzj.core.api.cq;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	/**
	 * 获取群成员信息
	 * @param groupId
	 * @param userId
	 * @param noCache
	 * @return
	 */
	@GetMapping(value="/get_group_member_info",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> getGroupMemberInfo(@RequestParam("group_id")long groupId,@RequestParam("user_id")long userId,@RequestParam("no_cache")boolean noCache);
	
	/**
	 * 获取群信息
	 * @param groupId
	 * @param noCache
	 * @return
	 */
	@GetMapping(value="/get_group_info",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> getGroupInfo(@RequestParam("group_id")long groupId,@RequestParam("no_cache")boolean noCache);
	
	/**
	 * 发送私聊消息
	 * @param userId
	 * @param message
	 * @param autoEscape
	 * @return
	 */
	@GetMapping(value="/send_private_msg",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> sendPrivateMsg(@RequestParam("user_id")long userId,@RequestParam("message")String message,@RequestParam("auto_escape")String autoEscape);
	
	/**
	 * 撤回消息
	 * @param messageId
	 * @return
	 */
	@GetMapping(value="/delete_msg",headers= {"Content-Type: application/json","Accept: application/json"})
	public void deleteMsg(@RequestParam("message_id")int messageId);
	
	/**
	 * 获取消息
	 * @param messageId
	 * @return
	 */
	@GetMapping(value="/get_msg",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> getMsg(@RequestParam("message_id")int messageId);
	
	/**
	 *  发送好友赞
	 * @param userId
	 * @param times
	 */
	@GetMapping(value="/send_like",headers= {"Content-Type: application/json","Accept: application/json"})
	public void sendLike(@RequestParam("user_id")long userId,@RequestParam("times")int times);
	
	/**
	 * 处理加好友请求
	 * @param flag
	 * @param approve
	 * @param remark
	 */
	@GetMapping(value="/set_friend_add_request",headers= {"Content-Type: application/json","Accept: application/json"})
	public void setFriendAddRequest(@RequestParam("flag")String flag,@RequestParam("approve")boolean approve,@RequestParam("remark")String remark);

	/**
	 * 处理加群请求／邀请
	 * @param flag
	 * @param subType
	 * @param approve
	 * @param reason
	 */
	@GetMapping(value="/set_group_add_request",headers= {"Content-Type: application/json","Accept: application/json"})
	public void setGroupAddRequest(@RequestParam("flag")String flag,@RequestParam("sub_type")String subType,@RequestParam("approve")boolean approve,@RequestParam("reason")String reason);
	
	/**
	 * 获取陌生人信息
	 * @param userId
	 * @param noCache
	 * @return
	 */
	@GetMapping(value="/get_stranger_info",headers= {"Content-Type: application/json","Accept: application/json"})
	public Map<String,Object> getStrangerInfo(@RequestParam("user_id")long userId,@RequestParam("no_cache")boolean noCache);
	
}
