package org.accen.dmzj.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QuickClassParamIndexUtil {
	/**
	 * 生成Bean属性索引，暂时只支持一层（即复杂类型不会再递归）
	 * @param clazz
	 * @return
	 */
	public static Map<String, Method> generalQuickClassParamIndex(Class<?> clazz){
		Map<String, Method> index = new HashMap<String, Method>(); 
		Arrays.stream(clazz.getDeclaredMethods())
	 		.filter(method->method.getName().length()>3&&method.getName().startsWith("get")&&method.getParameterCount()==0)
	 		.forEach(getter->{
	 			String name = getter.getName().substring(3);
	 			char[] nameChar = name.toCharArray();
	 			if(nameChar[0]>='A'&&nameChar[0]<='Z') {
	 				nameChar[0] += 32;
	 				name = String.valueOf(nameChar);
	 			}
	 			index.put(name, getter);
	 		});
		return index;
	}
	
}
