package org.accen.dmzj.core.handler.cmd;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.api.bilibili.BilibiliSearchApiClientPk;
import org.accen.dmzj.core.api.pixivc.PixivicApiClient;
import org.accen.dmzj.core.api.vo.BilibiliBangumiInfo;
import org.accen.dmzj.core.api.vo.BilibiliUserInfo;
import org.accen.dmzj.core.handler.group.Subscribe;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@FuncSwitch(groupClass = Subscribe.class, title = "订阅B站up",format = "订阅B站UP+[uid]",showMenu = true)
@Component
@Transactional
public class BilibiliSubscribe implements CmdAdapter{

	@Autowired
	private CmdBuSubMapper cmdBuSubMapper;

	@Autowired
	private BilibiliSearchApiClientPk bilibiliSearchApiClientPk;
	@Autowired
	private PixivicApiClient pixivicApiClient;
	
	private final static Pattern pattern = Pattern.compile("^(取消)?订阅B站(UP|Up|up|番剧)(.*)");
	private final static Pattern patternIllustor = Pattern.compile("^(取消)?订阅P站(画师)(\\d+)");
	private final static Pattern myPattern = Pattern.compile("^我的订阅$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		Matcher matcherIllustor = patternIllustor.matcher(message);
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
							
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 成功订阅B站"+matcher.group(2)+":"+bInfo.getName()+"("+bInfo.getMediaId()+")喵~\n");
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
							sub.setAttr1(uInfo.getRoomId()+"#0");//默认为0
							cmdBuSubMapper.insert(sub);
							
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 已成功订阅B站"+matcher.group(2)+":"+uInfo.getName()+"("+uInfo.getMid()+")喵~\n"
									+StringUtil.SPLIT+uInfo.getUsign());
							return task;
						}else {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 未找到此B站"+matcher.group(2)+"信息哦，请核对名称或id喵~");
							return null;
						}
					}
					
				}
			}
		}else if(matcherIllustor.matches()) {
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			//p站画师订阅
			Map<String,Object> artistDetail = pixivicApiClient.artist(Integer.parseInt(matcherIllustor.group(3)));
			
			if(artistDetail.containsKey("data")) {
				String artistName = (String) ((Map<String,Object>)artistDetail.get("data")).get("name");
				String avatar = (String) ((Map<String,Object>)artistDetail.get("data")).get("avatar");
				String comment = (String) ((Map<String,Object>)artistDetail.get("data")).get("comment");
				List<CmdBuSub>  subs = null;
				if("取消".equals(matcherIllustor.group(1))) {
					subs = cmdBuSubMapper.findBySubscriberAndObj("group", qmessage.getGroupId(), qmessage.getUserId(), "pixiv", "artist", matcherIllustor.group(3));
					if(subs==null||subs.isEmpty()) {
						task.setMessage(CQUtil.at(qmessage.getUserId())+" 还未订阅此画师"+artistName+"（"+matcherIllustor.group(3)+"）喵~");
					}else {
						cmdBuSubMapper.deleteById(subs.get(0).getId());
						task.setMessage(CQUtil.at(qmessage.getUserId())+" 已取消订阅画师"+artistName+"（"+matcherIllustor.group(3)+"）了喵~");
					}	
				}else {
					//订阅
					CmdBuSub sub = new CmdBuSub();
					sub.setStatus("1");
					sub.setSubObj(""+matcherIllustor.group(3));
					sub.setSubObjMark(artistName);
					sub.setSubscriber(qmessage.getUserId());
					sub.setTargetId(qmessage.getGroupId());
					sub.setSubTarget("pixiv");
					sub.setSubType("artist");
					sub.setType("group");
					sub.setSubTime(new Date());
					
					cmdBuSubMapper.insert(sub);
					
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 已成功订阅P站画师"+artistName+"（"+matcherIllustor.group(3)+"）"
							+CQUtil.imageUrl(avatar.replace("i.pximg.net", "i.pixiv.cat"))
							+StringUtil.SPLIT+comment);
				}
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 无法找到此画师喵~画师ID："+matcherIllustor.group(3));
			}
			
			return task;
			
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
							.append(mySub.getSubTarget()).append(" ")
							.append(mySub.getSubType())
							.append("[")
							.append(mySub.getSubObj())
							.append("]")
							.append(mySub.getSubObjMark());
					
				}
				task.setMessage(msgBuf.toString());
				return task;
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您当前还没订阅喵~发送[订阅P站画师159912]试试喵~");
				return task;
			}
		}
		return null;
	}

}
