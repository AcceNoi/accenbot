package org.accen.dmzj.core.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.AutowiredRegular;
import org.accen.dmzj.core.annotation.CmdRegular;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.exception.AutowiredParamException;
import org.accen.dmzj.core.exception.CmdRegularException;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.QuickClassParamIndexUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 管理cmdRegular对象的注册与移除
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Deprecated
@Component
public class CmdRegularManager implements BeanPostProcessor{
	private final Logger logger = LoggerFactory.getLogger(CmdRegularManager.class);;
	public Map<String,Object[]> cmdMethodMap =new HashMap<>();
	/**
	 * AutowiredParam的属性索引
	 */
	private Map<String,Method> quickAutowiredParamIndex;
	/**
	 * 初始化AutowiredParam的属性索引 TODO 支持record TODO 支持递归
	 * @param AutowiredParamClass
	 */
	protected void initAutowiredParamIndex(Class<?> AutowiredParamClass) {
		quickAutowiredParamIndex = new HashMap<String, Method>();
		quickAutowiredParamIndex.putAll(QuickClassParamIndexUtil.generalQuickClassParamIndex(AutowiredParamClass));
	}
	private Class<?> autowiredParamClass = Qmessage.class;
	public CmdRegularManager() {
		initAutowiredParamIndex(autowiredParamClass);
	}
	
