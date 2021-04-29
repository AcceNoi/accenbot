package org.accen.dmzj.core.api.pixivc;

public interface AuthFresher {
	/**
	 * 验证该auth是否有效
	 * @param auth
	 * @return
	 */
	boolean vertify(Auth auth);
	/**
	 * 刷新或产生一个新的auth
	 * @param auth
	 * @return
	 */
	Auth fresh(Auth auth);
}
