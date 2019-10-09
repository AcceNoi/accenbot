package org.accen.dmzj.core.task;

import java.util.Date;

public class TimerTask {
	private Date excuteTime;
	private GeneralTask task;
	public Date getExcuteTime() {
		return excuteTime;
	}
	public void setExcuteTime(Date excuteTime) {
		this.excuteTime = excuteTime;
	}
	public GeneralTask getTask() {
		return task;
	}
	public void setTask(GeneralTask task) {
		this.task = task;
	}
	
}