	@SuppressWarnings("preview")
	public void accept(Qmessage qmessage,TaskManager taskManager) {
		this.cmdMethodMap.forEach((name,cmdMethod)->{
			Matcher mt = ((Pattern) cmdMethod[0]).matcher(qmessage.getMessage());
			if(mt.matches()) {
				long mtGroupProcessBitmap = 0;//mt group的实时处理情况
				long paramProcessBitmap = 0;//param的实时处理情况
				CmdRegular cr = (CmdRegular) cmdMethod[1];
				Method mtd = (Method) cmdMethod[2];
				Parameter[] parameters = mtd.getParameters();
				Object obj = cmdMethod[3];
				int groupCount = mt.groupCount();
				
				Object[] params = new Object[parameters.length];
				for(int paramIndex= 0;paramIndex<parameters.length;paramIndex++) {
					//是否是qmessage类型
					if(parameters[paramIndex].getType().isAssignableFrom(autowiredParamClass)) {
						params[paramIndex] =  qmessage;
						paramProcessBitmap|=1<<paramIndex;
					//是否由AutowiredParam处理
					}else if(cr.enableAutowiredParam()&&parameters[paramIndex].isAnnotationPresent(AutowiredParam.class)) {
						//计算
						try {
							params[paramIndex] = quickAutowiredParamIndex
								.get(getAutowiredParamName(parameters[paramIndex]))
								.invoke(qmessage);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						//更新param的实时处理情况
						paramProcessBitmap|=1<<paramIndex;
					//是否由AutowiredRegular处理
					}else if(parameters[paramIndex].isAnnotationPresent(AutowiredRegular.class)&&parameters[paramIndex].getDeclaredAnnotation(AutowiredRegular.class).value()>0) {
						//计算
						int groupIndex = parameters[paramIndex].getDeclaredAnnotation(AutowiredRegular.class).value();
						params[paramIndex] = autoTypeCast(parameters[paramIndex]
								, mt.group(groupIndex));
						//更新mt group和param的实时处理情况，注意由于group是从1开始计数的，所以需要-1
						mtGroupProcessBitmap|=1<<(groupIndex-1);
						paramProcessBitmap|=1<<paramIndex;
					}
				}
				//其他顺序排放
				for(int paramIndex = 0;paramIndex<parameters.length;paramIndex++) {
					if(((paramProcessBitmap>>paramIndex)&(1<<paramIndex))>>paramIndex==1) {
						//当前位为1，说明已经有值了
						continue;
					}else {
						//不为1，说明没有值，则从group中顺序取还没有被使用的值
						//TODO 这里可以优化，没必要从0开始
						for(int mtGroupIndex = 0;mtGroupIndex<groupCount;mtGroupIndex++) {
							if(((mtGroupProcessBitmap>>mtGroupIndex)&(1<<mtGroupIndex))>>mtGroupIndex==1) {
								//当前位为1，说明已经使用了，换下一个
								continue;
							}else {
								params[paramIndex]  = autoTypeCast(parameters[paramIndex], mt.group(mtGroupIndex+1));
								//更新mt group和param的实时处理情况
								mtGroupProcessBitmap|=1<<(mtGroupIndex);
								paramProcessBitmap|=1<<paramIndex;
								break ;
							}
						}
					}
				}
				
				try {
					Object rt = mtd.invoke(obj, params);
					if(rt instanceof GeneralTask task) {
						taskManager.addGeneralTask(task);
					}else if(mtd.isAnnotationPresent(GeneralMessage.class)&&rt instanceof String message) {
						taskManager.addGeneralTaskQuick(
								qmessage.getEvent().get("self_id").toString()
								, qmessage.getMessageType()
								, qmessage.getGroupId()
								, message);
					}
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
	
	/**
	 * 注册一个CmdRegular
	 * @param method
	 * @return if {@link CmdRegular} valid return the unique name of this Manager, else return null 
	 */
	public String registerCmdRegular(Method method,Object obj) {
		if(method.isAnnotationPresent(CmdRegular.class)) {
			CmdRegular cmdRegular = method.getAnnotation(CmdRegular.class);
			try{
				Pattern p = Pattern.compile(cmdRegular.expression());
				String name = StringUtils.hasLength(cmdRegular.name())?cmdRegular.name().trim():nonDuplicateRegularName(method.getDeclaringClass().getName().concat("#").concat(method.getName()));
				checkAutowiredParam(method, name,cmdRegular);
				cmdMethodMap.put(name, new Object[] {p,cmdRegular,method,obj});
				logger.info("CmdRegular注册成功，name：{}，位置：{}#{}，expression：{}"
						,name
						,method.getDeclaringClass().getName()
						,method.getName()
						,cmdRegular.expression());
				return name;
			}catch(PatternSyntaxException e) {
				//不抛出
				logger.error("CmdRegular expression 配置错误，当前位置：{}#{}，expression：{}"
						,method.getDeclaringClass().getName()
						,method.getName()
						,cmdRegular.expression());
				return null;
			}catch(AutowiredParamException e) {
				//不抛出
				logger.error("CmdRegular autowiredparam 配置错误，当前位置：{}#{}>>>详细：{}"
						,method.getDeclaringClass().getName()
						,method.getName()
						,e.getMessage());
				return null;
			} catch (CmdRegularException e) {
				//不抛出
				logger.error("CmdRegular 配置错误，当前位置：{}#{}>>>详细：{}"
						,method.getDeclaringClass().getName()
						,method.getName()
						,e.getMessage());
				return null;
			}
		}else {
			return null;
		}
	}
	/**
	 * 移除一个CmdRegular
	 * @param name
	 * @return
	 */
	public boolean deregister(String name) {
		if(cmdMethodMap.containsKey(name)) {
			cmdMethodMap.remove(name);
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 注册cmdMethod
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Arrays.stream(bean.getClass().getDeclaredMethods())
				.filter(method->!Set.of("equals","hashCode","toString").contains(method.getName()))
				.forEach(method->registerCmdRegular(method,bean));
		return bean;
	}
	
	/**
	 * 获取一个不重复的cmdRegular名
	 * @param name
	 * @return
	 */
	private String nonDuplicateRegularName(String name) {
		return cmdMethodMap.containsKey(name)?nonDuplicateRegularName(name,1):name;
	}
	/**
	 * @param name
	 * @param sign
	 * @return
	 */
	private String nonDuplicateRegularName(String name,int sign) {
		return cmdMethodMap.containsKey(name+":"+sign)?nonDuplicateRegularName(name, ++sign):(name+":"+sign);
	}
	/**
	 * 检查AutowiredParam的配置
	 * @param mtd
	 * @param methodName
	 * @throws AutowiredParamException
	 */
	private void checkAutowiredParam(Method mtd,String methodName,CmdRegular cr) throws CmdRegularException,AutowiredParamException{
		for(Parameter parameter:mtd.getParameters()) {
			if(parameter.getDeclaredAnnotation(AutowiredParam.class)!=null) {
				if(!cr.enableAutowiredParam()) {
					throw new CmdRegularException(methodName);
				}
				String name = getAutowiredParamName(parameter);
				if(!(quickAutowiredParamIndex.containsKey(name)&&parameter.getType().isAssignableFrom(quickAutowiredParamIndex.get(name).getReturnType()))) {
					throw new AutowiredParamException(methodName,name);
				}
			}
		}
	}
	private String getAutowiredParamName(Parameter parameter) {
		String name = parameter.getDeclaredAnnotation(AutowiredParam.class).value().trim();
		if(!StringUtils.hasText(name)) {
			name = parameter.getName();
		}
		return name;
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