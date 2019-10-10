package org.accen.dmzj.core.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskManager {
	private final static Logger logger = LoggerFactory.getLogger(TaskManager.class);
	private BlockingQueue<GeneralTask> generalTaskQueue;
//	private Timer timer;
	
	//线程池
	private ExecutorService generalTaskProcessorPool = Executors.newCachedThreadPool();
	
	public TaskManager() {
		super();
		this.generalTaskQueue = new LinkedBlockingQueue<GeneralTask>();
//		this.timer = new Timer();
		start();
	}
	/**
	 * 开始任务调度
	 */
	public void start() {
		generalTaskProcessorPool.execute(new TaskProcessor(generalTaskQueue));
		logger.info("TaskManager启动成功！开始接收任务调度...");
	}
	/**
	 * 新增一个普通任务
	 * @param generalTask
	 */
	public void addGeneralTask(GeneralTask generalTask) {
		if(generalTask!=null) {
			generalTaskQueue.offer(generalTask);
		}
	}
	/**
	 * 新增若干个普通任务
	 * @param generalTasks
	 */
	public void addGeneralTasks(List<GeneralTask> generalTasks) {
		generalTasks.forEach(this::addGeneralTask);
	}
	/**
	 * 新增一个指定时间任务
	 * @param task
	 * @param date
	 */
	public void addTimerTask(GeneralTask task,Date date) {
		
	}
}
