package org.accen.dmzj.core.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.EventParser;
import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.annotation.HandlerChain;
import org.accen.dmzj.core.handler.callbacker.CallbackListener;
import org.accen.dmzj.core.handler.callbacker.CallbackManager;
import org.accen.dmzj.core.handler.cmd.CmdAdapter;
import org.accen.dmzj.core.handler.cmd.TriggerProSwitchCmd;
import org.accen.dmzj.core.handler.listen.ListenAdpter;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.dao.QmessageMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@HandlerChain(postType = "message")
public class GroupMessageEventhandler implements EventHandler{
	@Autowired
	private QmessageMapper qmessageMapper;
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private CallbackManager callbackManager;
	@Autowired
	private FuncSwitchUtil funcSwitchUtil;
	
	@Autowired
	private TriggerProSwitchCmd tpsc;
	private static final Random random = new Random();
	
	@Value("${coolq.manager}")
	private String manager = "1339633536" ;//管理员qq
	//不活跃的群组，即面壁中的
	private Set<String> noActiveGroup = new HashSet<String>();
	private static final String startSign = "召唤";
	private static final String endSign = "去面壁";
	public boolean isActiveGroup(String group) {
		return !noActiveGroup.contains(group);
	}
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
			qmessage.setMessageId(new BigDecimal((Double)event.get("message_id")).stripTrailingZeros().toPlainString());
			qmessage.setGroupId(new BigDecimal((Double)event.get("group_id")).stripTrailingZeros().toPlainString());
			qmessage.setUserId(new BigDecimal((Double)event.get("user_id")).stripTrailingZeros().toPlainString());
			qmessage.setMessage(event.get("message").toString());
			qmessage.setRawMessage(event.get("raw_message").toString());
			qmessage.setSendTime(new Date());
			qmessage.setFont(new BigDecimal((Double)event.get("font")).stripTrailingZeros().toPlainString());
			qmessage.setEvent(event);
//			qmessageMapper.insert(qmessage);
			
			//1.1是否为管理员
			String role = (String) ((Map<String,Object>)event.get("sender")).get("role");
			if(manager.equals(qmessage.getUserId())||"owner".equals(role)||"admin".equals(role)) {
				if(startSign.equals(qmessage.getMessage())) {
					GeneralTask task = new GeneralTask();
					task.setSelfQnum(event.get("selfQnum").toString());
					task.setTargetId(qmessage.getGroupId());
					task.setType("group");
					if(noActiveGroup.contains(qmessage.getGroupId())) {
						noActiveGroup.remove(qmessage.getGroupId());
						task.setMessage("冲喵！");
					}else {
						task.setMessage("已冲喵！");
					}
					taskManager.addGeneralTask(task);
					return ;
				}else if(endSign.equals(qmessage.getMessage())&&!noActiveGroup.contains(qmessage.getGroupId())) {
					noActiveGroup.add(qmessage.getGroupId());
					GeneralTask task = new GeneralTask();
					task.setSelfQnum(event.get("selfQnum").toString());
					task.setTargetId(qmessage.getGroupId());
					task.setType("group");
					task.setMessage("面壁中~");
					taskManager.addGeneralTask(task);
					return ;
				}
			}
			
