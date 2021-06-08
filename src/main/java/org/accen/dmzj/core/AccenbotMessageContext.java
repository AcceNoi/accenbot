package org.accen.dmzj.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.accen.dmzj.core.annotation.CmdMessage;
import org.accen.dmzj.core.autoconfigure.EventCmdPostProcessor;
import org.accen.dmzj.core.exception.CmdRegisterDuplicateException;
import org.accen.dmzj.core.exception.CmdRegisterException;
import org.accen.dmzj.core.meta.MessageSubType;
import org.accen.dmzj.core.meta.MessageType;
import org.accen.dmzj.core.meta.PostType;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AccenbotMessageContext extends AccenbotContext {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TaskManager taskManager;
	
	List<AccenbotCmdProxy> messageCmdProxy = new LinkedList<>();
	Map<String,AccenbotCmdProxy> messageCmdProxyIndex = new HashMap<>();
	
	private AccenbotContext parentContext;
	
	public AccenbotMessageContext(@Autowired @Qualifier("accenbotContext")AccenbotContext parentContext) {
		this.parentContext = parentContext;
		parentContext.registerContext(PostType.MESSAGE, this);
	}
	
	@Override
	public void acceptEvent(Map<String, Object> event) {
		MessageType messageType = MessageType.valueOf(((String)event.get("message_type")).toUpperCase());
		MessageSubType subType = event.containsKey("sub_type")?MessageSubType.valueOf(((String)event.get("sub_type")).toUpperCase()):MessageSubType._ALL;
		messageCmdProxy.stream().forEach(proxy->{
			if(Arrays.stream(((CmdMessage)proxy.anno()).messageType())
						.anyMatch(avaliableMessageType -> (avaliableMessageType == MessageType._ALL||avaliableMessageType == messageType))
				&&
				Arrays.stream(((CmdMessage)proxy.anno()).subType())
						.anyMatch(avaliableSubType -> (avaliableSubType == MessageSubType._ALL||avaliableSubType == subType))
			) {
				
				//cmd执行前的预处理，可以控制是否执行
				if(!parentContext.eventCmdPostProcessors.parallelStream().allMatch(p->p.beforeEventCmdPost(proxy, event))) {
					return;
				}
				
				try {
					Object rs = proxy.cmdMethod().invoke(proxy.cmd(), super.autowiredParams(proxy.cmdMethod().getParameters(), event));
					//cmd执行后的后处理，可以对cmd结果进行格式化，但是此方法比较危险，EventCmdPostProcessor互相可以影响
					for(EventCmdPostProcessor p:parentContext.eventCmdPostProcessors) {
						rs = p.afterEventCmdPost(proxy, event, rs);
					}
					
					GeneralTask[] tasks = super.generalMessage(rs, proxy.cmdMethod()
							, (String)event.get("message_type")
							, "group".equals(event.get("message_type"))?""+event.get("group_id"):""+event.get("user_id")
							, ""+event.get("self_id"));
					taskManager.addGeneralTasks(tasks);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void parseAndRegisterClass(Object bean) {
		
		if(bean.getClass().isAnnotationPresent(CmdMessage.class)) {
			CmdMessage cmdMessage = bean.getClass().getDeclaredAnnotation(CmdMessage.class);
			String name = cmdMessage.value();
			String[] executeMethods = cmdMessage.executeMethod();
			if(name!=null&&name.trim().equals("")&&executeMethods.length>1) {
				throw new CmdRegisterException("当定义了name时，仅允许定义一个excuteMethod，");
			}
			Map<String,List<Method>> methodMap = Arrays.stream(bean.getClass().getDeclaredMethods())
											.filter(method->!method.isAnnotationPresent(CmdMessage.class))//如果方法已经被@CmdMessage注解了则由registerMethod处理
											.collect(Collectors.groupingBy(Method::getName));
			
			if(executeMethods.length>0) {
				Arrays.stream(executeMethods)
						.forEach(executeMethod->{
							if(methodMap.keySet().contains(executeMethod)) {
								if(name==null||name.trim().equals("")) {
									//name为空，则所有重载了此方法名的方法全部注册进去
									IntStream.range(0, methodMap.get(executeMethod).size())
												.forEach(index->{
													String uniqueName = defineMethodName(methodMap.get(executeMethod).get(index),bean.getClass());
													try {
														String name1 = parseAndRegisterMethod(
																bean, 
																methodMap.get(executeMethod).get(index),index==0?uniqueName:(uniqueName+":"+(index-1)),
																cmdMessage												
														);
														logger.info("CmdMessage注册成功!name：{}",name1);
													} catch (CmdRegisterDuplicateException e) {
														logger.warn(e.getMessage());
													}
												});
									
								}else {
									//name不为空
									try {
										String name1 = parseAndRegisterMethod(bean,methodMap.get(executeMethod).get(0),name,cmdMessage);
										logger.info("CmdMessage注册成功!name：{}",name1);
									} catch (CmdRegisterDuplicateException e) {
										logger.warn(e.getMessage());
									}
								}
								
							}
						});
			}
		}
	}
	/**
	 * 注册一个cmdProxy
	 * @param bean
	 * @param method
	 * @return name
	 */
	@Override
	public String parseAndRegisterMethod(Object bean,Method method) throws CmdRegisterDuplicateException{
		if(method.isAnnotationPresent(CmdMessage.class)) {
			return parseAndRegisterMethod( bean
					, method
					,defineMethodName(method, bean.getClass())
					,method.getDeclaredAnnotation(CmdMessage.class)
					);
		}else {
			return null;
		}
		
	}
	/**
	 * 注册一个cmdProxy
	 * @param bean
	 * @param method
	 * @return name
	 */
	public String parseAndRegisterMethod(Object bean,Method method,Object anno) throws CmdRegisterDuplicateException{
		return parseAndRegisterMethod( bean, method,defineMethodName(method, bean.getClass()),anno);
	}
	/**
	 * 注册一个cmdProxy
	 * @param bean
	 * @param method
	 * @param name
	 * @return name
	 */
	private String parseAndRegisterMethod(Object bean,Method method,String name,Object anno) throws CmdRegisterDuplicateException{
		if(messageCmdProxyIndex.containsKey(name)) {
			throw new CmdRegisterDuplicateException(name, bean.getClass(), method);
		}else {
			messageCmdProxy.add(new AccenbotCmdProxy(name,bean, method,anno,CmdMessage.class));
			return name;
		}
		
	}
}
