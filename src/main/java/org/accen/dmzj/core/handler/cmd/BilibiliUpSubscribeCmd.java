package org.accen.dmzj.core.handler.cmd;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.BilibiliSearchApiClientPk;
import org.accen.dmzj.core.task.api.vo.BilibiliBangumiInfo;
import org.accen.dmzj.core.task.api.vo.BilibiliUserInfo;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class BilibiliUpSubscribeCmd implements CmdAdapter{

	@Autowired
	private CmdBuSubMapper cmdBuSubMapper;
	
	@Override
	public String describe() {
		return "订阅或取消一位b站视频up主";
	}

	@Override
	public String example() {
		return "订阅錬金ノクロノス";
	}

	@Autowired
	private BilibiliSearchApiClientPk bilibiliSearchApiClientPk;
	
	private final static Pattern pattern = Pattern.compile("^(取消)?订阅B站(UP|Up|up|番剧)(.*)");
	private final static Pattern myPattern = Pattern.compile("^我的订阅$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		Matcher myMatcher = myPattern.matcher(message);
		if(matcher.matches()) {
			String subTarget = "bilibili";
			String subType = matcher.group(2).equals("番剧")?"bangumi":"up";
			String subObj = null;
			String subObjMark = null;
			List<CmdBuSub>  subs = null;
			
			BilibiliBangumiInfo bInfo = null;
			BilibiliUserInfo uInfo = null;
			
			
			if(!StringUtil.isNumberString(matcher.group(3))) {
				subObjMark = matcher.group(3);
				//不是id,而是标题，则先获取下完整的标题
				if("bangumi".equals(subType)) {
					bInfo = bilibiliSearchApiClientPk.searchBangumi(subObjMark);
					if(bInfo!=null) {
						subObj = ""+bInfo.getMediaId();
					}
				}else if("up".equals(subType)) {
					uInfo = bilibiliSearchApiClientPk.searchUser(subObjMark);
					if(uInfo!=null) {
						subObj = ""+uInfo.getMid();
					}
				}
				
			}else {
				subObj = matcher.group(3);
			}
			
			
			subs = cmdBuSubMapper.findBySubscriberAndObj("group", qmessage.getGroupId(), qmessage.getUserId(), subTarget,subType,subObj);
			
			
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			//1.取消
			if("取消".equals(matcher.group(1))) {
				if(subs!=null&&!subs.isEmpty()) {
					cmdBuSubMapper.deleteById(subs.get(0).getId());
					task.setMessage(CQUtil.at(qmessage.getUserId())
							+" "+subs.get(0).getSubTarget()
							+"订阅["+subs.get(0).getSubObj()+","+subs.get(0).getSubObjMark()+"]已经取消掉了喵~");
					return task;
				}else {
					task.setMessage(CQUtil.at(qmessage.getUserId())
							+" 无法找到您的订阅喵~");
					return task;
				}
			}else {
				//2.订阅
				if(subs!=null&&!subs.isEmpty()) {
					task.setMessage(CQUtil.at(qmessage.getUserId())
							+" "+subs.get(0).getSubTarget()
							+"["+subs.get(0).getSubObj()+","+subs.get(0).getSubObjMark()+"]不要重复订阅喵！！");
					return task;
				}else {
					if("bangumi".equals(subType)) {
						
						if(bInfo!=null) {
							CmdBuSub sub = new CmdBuSub();
							sub.setStatus("1");
							sub.setSubObj(""+bInfo.getMediaId());
							sub.setSubObjMark(bInfo.getName());
							sub.setSubscriber(qmessage.getUserId());
							sub.setTargetId(qmessage.getGroupId());
							sub.setSubTarget("bilibili");
							sub.setSubType(subType);
							sub.setType("group");
							sub.setSubTime(new Date());
							cmdBuSubMapper.insert(sub);
							
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 已成功订阅B站"+matcher.group(2)+":"+bInfo.getName()+"("+bInfo.getMediaId()+")喵~");
							return task;
						}else {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 未找到此B站"+matcher.group(2)+"信息哦，请核对名称或id喵~");
							return null;
						}
					}else if("up".equals(subType)) {
						
						if(StringUtil.isNumberString(matcher.group(3))&&uInfo==null) {
							//由于第一次初始化uInfo只是在非id情况下，所以这里再找一次id情况的
							uInfo = bilibiliSearchApiClientPk.searchUser(Long.parseLong(subObj));
						}
						
						if(uInfo!=null) {
							//找到了，则订阅
							CmdBuSub sub = new CmdBuSub();
							sub.setStatus("1");
							sub.setSubObj(""+uInfo.getMid());
							sub.setSubObjMark(uInfo.getName());
							sub.setSubscriber(qmessage.getUserId());
							sub.setTargetId(qmessage.getGroupId());
							sub.setSubTarget("bilibili");
							sub.setSubType(subType);
							sub.setType("group");
							sub.setSubTime(new Date());
							cmdBuSubMapper.insert(sub);
							
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 已成功订阅B站"+matcher.group(2)+":"+uInfo.getName()+"("+uInfo.getMid()+")喵~");
							return task;
						}else {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 未找到此B站"+matcher.group(2)+"信息哦，请核对名称或id喵~");
							return null;
						}
					}
					
				}
			}
			
		}else if(myMatcher.matches()) {
			//我的订阅
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			List<CmdBuSub> mySubs = cmdBuSubMapper.findBySubscriber(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			if(null!=mySubs&&!mySubs.isEmpty()) {
				StringBuffer msgBuf = new StringBuffer(CQUtil.at(qmessage.getUserId()));
				msgBuf.append(" 您在本群一共有"+mySubs.size()+"个订阅喵：\n");
				for(int index=0;index<mySubs.size();index++) {
					if(index>0) {
						msgBuf.append("\n");
					}
					CmdBuSub mySub = mySubs.get(index);
					msgBuf.append(index+1)
							.append(". ")
							.append("bilibili".equals(mySub.getSubTarget())?"B站":"")
							.append(mySub.getSubType())
							.append("[")
							.append(mySub.getSubObj())
							.append("]")
							.append(mySub.getSubObjMark());
					
				}
				task.setMessage(msgBuf.toString());
				return task;
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您当前还没订阅喵~发送[订阅B站UP陈睿]试试喵~");
				return task;
			}
		}
		return null;
	}

}
