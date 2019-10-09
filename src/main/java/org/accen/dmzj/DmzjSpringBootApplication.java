package org.accen.dmzj;

import org.accen.dmzj.util.ApplicationContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
@SpringBootApplication
public class DmzjSpringBootApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DmzjSpringBootApplication.class, args);
		ApplicationContextUtil.setContext(applicationContext);
	}

}
