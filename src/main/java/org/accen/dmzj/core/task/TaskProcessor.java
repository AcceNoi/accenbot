package org.accen.dmzj.core.task;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskProcessor implements Runnable{
	/**待处理的任务，由{@link TaskManager}管理*/
	private final BlockingQueue<GeneralTask> generalTaskQueue;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
	
	public TaskProcessor(BlockingQueue<GeneralTask> generalTaskQueue) {
		super();
		this.generalTaskQueue = generalTaskQueue;
	}


	@Override
	public void run() {
		try {
			while(true) {
				GeneralTask task =  generalTaskQueue.take();
				logger.debug("处理任务：${0}",task);
				TaskCoolqProcessor processor = new TaskCoolqProcessor();
				processor.processs(task);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
