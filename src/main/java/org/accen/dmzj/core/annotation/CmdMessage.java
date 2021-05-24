package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.accen.dmzj.core.meta.MessageSubType;
import org.accen.dmzj.core.meta.MessageType;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdMessage {
	String value() default "";
	String[] executeMethod() default {"execute"};
	int order() default 999;
	MessageType[] messageType() default MessageType._ALL;
	MessageSubType[] subType() default MessageSubType._ALL;
}
