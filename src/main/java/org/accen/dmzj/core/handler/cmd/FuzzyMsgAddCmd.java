package org.accen.dmzj.core.handler.cmd;

import java.util.Date;
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
import org.springframework.util.StringUtils;

@Component
@Transactional
public class FuzzyMsgAddCmd implements CmdAdapter {
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;

	@Autowired
	private CheckinCmd checkinCmd;
	@Value("${coolq.fuzzymsg.coin.decrease:3}")
	private int decrease = 3;//
	
	@Override
	public String describe() {
		return "新增一条消息匹配回复";
	}

	@Override
	public String example() {
		return "添加[精确]问什么番？答[回复]吐鲁番";
	}

	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage,String selfQnum) {
		//1.基本信息
		String message = qmessage.getMessage().trim();
		
		//2.匹配
		//	2.1匹配结果
		boolean isPrecise = false;//是否是精确的
		boolean isNeedReply = false;//是否是需要回复的
		//	2.2开始匹配
		Pattern pattern = Pattern.compile("^添加(精确)?问(.*?)答(回复)?(.*)");
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			isPrecise |= matcher.group(1)!=null;
			isNeedReply |= matcher.group(3)!=null;
			
			String ask = matcher.group(2);
			String reply = matcher.group(4);
			//3.1处理
			
			if(StringUtils.isEmpty(ask)||StringUtils.isEmpty(reply)) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+"添加失败，示例："+example());
			}else {
				
				//金币检验
				int curCoin = checkinCmd.getCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
				if(curCoin<0) {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未绑定哦，暂时无法添加词条，发送[绑定]即可绑定个人信息喵~");
				}else if(curCoin-decrease<0) {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您库存金币不够了哦，暂无法添加词条喵~");
				}else {
					CfgQuickReply cfgReply = new CfgQuickReply();
					cfgReply.setMatchType(isPrecise?1:2);
					cfgReply.setPattern(isPrecise?ask:".*?"+ask+".*");
					cfgReply.setReply(reply);
					
					switch (task.getType()) {
					case "private":
						cfgReply.setApplyType(1);
						break;
					case "group":
						cfgReply.setApplyType(2);
						break;
					case "discuss":
						cfgReply.setApplyType(3);
						break;
					default:
						break;
					}
					
					cfgReply.setApplyTarget(task.getTargetId());
					cfgReply.setNeedAt(isNeedReply?1:2);
					cfgReply.setCreateTime(new Date());
					cfgReply.setCreateUserId(qmessage.getUserId());
					cfgReply.setStatus(1);
					long replyId = cfgQuickReplyMapper.insert(cfgReply);
					
					//金币消耗
					
					int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -decrease);
					
					task.setMessage(CQUtil.at(qmessage.getUserId())+"添加成功！词条编号："+cfgReply.getId()+"。本次消耗金币："+decrease+"。剩余："+newCoin);
				}
				
			}
			return task;
		}else {
			return null;
		}
		
	}
}