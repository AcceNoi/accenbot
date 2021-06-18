package org.accen.dmzj.core;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;
import org.accen.dmzj.core.annotation.AutowiredRegular;
import org.accen.dmzj.core.annotation.CmdMessage;
import org.accen.dmzj.core.annotation.MessageRegular;
import org.accen.dmzj.core.annotation.Order;
import org.accen.dmzj.core.autoconfigure.EventCmdPostProcessor;
import org.accen.dmzj.core.autoconfigure.ProxyPostProcessor;
import org.accen.dmzj.core.exception.AutowiredRegularIndexException;
import org.accen.dmzj.core.exception.MessageRegularException;
import org.springframework.stereotype.Component;
/**
 * 对MessageRegular的支持<br>
 * 1.对regular进行检查<br>
 * 2.对proxy的param进行封装
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class MessageRegularHelper implements EventCmdPostProcessor,ProxyPostProcessor{
	/**
	 * 检查MessageRegular的expression、regular是否合法
	 * @param context
	 * @param proxy
	 * @return
	 */
	@Override
	public AccenbotCmdProxy beforeRegisterProxy(AccenbotContext context,AccenbotCmdProxy proxy) {
		if(proxy.annoClass()==CmdMessage.class&&proxy.cmdMethod().isAnnotationPresent(MessageRegular.class)) {
			String expression = proxy.cmdMethod().getDeclaredAnnotation(MessageRegular.class).expression();
			if(expression==null||expression.isBlank()) {
				throw new MessageRegularException("expression不能为空!location：%s".formatted(proxy.cmdMethod().toGenericString()));
			}else {
				try{
					Pattern.compile(expression);
				}catch(PatternSyntaxException e) {
					throw new MessageRegularException("expression配置错误，必须为正则式!location：%s".formatted(proxy.cmdMethod().toGenericString()));
				}
			}
			if(Arrays.stream(proxy.cmdMethod().getParameters())
					.anyMatch(p->{
						if(p.isAnnotationPresent(AutowiredRegular.class)) {
							return p.getDeclaredAnnotation(AutowiredRegular.class).value()<0;
						}else {
							return false;
						}
					})) {
				throw new MessageRegularException("regular配置错误，值必须为自然数！location：%s".formatted(proxy.cmdMethod().toGenericString()));
			}
		}
		return proxy;
	}
	private final static String QUICK_REGULAR_INDEX = "_REGULAR";
	/**
	 * cmd_message在接受event前，检查表达式是否通过，通过则建立索引
	 */
	@Override
	public boolean beforeEventCmdPost(AccenbotCmdProxy proxy,Map<String, Object> event) {
		if(proxy.annoClass()==CmdMessage.class&&proxy.cmdMethod().isAnnotationPresent(MessageRegular.class)) {
			String expression = proxy.cmdMethod().getDeclaredAnnotation(MessageRegular.class).expression();
			String message = (String) event.get("message");
			Pattern p = Pattern.compile(expression);
			Matcher m = p.matcher(message);
			if(!m.matches()) {
				return false;
			}else {
				int gpct = m.groupCount();
				if(gpct>0) {
					String[] groupResults = IntStream.range(1, gpct+1)
														.mapToObj(groupIndex->m.group(groupIndex))
														.toArray(String[]::new);
					event.put(QUICK_REGULAR_INDEX, groupResults);
				}
				return true;
			}
		}
		return true;
	}
	/**
	 * cmd执行时，通过前一步建立的索引，封装param
	 */
	@Override
	@Order(value = 0,xvalue = 1)
	public Object eventCmdParamPost(AccenbotCmdProxy proxy,Map<String, Object> event,Parameter p,Object lastParameterValue) {
		if(lastParameterValue==null&&p.isAnnotationPresent(AutowiredRegular.class)) {
			if(!event.containsKey(QUICK_REGULAR_INDEX)) {
				throw new AutowiredRegularIndexException("当前event未初始化regular Index：%s".formatted(event.toString()));
			}
			int regularGroup = p.getDeclaredAnnotation(AutowiredRegular.class).value();
			if(regularGroup<0) {
				//TODO 为负抛出异常
			}else if(regularGroup == 0) {
				return event.get("message");
			}else {
				String v = ((String[]) event.get(QUICK_REGULAR_INDEX))[regularGroup-1];
				return autoTypeCast(p, v);
			}
		}
		return lastParameterValue;
	}
	private Object autoTypeCast(Parameter parameter,String groupValue) {
		if(parameter.getType()==byte.class||parameter.getType()==Byte.class) {
			return Byte.valueOf(groupValue);
		}else if(parameter.getType()==int.class||parameter.getType()==Integer.class) {
			return Integer.valueOf(groupValue);
		}else if(parameter.getType()==long.class||parameter.getType()==Long.class) {
			return Long.valueOf(groupValue);
		}else if(parameter.getType()==float.class||parameter.getType()==Float.class) {
			return Float.valueOf(groupValue);
		}else if(parameter.getType()==double.class||parameter.getType()==Double.class) {
			return Double.valueOf(groupValue);
		}else if(parameter.getType()==char.class||parameter.getType()==Character.class) {
			return groupValue.charAt(0);
		}else {
			return groupValue;
		}
	}
}
