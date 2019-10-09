package org.accen.dmzj.core.handler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.accen.dmzj.core.EventParser;
import org.accen.dmzj.core.annotation.HandlerChain;
import org.accen.dmzj.core.handler.cmd.CmdAdapter;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.dao.QmessageMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;

@HandlerChain(postType = "message")
public class GroupMessageEventhandler implements EventHandler{
	@Autowired
	private QmessageMapper qmessageMapper;
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	@Override
	public void handle(Map<String, Object> event) {
		if(EventParser.MESSAGE_TYPE_GROUP.equals(event.get("message_type"))
				&&EventParser.SUB_TYPE_NORMAL.equals(event.get("sub_type"))) {
			//生命周期开始
			List<GeneralTask> tasks = new LinkedList<GeneralTask>();
			//1.元数据记录
			Qmessage qmessage = new Qmessage();
			qmessage.setMessageType(EventParser.MESSAGE_TYPE_GROUP);
			qmessage.setSubType(EventParser.SUB_TYPE_NORMAL);
			qmessage.setMessageId(event.get("message_id").toString());
			qmessage.setGroupId(event.get("groupId").toString());
			qmessage.setUserId(event.get("user_id").toString());
			qmessage.setMessage(event.get("message").toString());
			qmessage.setRawMessage(event.get("raw_message").toString());
			qmessage.setSendTime(new Date());
			qmessage.setFont(event.get("font").toString());
			
			qmessageMapper.insert(qmessage);
			//2.自定义快速回复型（对确定的消息进行匹配并产生简要回复的任务）
			List<CfgQuickReply> replys = cfgQuickReplyMapper.queryByApply(2, event.get("groupId").toString());
			if(replys!=null&&!replys.isEmpty()) {
				replys.forEach(reply->{
					if((reply.getMatchType()==1&&reply.getPattern().equals(event.get("message")))//精确匹配
						||//模糊匹配
						(reply.getMatchType()==2&&Pattern.matches(reply.getPattern(),event.get("message").toString()))) {
						
						GeneralTask task = new GeneralTask();
						task.setSelfQnum(event.get("selfQnum").toString());
						task.setTargetId(event.get("groupId").toString());
						task.setType("message");
						task.setMessage(1==reply.getNeedAt()?CQUtil.at(event.get("user_id").toString()):""
							+reply.getReply());
						tasks.add(task);
					}
					
				});
			}
			//3.功能型（对系统功能进行操作，或对确定的消息匹配并产生复杂的回复的任务）
			Map<String, CmdAdapter> cmds = ApplicationContextUtil.getBeans(CmdAdapter.class); 
			for(String cmdName:cmds.keySet()) {
				tasks.add(cmds.get(cmdName).cmdAdapt(qmessage, event.get("selfQnum").toString()));
			}
			//4.监听型（匹配所有消息，但满足特定条件后产生复杂的回复的任务）
		}
	}
	
}
