package org.accen.dmzj.core.autoconfigure;

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
}
