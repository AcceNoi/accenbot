package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.accen.dmzj.core.handler.CmdManager;
import org.accen.dmzj.web.vo.Qmessage;

/**
 * 使用正则式进行匹配的cmd，简要化，确保该方法是public修饰的<br />
 * 其方法参数暂时只允许简单参数类型和String<br />
 * @see CmdManager.CmdRegularManager
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.1
 * @since 2.2 deprecated
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface CmdRegular {
	String name() default "";
	/**
	 * 待匹配的正则式
	 * @return
	 */
	String expression();
	/**
	 * 是否允许注入{@link Qmessage}参数，默认true
	 * @return
	 */
	boolean enableAutowiredParam() default true;
}
