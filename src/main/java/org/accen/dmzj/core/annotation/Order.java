package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义ContextPostProcessor、EventPostProcessor、EventCmdPostProcessor、ProxyPostProcessor等钩子的执行顺序
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {
	public static final int LOWEST_ORDER = Integer.MAX_VALUE;
	/**
	 * 定义顺序
	 * @return
	 */
	int value() default LOWEST_ORDER;
	/**
	 * value相等时，使用这个确定，但是一般的类请不要使用这个
	 * @return
	 */
	int xvalue() default LOWEST_ORDER;
}
