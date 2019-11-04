package org.accen.dmzj.core.handler.cmd;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@FuncSwitch("cmd_timer")
@Component
public class TimerCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "设置定时提醒任务";
	}

	@Override
	public String example() {
		return "1小时后提醒我吃饭";
	}
	
	@Autowired
	private TaskManager taskManager;
	
	private static final Pattern pattern = Pattern.compile("^老婆((\\d)?小时)?(([1-5]?[0-9])?分钟)?(([1-5]?[0-9])秒)?后(提醒我|说)(.+)");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			
			int h = matcher.group(2)==null?0:Integer.parseInt(matcher.group(2));
			int m = matcher.group(4)==null?0:Integer.parseInt(matcher.group(4));
			int s = matcher.group(6)==null?0:Integer.parseInt(matcher.group(6));
			String type = matcher.group(7);
			String msg = matcher.group(8);
			
			GeneralTask task =  new GeneralTask();
			
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			if(h+m+s>0) {
				Timer nTimer = new Timer();
				nTimer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						GeneralTask task2 =  new GeneralTask();
						
						task2.setSelfQnum(selfQnum);
						task2.setType(qmessage.getMessageType());
						task2.setTargetId(qmessage.getGroupId());
						task2.setMessage(("提醒我".equals(type)?CQUtil.at(qmessage.getUserId())+"老公时间到啦，该":"时间到啦")+msg
								+("提醒我".equals(type)?"了":"")
								+"喵~");
						taskManager.addGeneralTask(task2);
					}
				}, h*60*60*1000+m*60*1000+s*1000);
				task.setMessage(CQUtil.at(qmessage.getUserId())+"老公收到了喵~，"
						+(0==h?"":h+"小时")
						+(0==m?"":m+"分钟")
						+(0==s?"":s+"秒")
						+"后"
						+("提醒我".equals(type)?"提醒你":"说")
						+msg+"喵！");
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+"老公你在说什么听不懂喵~");
				
			}
			return task;
			
			
		}
		return null;
	}

}
