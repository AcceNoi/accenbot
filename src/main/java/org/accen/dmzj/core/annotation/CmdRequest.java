package org.accen.dmzj.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.accen.dmzj.core.meta.RequestSubType;
import org.accen.dmzj.core.meta.RequestType;
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdRequest {
	String value() default "";
	RequestType[] requestType() default RequestType._ALL;
	RequestSubType[] subType() default RequestSubType._ALL;
	String[] executeMethod() default {"execute"};
	boolean enableAutowiredParam() default true;
	int order() default 999;
}
