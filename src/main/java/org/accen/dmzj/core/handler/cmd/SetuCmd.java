package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.LoliconApiClientPk;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@FuncSwitch("cmd_setu")
@Component
public class SetuCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "随机获取网上的一张p站图";
	}

	@Override
	public String example() {
		return "随机涩图";
	}
	@Autowired
	private LoliconApiClientPk loliconApiClientPk;
	@Autowired
	private FuncSwitchUtil funcSwitchUtil;

	private static final Pattern pattern = Pattern.compile("^随机(色图|瑟图|涩图)$");
	
//	private Boolean locked = false;//未知原因使得此功能被滥用则回系统崩溃，可能是coolq pro接收数据的超时设置问题，这里为了防止滥用，同一时间段只接收一个请求。
	
	@Override
	public synchronized GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
//		if(locked) {
//			return null;
//		}else {
//			synchronized (locked) {
//				
//				if(locked) {
//					return null;
//				}else {
//					locked = true;
					String message = qmessage.getMessage().trim();
					Matcher matcher = pattern.matcher(message);
					if(matcher.matches()) {
						String imageUrl = loliconApiClientPk.setu();
						if(imageUrl!=null&&funcSwitchUtil.isImgReviewPass(imageUrl, qmessage.getMessageType(), qmessage.getGroupId())) {
							GeneralTask task =  new GeneralTask();
							
							task.setSelfQnum(selfQnum);
							task.setType(qmessage.getMessageType());
							task.setTargetId(qmessage.getGroupId());
							task.setMessage(CQUtil.imageUrl(imageUrl));
							
//							locked = false;
							return task;
						}
						
						
						
					}
//					locked = false;
					return null;
//				}
				
//			}
//		}
		
		
	}

}
