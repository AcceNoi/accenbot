package org.accen.dmzj.core;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;
import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.Order;
import org.accen.dmzj.core.autoconfigure.EventCmdPostProcessor;
import org.accen.dmzj.core.autoconfigure.EventPostProcessor;
import org.accen.dmzj.core.exception.AutowiredParamIndexException;
import org.springframework.stereotype.Component;
/**
 * 提供对AutowiredParam的支持
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class AutowiredParamHelper implements EventPostProcessor,EventCmdPostProcessor{
	public final static String quickIndexSign = "_INDEX";
	private static Map<String, Map<String,Object>> quickIndex = new HashMap<String, Map<String,Object>>();
	/**
	 * 为event生成一个索引
	 * @param event
	 */
	public static void generateIndex(Map<String, Object> event) {
		Map<String,Object> index = new HashMap<String, Object>();
		scanMap(".", event, index);
		index.put(".", event);
		//生成随机uuid标识此event
		String uuid = UUID.randomUUID().toString();
		event.put(quickIndexSign, uuid);
		quickIndex.put(uuid, index);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void scanMap(String prefix,Map<String, Object> map,Map<String, Object> index) {
		if(map!=null) {
			map.forEach((key,value)->{
				index.put(prefix+key, value);
				if(value instanceof Map sub) {
					scanMap(prefix+key+".", sub, index);
				}
			});
		}
	}
	public static void removeIndex(String index) {
		quickIndex.remove(index);
	}
	public static void removeIndex(Map<String,Object> event) {
		if(event.containsKey(quickIndexSign)) {
			removeIndex((String) event.get(quickIndexSign));
		}
	}
	public static Object catchIndex(String eventUuid,String key) {
		return quickIndex.containsKey(eventUuid)&&quickIndex.get(eventUuid).containsKey(key)?quickIndex.get(eventUuid).get(key):null;
	}
	public static boolean hasEventIndex(String eventUuid) {
		return quickIndex.containsKey(eventUuid);
	}
	
	/**
	 * 为event建立索引
	 */
	public void beforeEventPost(Map<String, Object> event) {
		generateIndex(event);
	}
	/**
	 * 结束后删除索引
	 */
	public void afterEventPostSuccess(Map<String, Object> event,AccenbotContext context) {
		removeIndex(event);
	}
	/**
	 * 失败回滚删除索引
	 */
	public void afterEventPostFaild(Map<String,Object> event) {
		removeIndex(event);
	}
	/**
	 * 处理AutowireParam
	 */
	@Order(value = 0,xvalue = 0)
	public Object eventCmdParamPost(AccenbotCmdProxy proxy,Map<String, Object> event,Parameter p,Object lastParameterValue) {
		if(!event.containsKey(AutowiredParamHelper.quickIndexSign)) {
			throw new AutowiredParamIndexException("当前event未初始化Index：%s".formatted(event.toString()));
		}
		String eventKey = (String) event.get(AutowiredParamHelper.quickIndexSign);
		if(!AutowiredParamHelper.hasEventIndex(eventKey)) {
			throw new AutowiredParamIndexException("当前event的Index不存在或已被删除：%s".formatted(eventKey));
		}
		if(p.isAnnotationPresent(AutowiredParam.class)) {
			AutowiredParam autoParam = p.getDeclaredAnnotation(AutowiredParam.class);
			String sign = null;
			if("".equals(autoParam.value())) {
				if("event".equals(p.getName())){
					sign = ".";
				}else {
					//驼峰转下划线
					sign = ".".concat(p.getName().replaceAll("[A-Z]", "_$0").toLowerCase());
				}
			}else {
				sign = autoParam.value();
			}
			return AutowiredParamHelper.catchIndex(eventKey, sign);
		}
		return lastParameterValue;
	}
	
}
