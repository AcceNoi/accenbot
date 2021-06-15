package org.accen.dmzj.core.autoconfigure;

import org.accen.dmzj.core.AccenbotContext;
import org.accen.dmzj.core.meta.PostType;

/**
 * 监听accenbotcontext的注册和注销
 * @author <a href="1339liu@gmail.com">Accen</a>
 * since 2.2
 */
public interface ContextPostProcessor {
	/**
	 * 注册完一个context后收到此通知
	 * @param postType
	 * @param context
	 */
	default public void afterRegisterContext(PostType postType,AccenbotContext context) {}
	//TODO 注销
}
