package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于匹配Cmd Regular中expression的group位置
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutowiredRegular {
	/**
	 * group值（应该配置为自然数），若为0，则使用默认值即顺序匹配，其数值若大于expression group的总数则执行错误
	 * @return
	 */
	int value() default 0;
}
