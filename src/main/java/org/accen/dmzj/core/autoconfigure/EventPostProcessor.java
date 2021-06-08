package org.accen.dmzj.core.autoconfigure;

import java.util.Map;

import org.accen.dmzj.core.AccenbotContext;
/**
 * AccenbotContex的事件处理器前后预置，{@link AccenbotContext#accept(Map)}
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
public interface EventPostProcessor {
	default public void beforeEventPost(Map<String, Object> event) {};
	default public void afterEventPostSuccess(Map<String, Object> event,AccenbotContext context) {};
	default public void afterEventPostFaild(Map<String,Object> event) {};
}
