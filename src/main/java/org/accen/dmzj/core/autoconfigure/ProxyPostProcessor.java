package org.accen.dmzj.core.autoconfigure;

import org.accen.dmzj.core.AccenbotContext;
import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;

/**
 * 针对proxy生命周期的post processor
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
public interface ProxyPostProcessor {
	/**
	 * 在Context注册proxy前自行
	 * @param context
	 * @param proxy
	 * @return
	 */
	//TODO order
	default public AccenbotCmdProxy beforeRegisterProxy(AccenbotContext context,AccenbotCmdProxy proxy) {return proxy;}
	/**
	 * 在context注册完proxy后执行
	 * @param contex
	 * @param proxy
	 */
	default public void afterRegisterProxy(AccenbotContext contex,AccenbotCmdProxy proxy) {}
}
