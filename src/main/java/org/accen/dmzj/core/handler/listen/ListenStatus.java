package org.accen.dmzj.core.handler.listen;

import org.accen.dmzj.web.vo.CfgListenStatus;

/**
 * 描述此监听器是属于状态面板的，比如XXX模式
 * @author 刘伟
 *
 */
public interface ListenStatus {
	/**
	 * 状态名
	 * @return
	 */
	String name();
	/**
	 * status name in english
	 * @return
	 */
	String nameEn();
	/**
	 * 此监听器的编号，与{@link CfgListenStatus}对应
	 * @return
	 */
	String code();
}