			if(noActiveGroup.contains(qmessage.getGroupId())) {
				return ;
			}
			
			
			//2.自定义快速回复型（对确定的消息进行匹配并产生简要回复的任务）
			//概率触发
			int pro = tpsc.triggerPro(qmessage.getGroupId());
			int tgt = random.nextInt(100);//[0~100)
			if(tgt<pro) {
				
			
				/*List<CfgQuickReply> replys = cfgQuickReplyMapper.queryByApply(2, qmessage.getGroupId());
				if(replys!=null&&!replys.isEmpty()) {
					List<CfgQuickReply> pReplys = replys.stream().filter(reply->1==reply.getMatchType()).collect(Collectors.toList());
					List<GeneralTask> pTasks = pReplys.stream()
							.filter(reply->1==reply.getMatchType()&&reply.getPattern().equals(qmessage.getMessage()))
							.map(reply->{
								GeneralTask task = new GeneralTask();
								task.setSelfQnum(event.get("selfQnum").toString());
								task.setTargetId(qmessage.getGroupId());
								task.setType("group");
								task.setMessage((1==reply.getNeedAt()?CQUtil.at(qmessage.getUserId().toString()):"")
									+reply.getReply());
								return task;
								})
							.collect(Collectors.toList());
					if(pTasks==null||pTasks.isEmpty()) {
						//精确未匹配到，再去匹配模糊的
						pReplys = replys.stream().filter(reply->2==reply.getMatchType()).collect(Collectors.toList());
						pTasks = pReplys.stream()
								.filter(reply->reply.getMatchType()==2&&Pattern.matches(reply.getPattern(),qmessage.getMessage()))
								.map(reply->{
									GeneralTask task = new GeneralTask();
									task.setSelfQnum(event.get("selfQnum").toString());
									task.setTargetId(qmessage.getGroupId());
									task.setType("group");
									task.setMessage((1==reply.getNeedAt()?CQUtil.at(qmessage.getUserId().toString()):"")
										+reply.getReply());
									return task;
								})
								.collect(Collectors.toList());
					}
					//匹配结束
					if(pTasks!=null&&!pTasks.isEmpty()) {
						//匹配到了记录，则随机取一个
						tasks.add(pTasks.get(new Random().nextInt(pTasks.size())));
					}
					
					
				}*/
				
				//accen@20191113重新实现快速回复功能
				CfgQuickReply aReply = cfgQuickReplyMapper.queryByApplyRandom(2, qmessage.getGroupId(), 1, qmessage.getMessage().trim());
				if(aReply!=null) {
					//匹配到精确词条
					GeneralTask task = new GeneralTask();
					task.setSelfQnum(event.get("selfQnum").toString());
					task.setTargetId(qmessage.getGroupId());
					task.setType("group");
					task.setMessage((1==aReply.getNeedAt()?CQUtil.at(qmessage.getUserId().toString()):"")
						+aReply.getReply());
					tasks.add(task);
				}else {
					//没有精确词条，则取找模糊词条
					CfgQuickReply fReply = cfgQuickReplyMapper.queryByApplyRandom(2, qmessage.getGroupId(), 2, qmessage.getMessage().trim());
					if(fReply!=null) {
						//模糊匹配到了
						GeneralTask task = new GeneralTask();
						task.setSelfQnum(event.get("selfQnum").toString());
						task.setTargetId(qmessage.getGroupId());
						task.setType("group");
						task.setMessage((1==fReply.getNeedAt()?CQUtil.at(qmessage.getUserId().toString()):"")
							+fReply.getReply());
						tasks.add(task);
					}
				}
				
			}
			//3.功能型（对系统功能进行操作，或对确定的消息匹配并产生复杂的回复的任务）
			Map<String, CmdAdapter> cmds = ApplicationContextUtil.getBeans(CmdAdapter.class); 
			for(String cmdName:cmds.keySet()) {
				CmdAdapter cmd = cmds.get(cmdName);
				if(funcSwitchUtil.isCmdPass(cmd.getClass(), qmessage.getMessageType(), qmessage.getGroupId())) {
					tasks.add(cmd.cmdAdapt(qmessage, event.get("selfQnum").toString()));
				}
				
			}
			//4.监听型（匹配所有消息，但满足特定条件后产生复杂的回复的任务）
			Map<String,ListenAdpter> listens = ApplicationContextUtil.getBeans(ListenAdpter.class);
			for(String listenName:listens.keySet()) {
				ListenAdpter listen = listens.get(listenName);
				List<GeneralTask> rs = listen.listen(qmessage, event.get("selfQnum").toString());
				if(rs!=null) {
					tasks.addAll(rs);
				}
				
			}
			//4.5 回调监听型
			
//			CallbackManager cm = ApplicationContextUtil.getBean(CallbackManager.class);
			callbackManager.accept(qmessage);
			
			//5.处理task
//			tasks.forEach(task->{
//				
//			});
			taskManager.addGeneralTasks(tasks);
		}
	}
	
}
