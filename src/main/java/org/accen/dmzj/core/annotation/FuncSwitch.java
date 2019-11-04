package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 配置在Cmd上面，标识其对应的功能点
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FuncSwitch {
	String value();
}
