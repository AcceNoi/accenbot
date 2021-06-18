package org.accen.dmzj.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.annotation.Order;
import org.accen.dmzj.core.autoconfigure.EventCmdPostProcessor;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 封装cmd最后的处理结果，为GeneralTask
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.3
 */
@Component
public class GeneralTaskBindingHelper implements EventCmdPostProcessor{
	@Autowired
	private TaskManager taskManager;
	private static Set<String> NOTICE_TYPE_4_GROUP = Set.of("group_upload","group_admin","group_decrease","group_increase","group_ban","group_recall","notify");
	private static Set<String> NOTICE_TYPE_4_PRIVATE = Set.of("friend_recall");
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Order(value = Order.LOWEST_ORDER,xvalue = Order.LOWEST_ORDER)
	public Object afterEventCmdPost(AccenbotCmdProxy proxy,Map<String, Object> event,Object invokeResult) {
		if(invokeResult instanceof GeneralTask task) {
			taskManager.addGeneralTask(task);
		}else if(proxy.cmdMethod().isAnnotationPresent(GeneralMessage.class)) {
			GeneralMessage gm = proxy.cmdMethod().getDeclaredAnnotation(GeneralMessage.class);
			String _selfId = "".equals(gm.selfNum())?event.get("self_id").toString():gm.selfNum();
			//处理type和target，默认值
			String _type = null;
			String _targetId = null;
			if("message".equals(event.get("post_type"))) {
				if("private".equals(event.get("message_type"))) {
					_type = "private";
					_targetId = event.get("user_id").toString();
				}else if("group".equals(event.get("message_type"))) {
					_type = "group";
					_targetId = event.get("group_id").toString();
				}
			}else if("meta_event".equals(event.get("post_type"))) {
				
			}else if("notice".equals(event.get("post_type"))) {
				if(NOTICE_TYPE_4_GROUP.contains(event.get("notice_type"))) {
					_type = "group";
					_targetId = event.get("group_id").toString();
				}else if(NOTICE_TYPE_4_PRIVATE.contains(event.get("notice_type"))) {
					_type = "private";
					_targetId = event.get("user_id").toString();
				}
			}else if("request".equals(event.get("post_type"))) {
				if("group".equals(event.get("request_type"))) {
					_type = "group";
					_targetId = event.get("group_id").toString();
				}
			}
			
			_type = "".equals(gm.type())?_type:gm.type();
			_targetId = "".equals(gm.targetId())?_targetId:gm.targetId();
			final String type = _type;
			final String targetId = _targetId;
			if(type==null||targetId==null) {
				//没有有效的type和target，则不产生GeneralTask
				return null;
			}else {
				if(invokeResult.getClass().isArray()) {
					taskManager.addGeneralTasks(IntStream.range(0, Array.getLength(invokeResult))
								.mapToObj(index->Array.get(invokeResult, index) instanceof GeneralTask ot?ot:new GeneralTask(type,targetId,Array.get(invokeResult, index).toString(),_selfId))
								.toArray(GeneralTask[]::new));
				}else if(invokeResult instanceof Collection c) {
					taskManager.addGeneralTasks( (GeneralTask[]) c.stream()
							.map(o->o instanceof GeneralTask ot?ot:new GeneralTask(type,targetId,o.toString(),_selfId))
							.toArray(GeneralTask[]::new));
				}else {
					taskManager.addGeneralTask(new GeneralTask(type, targetId, invokeResult.toString(), _selfId));
				}
				return null;
			}
		}
		return null;
	}
	
}
