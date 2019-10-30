package org.accen.dmzj.core.handler.cmd;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
@Transactional
public class FuzzyMsgDeleteCmd  implements CmdAdapter{

	@Autowired
	private CheckinCmd checkinCmd;
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	
	@Override
	public String describe() {
		return "删除一条已存在的词条";
	}

	@Override
	public String example() {
		return "删除词条1";
	}
	
	@Value("${coolq.fuzzymsg.fav.lowerlimit:10}")
	private int lowerLimitFav;//删除词条最低好感
	@Value("${coolq.fuzzymsg.fav.adminexcept:true}")
	private boolean adminExcept;//管理员是否不受好感限制
	
	@Value("${coolq.manager}")
	private String manager = "1339633536";//管理员qq
	
	private final static Pattern pattern = Pattern.compile("^删除词条(\\d+)$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			int fav = checkinCmd.getFav(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());//好感度
			if(!manager.equals(qmessage.getUserId())) {
				if(adminExcept) {
					String role = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("role");
					if((!"owner".equals(role))&&(!"admin".equals(role))&&fav<lowerLimitFav) {
						//不是管理员，也不是群主，同时好感度不够。不给删
						task.setMessage(CQUtil.at(qmessage.getUserId())+"当前好感度不够，无法删除词条喵~");
						return task;
					}
				}else {
					if(fav<lowerLimitFav) {
						//好感度不够。不给删
						task.setMessage(CQUtil.at(qmessage.getUserId())+"当前好感度不够，无法删除词条喵~");
						return task;
					}
				}
			}
			
			
			
			CfgQuickReply reply = cfgQuickReplyMapper.selectById(Long.parseLong(matcher.group(1)));
			if(reply==null||reply.getStatus()!=1) {
				task.setMessage("无法找到此词条，请确认词条编号喵~");
			}else if("0".equals(reply.getApplyTarget())||"-1".equals(reply.getApplyTarget())) {
				task.setMessage("无法删除公共词条喵~");
			}
			else if(!qmessage.getGroupId().equals(reply.getApplyTarget())) {
				task.setMessage("非本群词条喵~");
			}else {
				reply.setStatus(2);
				cfgQuickReplyMapper.update(reply);
				task.setMessage("删除词条"+reply.getId()+"[问"+reply.getPattern()+"答"+reply.getReply()+"]成功喵！");
			}
			return task;
		}
		return null;
	}

}
