package org.accen.dmzj.core.handler.listen;

import java.util.List;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;

/**
 * 监听适配器，会匹配所有的消息，满足一定的条件后触发特定的功能
 * @author Accen
 * @since 0.1
 */
public interface ListenAdpter {
	/**
	 * 监听，若触发某事件则可能产生多个任务
	 * @param qmessage
	 * @param selfQnum
	 * @return
	 */
	List<GeneralTask> listen(Qmessage qmessage,String selfQnum);
}
