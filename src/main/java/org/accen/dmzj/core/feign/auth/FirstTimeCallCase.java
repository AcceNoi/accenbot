package org.accen.dmzj.core.feign.auth;
/**
 * 对初次调用敏感的，适用于懒加载
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface FirstTimeCallCase {
	/**
	 * 如果是第一次调用，则额外调用此方法
	 */
	void firstTimeDo();
}
