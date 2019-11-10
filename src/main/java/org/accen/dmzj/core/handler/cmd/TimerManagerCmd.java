package org.accen.dmzj.core.handler.cmd;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.timer.ReportTimeSchedule;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@FuncSwitch("cmd_report_manage")
@Component
public class TimerManagerCmd implements CmdAdapter {

	@Autowired
	private ReportTimeSchedule timeSchedule;
	@Autowired
	private TaskManager TaskManager;
	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String example() {
		// TODO Auto-generated method stub
		return null;
	}

	@Value("${coolq.manager}")
	private String manager = "1339633536";//管理员qq
	
	private static final Pattern pattern = Pattern.compile("^(开启|关闭)(.*?)报时$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String role = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("role");
		if((!manager.equals(qmessage.getUserId()))&&(!"owner".equals(role))&&(!"admin".equals(role))) {
			return null;
		}
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			if("关闭".equals(matcher.group(1))) {
				if(timeSchedule.isOpenClock(qmessage.getGroupId())) {
					//开启着在
					timeSchedule.closeClock(qmessage.getGroupId());
					task.setMessage("已关闭报时喵~");
					return task;
				}else {
					//没开着
					task.setMessage("本群还未开启报时哦喵~");
					return task;
				}
			}else if("开启".equals(matcher.group(1))){
				String clock = matcher.group(2);
				boolean isHaveOpened = timeSchedule.isOpenClock(qmessage.getGroupId());
				switch (clock) {
				case "晓":
					timeSchedule.openClock(qmessage.getGroupId(), "晓");
					task.setMessage((isHaveOpened?"切换":"开启")+"报时成功喵！[晓]将为您报时~");
					
					GeneralTask taskOpn = new GeneralTask();
					taskOpn.setSelfQnum(selfQnum);
					taskOpn.setType(qmessage.getMessageType());
					taskOpn.setTargetId(qmessage.getGroupId());
					taskOpn.setMessage(CQUtil.recordUrl(CQUtil.recordUrl("https://img.moegirl.org/common/7/70/Akatsuki01.mp3")));
					TaskManager.addGeneralTask(taskOpn);
					return task;
				case "响":
					timeSchedule.openClock(qmessage.getGroupId(), "响");
					task.setMessage((isHaveOpened?"切换":"开启")+"报时成功喵！[响]将为您报时~");
					
					GeneralTask taskOpn2 = new GeneralTask();
					taskOpn2.setSelfQnum(selfQnum);
					taskOpn2.setType(qmessage.getMessageType());
					taskOpn2.setTargetId(qmessage.getGroupId());
					taskOpn2.setMessage(CQUtil.recordUrl("https://img.moegirl.org/common/5/55/%D0%92%D0%B5%D1%80%D0%BD%D1%8B%D0%B91.mp3"));
					TaskManager.addGeneralTask(taskOpn2);
					return task;
				case "吹雪":
					timeSchedule.openClock(qmessage.getGroupId(), "吹雪");
					task.setMessage((isHaveOpened?"切换":"开启")+"报时成功喵！[吹雪]将为您报时~");
					
					GeneralTask taskOpn3 = new GeneralTask();
					taskOpn3.setSelfQnum(selfQnum);
					taskOpn3.setType(qmessage.getMessageType());
					taskOpn3.setTargetId(qmessage.getGroupId());
					taskOpn3.setMessage(CQUtil.recordUrl("https://img.moegirl.org/common/d/d6/Fubuki1.mp3"));
					TaskManager.addGeneralTask(taskOpn3);
					return task;
				default:
					task.setMessage("找不到此闹钟，这一定不是我的错喵~");
					return task;
				}
			}
			
			
		}
		return null;
	}

}
