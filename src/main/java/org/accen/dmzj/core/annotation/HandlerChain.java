package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HandlerChain {
	@AliasFor(annotation = Component.class)
	String value() default "";
	/**
	 * 处理器的优先级,越大优先级越高越先处理
	*/
	int order() default 0;
	/**
	 * 处理的事件类型，例如message,notice
	 * @return
	 */
	String postType();
	/**
	 * 是否阻断后续的处理器执行
	 * @return
	 */
	boolean isBlock() default false;
}
