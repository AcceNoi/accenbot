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

import org.accen.dmzj.core.annotation.CmdMeta;
import org.accen.dmzj.core.exception.CmdRegisterDuplicateException;
import org.accen.dmzj.core.exception.CmdRegisterException;
import org.accen.dmzj.core.meta.MetaEventType;
import org.accen.dmzj.core.meta.PostType;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AccenbotMetaContext extends AccenbotContext {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TaskManager taskManager;
	List<AccenbotCmdProxy> metaCmdProxy = new LinkedList<>();
	Map<String,AccenbotCmdProxy> metaCmdProxyIndex = new HashMap<>();
	
	public AccenbotMetaContext(@Autowired @Qualifier("accenbotContext")AccenbotContext parentContext) {
		parentContext.registerContext(PostType.META_EVENT, this);
	}
	
	@Override
	public void acceptEvent(Map<String, Object> event) {
		MetaEventType metaType = MetaEventType.valueOf(((String)event.get("meta_event_type")).toUpperCase());
		metaCmdProxy.stream().forEach(proxy->{
			if(Arrays.stream(((CmdMeta)proxy.anno()).metaEventType())
						.anyMatch(avaliableMessageType -> avaliableMessageType == metaType)) {
				
				try {
					Object rs = proxy.cmdMethod().invoke(proxy.cmd(), super.autowiredParams(proxy.cmdMethod().getParameters(), event));
					GeneralTask task = super.generalMessage(rs, proxy.cmdMethod()
							,null
							,null
							,null);
					taskManager.addGeneralTask(task);
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
	
	public void parseAndRegisterClass(Object bean) {
		if(bean.getClass().isAnnotationPresent(CmdMeta.class)) {
			CmdMeta cmdMeta= bean.getClass().getDeclaredAnnotation(CmdMeta.class);
			String name = cmdMeta.value();
			String[] executeMethods = cmdMeta.executeMethod();
			if(name!=null&&name.trim().equals("")&&executeMethods.length>1) {
				throw new CmdRegisterException("当定义了name时，仅允许定义一个executeMethod，");
			}
			Map<String,List<Method>> methodMap = Arrays.stream(bean.getClass().getDeclaredMethods())
											.filter(method->!method.isAnnotationPresent(CmdMeta.class))//如果方法已经被@CmdMessage注解了则由registerMethod处理
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
																cmdMeta
														);
														logger.info("CmdMeta注册成功!name：{}",name1);
													} catch (CmdRegisterDuplicateException e) {
														logger.warn(e.getMessage());
													}
												});
									
								}else {
									//name不为空
									try {
										String name1 = parseAndRegisterMethod(bean,methodMap.get(executeMethod).get(0),name,cmdMeta);
										logger.info("CmdMeta注册成功!name：{}",name1);
									} catch (CmdRegisterDuplicateException e) {
										logger.warn(e.getMessage());
									}
								}
								
							}
						});
			}
		}
	}
	@Override
	public String parseAndRegisterMethod(Object bean,Method method) throws CmdRegisterDuplicateException{
		if(method.isAnnotationPresent(CmdMeta.class)) {
			return parseAndRegisterMethod( bean
					, method
					,defineMethodName(method, bean.getClass())
					,method.getDeclaredAnnotation(CmdMeta.class));
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
	public String parseAndRegisterMethod(Object bean,Method method,String name,Object anno) throws CmdRegisterDuplicateException{
		if(metaCmdProxyIndex.containsKey(name)) {
			throw new CmdRegisterDuplicateException(name, bean.getClass(), method);
		}else {
			metaCmdProxy.add(new AccenbotCmdProxy(name,bean, method,anno,CmdMeta.class));
			return name;
		}
	}
}
