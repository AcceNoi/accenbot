package org.accen.dmzj.core.handler.cmd;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.BilibiliSearchApiClientPk;
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
	
	private final static Pattern pattern = Pattern.compile("^(取消)?订阅B站(UP|番剧)(.*)");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			String subTarget = "bilibili";
			String subType = matcher.group(2).equals("番剧")?"bangumi":"up";
			String subObj = null;
			String subObjMark = null;
			List<CmdBuSub>  subs = null;
			if(StringUtil.isNumberString(matcher.group(3))) {
				subObj = matcher.group(3);
				subs = cmdBuSubMapper.findBySubscriberAndObj("group", qmessage.getGroupId(), qmessage.getUserId(), "bilibili",subType,subObj);
			}else {
				subObjMark = matcher.group(3);
				subs = cmdBuSubMapper.findBySubscriberAndObjMark("group", qmessage.getGroupId(), qmessage.getUserId(), "bilibili",subType,subObjMark);
			}
			
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
					if("bangui".equals(subType)) {
						
					}else if("up".equals(subType)) {
						BilibiliUserInfo info = null;
						if(StringUtil.isNumberString(matcher.group(3))) {
							info = bilibiliSearchApiClientPk.searchUser(Long.parseLong(subObj));
						}else {
							info = bilibiliSearchApiClientPk.searchUser(subObjMark);
						}
						
						if(info!=null) {
							//找到了，则订阅
							CmdBuSub sub = new CmdBuSub();
							sub.setStatus("1");
							sub.setSubObj(""+info.getMid());
							sub.setSubObjMark(info.getName());
							sub.setSubscriber(qmessage.getUserId());
							sub.setTargetId(qmessage.getGroupId());
							sub.setSubTarget("bilibili");
							sub.setSubType(subType);
							sub.setType("group");
							sub.setSubTime(new Date());
							cmdBuSubMapper.insert(sub);
							
							task.setMessage(CQUtil.at(qmessage.getUserId()+" 已成功订阅B站"+matcher.group(2)+":"+info.getName()+"("+info.getMid()+")喵~"));
							return task;
						}else {
							task.setMessage(CQUtil.at(qmessage.getUserId()+" 未找到此B站"+matcher.group(2)+"信息哦，请核对名称或id喵~"));
							return null;
						}
					}
					
				}
			}
			
		}
		return null;
	}

}
