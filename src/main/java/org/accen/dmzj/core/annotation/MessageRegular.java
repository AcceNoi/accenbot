package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在CmdMessage的基础上实现CmdRegular的功能，同时废弃不受AccenbotContex管辖的CmdRegular
 * <br>标注在被CmdMessage注解的Method上，以对message的内容进行匹配并注入到方法中
 * @see AutowiredRegular
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageRegular {
	/**
	 * 匹配的正则式 
	 * @return
	 */
	String expression();
}
