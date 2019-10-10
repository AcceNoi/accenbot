package org.accen.dmzj.core.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import feign.codec.Encoder;
import feign.gson.GsonEncoder;

/**
 * 这是为了适应不同feignClient做的改进，参考了<a href="https://www.imooc.com/article/details/id/31245">https://www.imooc.com/article/details/id/31245</a>
 * @author Accen
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignApi {
	String host();
	/**
	 * 自定义encoder
	 * @return
	 */
	Class<? extends Encoder> encoder() default GsonEncoder.class;//TODO 这里的默认最好弄成form/muilti/json自适应的encoder
}
