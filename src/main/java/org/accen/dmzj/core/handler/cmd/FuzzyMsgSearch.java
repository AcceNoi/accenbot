package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class FuzzyMsgSearch implements CmdAdapter {
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	@Override
	public String describe() {
		return "查询一条已存在的词条";
	}

	@Override
	public String example() {
		return "查看词条1";
	}
	private final static Pattern pattern = Pattern.compile("^查看词条(\\d+)$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			CfgQuickReply reply = cfgQuickReplyMapper.selectById(Long.parseLong(matcher.group(1)));
			if(reply==null||reply.getStatus()!=1) {
				task.setMessage("无法找到此词条，请确认词条编号喵~");
			}else if(!qmessage.getGroupId().equals(reply.getApplyTarget())) {
				task.setMessage("非本群词条喵~");
			}else {
				task.setMessage("词条"+reply.getId()+"为[问"+reply.getPattern()+"答"+reply.getReply()+"]喵~");
			}
			return task;
		}
		return null;
	}

}
