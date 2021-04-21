package org.accen.dmzj.core.autoconfigure;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
/**
 * 初始化cmd时，同时统一初始化其工作目录
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class CmdWorkdirIniter implements BeanPostProcessor{
	private final static Logger logger = LoggerFactory.getLogger(CmdWorkdirIniter.class);
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof Workdirer wbean) {
			String workdir = wbean.workdir();
			File dir = new File(workdir);
			if(!dir.exists()) {
				logger.info("cmd:{}的工作目录{}不存在，即将创建...",  beanName,dir.getName());
				dir.mkdirs();
			}
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
