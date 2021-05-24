package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.accen.dmzj.core.meta.MetaEventType;
import org.accen.dmzj.core.meta.MetaSubType;

/**
 * 标识一个meta类型的event处理器
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdMeta {
	String value() default "";
	/**
	 * 匹配的MetaEventType
	 * @return
	 */
	MetaEventType[] metaEventType() default MetaEventType._ALL;
	MetaSubType[] subType() default MetaSubType._ALL;
	String[] executeMethod() default {"execute"};
	boolean enableAutowiredParam() default true;
	int order() default 999;
}