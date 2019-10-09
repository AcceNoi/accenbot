package org.accen.dmzj.core.task;

import java.util.concurrent.BlockingQueue;

public class TaskProcessor implements Runnable{
	/**待处理的任务，由{@link TaskManager}管理*/
	private final BlockingQueue<GeneralTask> generalTaskQueue;
	
	public TaskProcessor(BlockingQueue<GeneralTask> generalTaskQueue) {
		super();
		this.generalTaskQueue = generalTaskQueue;
	}


	@Override
	public void run() {
		try {
			GeneralTask task =  generalTaskQueue.take();
			TaskCoolqProcessor processor = new TaskCoolqProcessor();
			processor.processs(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
