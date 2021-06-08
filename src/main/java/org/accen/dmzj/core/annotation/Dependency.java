package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个Cmd是否依赖另一个/多个cmd，需配合order
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dependency {
	String[] value() default {};
	/**
	 * 依赖模式，默认为all，即依赖value中全部。可改为ANY，即依赖一个即可
	 * @return
	 */
	DependMode dependMode() default DependMode.ALL;
}
