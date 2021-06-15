package org.accen.dmzj.core;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;
import org.accen.dmzj.core.annotation.AutowiredRegular;
import org.accen.dmzj.core.annotation.CmdMessage;
import org.accen.dmzj.core.annotation.MessageRegular;
import org.accen.dmzj.core.autoconfigure.EventCmdPostProcessor;
import org.accen.dmzj.core.exception.AutowiredRegularIndexException;
import org.springframework.stereotype.Component;

@Component
public class MessageRegularHelper implements EventCmdPostProcessor{
	//TODO 检查MessageRegular的expression是否合法
	private final static String QUICK_REGULAR_INDEX = "_REGULAR";
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
	@Override
	public Object eventCmdParamPost(AccenbotCmdProxy proxy,Map<String, Object> event,Parameter p,Object lastParameterValue) {
		if(!event.containsKey(QUICK_REGULAR_INDEX)) {
			throw new AutowiredRegularIndexException("当前event未初始化regular Index：%s".formatted(event.toString()));
		}
		if(lastParameterValue==null&&p.isAnnotationPresent(AutowiredRegular.class)) {
			int regularGroup = p.getDeclaredAnnotation(AutowiredRegular.class).value();
			if(regularGroup<0) {
				//TODO 为负抛出异常
			}else if(regularGroup == 0) {
				return event.get("message");
			}else {
				//TODO 考虑Parameter的Type
				return ((String[]) event.get(QUICK_REGULAR_INDEX))[regularGroup-1];
			}
		}
		return lastParameterValue;
	}
}
