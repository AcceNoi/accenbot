package org.accen.dmzj;

import org.accen.dmzj.core.feign.GloabalFeignConfigration;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableFeignClients(defaultConfiguration = GloabalFeignConfigration.class)
@ConfigurationPropertiesScan
public class DmzjSpringBootApplication {

	public static void main(String[] args) {
		System.setProperty("user.timezone","GMT +08");
		ApplicationContext applicationContext = SpringApplication.run(DmzjSpringBootApplication.class, args);
		ApplicationContextUtil.setContext(applicationContext);
		applicationContext.getBean(OnebotKotlinDaemon.class).setUpOnebotKotlin(args);
	}

}
