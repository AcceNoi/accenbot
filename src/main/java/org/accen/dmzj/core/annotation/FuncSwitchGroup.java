package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能组
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FuncSwitchGroup {
	/**
	 * 分组Key，默认为grp_类名
	 * @return
	 */
	String name() default "";
	/**
	 * 是否要在菜单中展示
	 * @return
	 */
	boolean showMenu() default false;
	/**
	 * 分组名
	 * @return
	 */
	String title();
	/**
	 * 排序
	 * @return
	 */
	int order() default 99;
	String[] matchSigns() default {};
}
