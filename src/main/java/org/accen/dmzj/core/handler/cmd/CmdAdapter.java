package org.accen.dmzj.core.handler.cmd;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;

/**
 * 此类用于处理功能型的消息，例如“新增一条精确匹配”
 * @author Accen
 * @since 0.1
 */
public interface CmdAdapter {
	/**
	 * 具体操作
	 * @param qmessage
	 */
	GeneralTask cmdAdapt(Qmessage qmessage,String selfQnum);
}
