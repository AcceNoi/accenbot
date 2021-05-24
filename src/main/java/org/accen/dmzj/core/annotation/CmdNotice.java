package org.accen.dmzj.core.annotation;
/**
 * 标识一个类、方法，接受Notice类型的Onebot事件上报
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.accen.dmzj.core.meta.NoticeSubType;
import org.accen.dmzj.core.meta.NoticeType;
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdNotice {
	String value() default "";
	/**
	 * 匹配的notice_type
	 * @return
	 */
	NoticeType[] noticeType() default NoticeType._ALL;
	/**
	 * 匹配的sub_type
	 * @return
	 */
	NoticeSubType[] subType() default NoticeSubType._ALL;
	String[] executeMethod() default {"execute"};
	boolean enableAutowiredParam() default true;
	int order() default 999;
}
