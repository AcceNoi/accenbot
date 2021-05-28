package org.accen.dmzj.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.exception.AutowiredParamIndexException;
import org.accen.dmzj.core.exception.CmdRegisterDuplicateException;
import org.accen.dmzj.core.meta.PostType;
import org.accen.dmzj.core.task.GeneralTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 所有事件处理器的上下文
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.1
 */
@Component("accenbotContext")
public class AccenbotContext implements BeanPostProcessor{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/*private AccenbotMessageContext messageContext;
	private AccenbotMetaContext metaContext;
	private AccenbotRequestContext requestContext;
	private AccenbotNoticeContext noticeContext;*/
	private Map<PostType,AccenbotContext> contexts;
	
	protected void registerContext(PostType postType,AccenbotContext context) {
		contexts.put(postType, context);
	}
	public AccenbotContext() {
		contexts = new HashMap<>(4);
	}
	/**
	 * context需实现此方法来处理自己范围内的Event
	 * @param event
	 */
	protected void acceptEvent(Map<String, Object> event) {
		return ;
	}
	
	@SuppressWarnings({ "preview", "rawtypes", "unchecked" })
	protected GeneralTask[] generalMessage(Object result,Method m, String type,String targetId,String selfId) {
		if(result instanceof GeneralTask task) {
			return new GeneralTask[] {task};
		}else if(m.isAnnotationPresent(GeneralMessage.class)) {
			GeneralMessage gm = m.getDeclaredAnnotation(GeneralMessage.class);
			String _selfId = "".equals(gm.selfNum())?selfId:gm.selfNum();
			String _type = "".equals(gm.type())?type:gm.type();
			String _targetId = "".equals(gm.targetId())?targetId:gm.targetId();
			if(result.getClass().isArray()) {
				return IntStream.range(0, Array.getLength(result))
							.mapToObj(index->Array.get(result, index) instanceof GeneralTask ot?ot:new GeneralTask(_type,_targetId,Array.get(result, index).toString(),_selfId))
							.toArray(GeneralTask[]::new);
			}else if(result instanceof Collection c) {
				return (GeneralTask[]) c.stream()
						.map(o->o instanceof GeneralTask ot?ot:new GeneralTask(_type,_targetId,o.toString(),_selfId))
						.toArray(GeneralTask[]::new);
			}else {
				return new GeneralTask[] { new GeneralTask(_type, _targetId, result.toString(), _selfId)};
			}
			
		}else {
			return null;
		}
	}
	/**
	 * 自动填充params
	 * @param params
	 * @param event
	 * @return
	 */
	protected Object[] autowiredParams(Parameter[] params,Map<String,Object> event) {
		if(!event.containsKey(AutowiredParamHelper.quickIndexSign)) {
			throw new AutowiredParamIndexException("当前event未初始化Index：%s".formatted(event.toString()));
		}
		String eventKey = (String) event.get(AutowiredParamHelper.quickIndexSign);
		if(!AutowiredParamHelper.hasEventIndex(eventKey)) {
			throw new AutowiredParamIndexException("当前event的Index不存在或已被删除：%s".formatted(eventKey));
		}
		Object[] parameters = new Object[params.length];
		IntStream.range(0, params.length).parallel().forEach(index->{
			Parameter p = params[index];
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
				parameters[index] = AutowiredParamHelper.catchIndex(eventKey, sign);
			}else {
				parameters[index] = null;
			}
			
		});
		return parameters;
	}
	public final void accept(Map<String, Object> event) {
		AutowiredParamHelper.generateIndex(event);
		String postType = (String) event.get("post_type");
		try {
			PostType.valueOf(postType.toUpperCase());
		}catch(Exception e) {
			logger.error("PostType定义错误：{}",postType.toUpperCase());
			AutowiredParamHelper.removeIndex(event);
			return;
		}
		if(contexts.containsKey(PostType.valueOf(postType.toUpperCase()))) {
			contexts.get(PostType.valueOf(postType.toUpperCase())).acceptEvent(event);
			AutowiredParamHelper.removeIndex(event);
		}else {
			logger.warn("未定义PostType：{}对应的Context，将由AccenbotContext处理此event！");
			this.acceptEvent(event);
			AutowiredParamHelper.removeIndex(event);
			return;
		}
	}
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		registerClassAll(bean);
		Arrays.stream(bean.getClass().getDeclaredMethods())
				.filter(method->!Set.of("equals","hashCode","toString").contains(method.getName()))
				.forEach(method->{
					registerMethodAll(bean, method);
				});
		return bean;
	}
	private void registerClassAll(Object bean) {
		contexts.values().parallelStream().forEach(context->context.parseAndRegisterClass(bean));
	}
	private void registerMethodAll(Object bean,Method m) {
		contexts.values().parallelStream().forEach(context->{
			try {
				context.parseAndRegisterMethod(bean, m);
			} catch (CmdRegisterDuplicateException e) {
				logger.warn(e.getMessage());
			}
		});
			
	}
	/**
	 * context需实现此方法来注册自己的cmdProxy
	 * @param bean
	 */
	protected void parseAndRegisterClass(Object bean) {
		return;
	}
	/**
	 * context需实现此方法来注册自己的cmdProxy
	 * @param bean
	 * @param m
	 * @return
	 * @throws CmdRegisterDuplicateException
	 */
	protected String parseAndRegisterMethod(Object bean,Method m) throws CmdRegisterDuplicateException {
		return null;
	}
	
	 
	@SuppressWarnings("preview")
	public record AccenbotCmdProxy(String name,Object cmd,Method cmdMethod,Object anno,Class<? extends Annotation> annoClass){}
	
	/**
	 * 获取一个方法的名字
	 * @param method
	 * @return
	 */
	protected String defineMethodName(Method method,Class<?> clazz) {
		return clazz.getName()+"#"+method.getName();
	}

	public void register(Object cmd,Method cmdMethod) {
		
	}
}
