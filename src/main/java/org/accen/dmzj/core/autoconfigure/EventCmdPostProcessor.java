package org.accen.dmzj.core.autoconfigure;

import java.lang.reflect.Parameter;
import java.util.Map;

import org.accen.dmzj.core.AccenbotContext;
import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;

/**
 * cmd执行器前后预置，{@link AccenbotContext#acceptEvent}
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
public interface EventCmdPostProcessor {
	default public boolean beforeEventCmdPost(AccenbotCmdProxy proxy,Map<String, Object> event) {return true;}
	default public Object afterEventCmdPost(AccenbotCmdProxy proxy,Map<String, Object> event,Object invokeResult) {return invokeResult;}

	/**
	 * 处理proxy执行的方法参数
	 * @param proxy
	 * @param event
	 * @param p
	 * @param lastParameterValue 默认该parameter的值
	 * @return
	 */
	default public Object eventCmdParamPost(AccenbotCmdProxy proxy,Map<String, Object> event,Parameter p,Object lastParameterValue) {return lastParameterValue;}
}
