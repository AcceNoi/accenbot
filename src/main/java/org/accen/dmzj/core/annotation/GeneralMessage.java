package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.accen.dmzj.core.handler.CmdManager;
import org.accen.dmzj.core.task.GeneralTask;

/**
 * 配合{@link CmdRegular}标识方法后，将自动将方法的返回值做{@link GeneralTask}处理
 * @see CmdManager
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GeneralMessage {
	/**
	 * @see GeneralTask#setType(String)
	 * @return
	 */
	String type() default "";
	/**
	 * @see GeneralTask#setTargetId(String)
	 * @return
	 */
	String targetId() default "";
	/**
	 * @see GeneralTask#setSelfQnum(String)
	 * @return
	 */
	String selfNum() default "";
}
