package org.accen.dmzj.core.handler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.CmdMessage;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.api.cq.CqHttpConfigurationProperties;
import org.accen.dmzj.core.handler.callbacker.CallbackManager;
import org.accen.dmzj.core.handler.cmd.TriggerProSwitchCmd;
import org.accen.dmzj.core.handler.listen.ListenAdpter;
import org.accen.dmzj.core.meta.MessageType;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 适应原有GroupMessageEventHandler
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.1
 */
@CmdMessage(value="group_message_event_handler",messageType = MessageType.GROUP)
@Component
public class GroupMessageEventHandlerAdpter {
	@Autowired
	CqHttpConfigurationProperties cqProp;
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	@Autowired
	TaskManager taskManager;
	@Autowired
	CallbackManager callbackManager;
	@Autowired
	private TriggerProSwitchCmd tpsc;
	@Autowired
	private CmdManager cmdManager;
	/**
	 * 不活跃的群组
	 */
	private Set<Long> noActiveGroup = new HashSet<Long>();
	
	
	private Set<String> adminRoles = Set.of("owner","admin");
	@GeneralMessage
	public String execute(@AutowiredParam(".") Map<String, Object> event
			,@AutowiredParam(".message") String message
			,@AutowiredParam(".group_id") long groupId
			,@AutowiredParam(".self_id") long selfId
			,@AutowiredParam(".sender.role") String role
			,@AutowiredParam(".sender.user_id") long userId) {
		if(adminRoles.contains(role)||(cqProp.adminId() !=null && cqProp.adminId().contains(userId))) {
			if("召唤".equals(message)) {
				if(noActiveGroup.contains(groupId)) {
					noActiveGroup.remove(groupId);
					return "冲喵！";
				}else {
					return "已冲喵！";
				}
			}else if("去面壁".equals(message)) {
				noActiveGroup.add(groupId);
				return "面壁中！";
			}
		}
		if(noActiveGroup.contains(groupId)) {
			return null;
		}
		if(RandomUtil.randomPass(((double)tpsc.triggerPro(""+groupId))/100)) {
			CfgQuickReply aReply = cfgQuickReplyMapper.queryByApplyRandom(2, ""+groupId, 1, message.trim());
			if(aReply!=null) {
				//匹配到精确词条
				return (1==aReply.getNeedAt()?CQUtil.at(""+userId):"")
					+aReply.getReply();
			}else {
				//没有精确词条，则取找模糊词条
				CfgQuickReply fReply = cfgQuickReplyMapper.queryByApplyRandom(2, ""+groupId, 2, message.trim());
				if(fReply!=null) {
					//模糊匹配到了
					return (1==fReply.getNeedAt()?CQUtil.at(""+userId):"")
						+fReply.getReply();
				}
			}
		}
		Qmessage qmessage = new Qmessage();
		qmessage.setMessageType("group");
		qmessage.setSubType("normal");
		qmessage.setMessageId(((Integer)event.get("message_id")).toString());
		qmessage.setGroupId(""+groupId);
		qmessage.setUserId(""+userId);
		qmessage.setMessage(message);
		qmessage.setSendTime(new Date());
		qmessage.setEvent(event);
		cmdManager.accept(qmessage);
		//4.监听型（匹配所有消息，但满足特定条件后产生复杂的回复的任务）
		Map<String,ListenAdpter> listens = ApplicationContextUtil.getBeans(ListenAdpter.class);
		for(String listenName:listens.keySet()) {
			ListenAdpter listen = listens.get(listenName);
			List<GeneralTask> rs = listen.listen(qmessage, ""+selfId);
			if(rs!=null) {
				taskManager.addGeneralTasks(rs);
			}
			
		}
		//4.5 回调监听型
		
		callbackManager.accept(qmessage);
		return null;
	}
	public boolean isActiveGroup(Long groupId) {
		return !noActiveGroup.contains(groupId);
	}
}
