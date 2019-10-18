package org.accen.dmzj.core.handler.callbacker;

import org.accen.dmzj.web.vo.Qmessage;
/**
 * 回调监听
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface CallbackListener {
	/**
	 * 监听消息，若返回true,则去掉此监听器实例
	 *
	 * @param qmessage
	 * @return
	 */
	public boolean listen(Qmessage originQmessage,Qmessage qmessage,String selfQnum);//TODO 虽然可以通过返回bool来选择是否移除，但为了保证稳定，应该给监听器增加一个超时timeout自动移除的机制
}
