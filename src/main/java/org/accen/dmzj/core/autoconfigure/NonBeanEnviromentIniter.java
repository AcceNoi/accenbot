package org.accen.dmzj.core.autoconfigure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
/*
 * 用于初始化非spring管理的实例，允许使用spring管理的参数
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface NonBeanEnviromentIniter extends EnvironmentPostProcessor {
	final Pattern valuePattern = Pattern.compile("^\\$\\{(.+)\\}$");
	@Override
	default void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Field[] fields = this.getClass().getDeclaredFields();
//		ExpressionParser expressionParser = new SpelExpressionParser();
		Arrays.stream(fields).forEach(field->{
			if(field.isAnnotationPresent(Value.class)) {
				String value = field.getAnnotation(Value.class).value();
				if(value!=null&&!value.isBlank()) {
					Matcher mt = valuePattern.matcher(value);
					if(mt.matches()) {
						try {
							field.setAccessible(true);
							if(Modifier.isStatic(field.getModifiers())) {
								field.set(null, environment.getProperty(mt.group(1)));
							}else {
								field.set(this,environment.getProperty(mt.group(1)));
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					/*System.out.println(environment.getProperty("pixivc.account"));
					Expression expression = expressionParser.parseExpression(value);
					try {
						field.set(this, expression.getValue(environment));
					} catch (EvaluationException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
//					}*/
					
				}
			}
		});
	}

}
