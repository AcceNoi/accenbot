package org.accen.dmzj.core.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.accen.dmzj.core.annotation.CmdRegular;
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
@Component
public class CmdRegularManager implements BeanPostProcessor{
	private final Logger logger = LoggerFactory.getLogger(CmdRegularManager.class);;
	public Map<String,Object[]> cmdMethodMap =new HashMap<>();
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
				Class<?>[] paramTypes = method.getParameterTypes();
				int qmessageOffset = cmdRegular.qmessageParamIndex();
				if(qmessageOffset>=0&&!Qmessage.class.isAssignableFrom(paramTypes[qmessageOffset])) {
					throw new IndexOutOfBoundsException(qmessageOffset);
				}
				int selfNumOffset = cmdRegular.selfNumParamIndex();
				if(selfNumOffset>=0&&!String.class.isAssignableFrom(paramTypes[selfNumOffset])) {
					throw new IndexOutOfBoundsException(selfNumOffset);
				}
				cmdMethodMap.put(name, new Object[] {p,cmdRegular,method,obj});
				logger.info("CmdRegular注册成功，name：{}，位置：{}#{}，expression：{}"
						,name
						,method.getDeclaringClass().getName()
						,method.getName()
						,cmdRegular.expression());
				return name;
			}catch(PatternSyntaxException e) {
				//不抛出
				logger.warn("CmdRegular expression 配置错误，当前位置：{}#{}，expression：{}"
						,method.getDeclaringClass().getName()
						,method.getName()
						,cmdRegular.expression());
				return null;
			}catch(IndexOutOfBoundsException e) {
				//不抛出
				logger.warn("CmdRegular param offset 配置错误，当前位置：{}#{}，qmessage：{}，selfnum：{}"
						,method.getDeclaringClass().getName()
						,method.getName()
						,cmdRegular.qmessageParamIndex()
						,cmdRegular.selfNumParamIndex());
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
		System.out.println(bean.getClass().getName());
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
}