package org.accen.dmzj.core.task;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;

public class TaskManager {
	private BlockingQueue<GeneralTask> generalTaskQueue;
	private Timer timer;
	public TaskManager(BlockingQueue<GeneralTask> generalTaskQueue) {
		super();
		this.generalTaskQueue = generalTaskQueue;
		this.timer = new Timer();
	}
	/**
	 * 开始任务调度
	 */
	public void start() {
		
	}
	/**
	 * 新增一个普通任务
	 * @param generalTask
	 */
	public void addGeneralTask(GeneralTask generalTask) {
		
	}
	/**
	 * 新增若干个普通任务
	 * @param generalTasks
	 */
	public void addGeneralTasks(List<GeneralTask> generalTasks) {
		
	}
	/**
	 * 新增一个指定时间任务
	 * @param task
	 * @param date
	 */
	public void addTimerTask(GeneralTask task,Date date) {
		
	}
}
