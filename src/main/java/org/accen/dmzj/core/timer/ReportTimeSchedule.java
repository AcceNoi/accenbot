package org.accen.dmzj.core.timer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportTimeSchedule {
	@Autowired
	private TaskManager taskManager;
	
	@Autowired
	private CfgResourceMapper cfgResourceMapper;
	
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
	private static final String KEY_PREFFIX = "record_clock_";
	/**
	 * 萌娘上的报时语音
	 * @param clock
	 * @return 
	 */
	private String getMp3UrlMoegirl(String clock,int hour) {
		String key = null;
		switch (clock) {
		case "晓":
			key= KEY_PREFFIX+"Akatsuki_"+hour;
			break;
		case "响":
			key = KEY_PREFFIX+"Hibiki_"+hour;
			break;
		case "吹雪":
			key = KEY_PREFFIX+"Fubuki_"+hour;
			break;
		default:
			break;
		}
		if(null!=key) {
			CfgResource cr = cfgResourceMapper.selectByKey(key);
			if(cr!=null) {
				return cr.getCfgResource();
			}
		}
		return null;
	}
	
	public boolean isOpenClock(String groupId) {
		return groupClockMap.containsKey(groupId);
	}
	public String openClock(String groupId,String clock) {
		return groupClockMap.put(groupId, clock);
	}
	public String closeClock(String groupId) {
		return groupClockMap.remove(groupId);
	}
	public String getClock(String groupId) {
		return groupClockMap.get(groupId);
	}
}
