package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个Cmd是否排斥另一个/多个cmd，需配合order
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rejection {
	String[] value() default {};
	/**
	 * 依赖模式，默认为any，即排斥value中的一个即可。可改为ALL，即需要排斥全部
	 * @return
	 */
	DependMode rejectMode() default DependMode.ANY;
}
