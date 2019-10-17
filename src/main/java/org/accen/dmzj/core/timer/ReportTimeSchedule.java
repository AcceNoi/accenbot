package org.accen.dmzj.core.timer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportTimeSchedule {
	@Autowired
	private TaskManager taskManager;
	
	/**
	 * 每个群对应的闹钟
	 */
	private Map<String, String> groupClockMap = new HashMap<String, String>();
	
	@Value("${coolq.bot}")
	private String botId;
	
	@Scheduled(cron = "0 0 * * * *")
	public void reportTime() {
		//整点报时
		int hour = LocalDateTime.now().getHour();
		groupClockMap.forEach((groupId,clock)->{
			String recordUrl = getMp3UrlMoegirl(groupClockMap.get(groupId), hour);
			if(recordUrl!=null) {
				GeneralTask task = new GeneralTask();
				task.setSelfQnum(botId);
				task.setType("group");
				task.setTargetId(groupId);
				task.setMessage(CQUtil.recordUrl(recordUrl));
				taskManager.addGeneralTask(task);
			}
		});
	}
	/**
	 * 萌娘上的报时语音
	 * @param clock
	 * @return 
	 */
	private String getMp3UrlMoegirl(String clock,int hour) {
		switch (clock) {
		case "晓":
			return "https://img.moegirl.org/common/3/35/Akatsuki"+(30+hour	)+".mp3";
		case "响":
			return "https://img.moegirl.org/common/e/ee/%D0%92%D0%B5%D1%80%D0%BD%D1%8B%D0%B9"+(30+hour)+".mp3";
		default:
			return null;
		}
	}
	
	public boolean isOpenClock(String groupId) {
		return groupClockMap.containsKey(groupId);
	}
	public String openClock(String groupId,String clock) {
		return groupClockMap.put(groupId, groupId);
	}
	public String closeClock(String groupId) {
		return groupClockMap.remove(groupId);
	}
}
