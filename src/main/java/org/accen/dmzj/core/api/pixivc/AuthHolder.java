package org.accen.dmzj.core.api.pixivc;
/**
 * 提供对auth的管理，可以考虑本地管理、远程管理、分布式管理、缓存管理等实现方式
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface AuthHolder {
	/**
	 * 获得auth
	 * @return
	 */
	Auth getAuth();
	/**
	 * 更新auth
	 * @param auth
	 */
	void updateAuth(Auth auth);
}
