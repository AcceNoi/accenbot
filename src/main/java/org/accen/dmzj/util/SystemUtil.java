package org.accen.dmzj.util;

import java.util.Properties;

public class SystemUtil {
	/**
	 * 获取当前系统的文件夹符
	 * @return
	 */
	public static String getFileSeperate() {
		String osName = getSystemProperty("os.name");
		if(osName.matches("Windows.*")) {
			return "\\";
		}else {
			return "/";
		}
	}
	public static String getOs() {
		return getSystemProperty("os.name").toUpperCase();
	}
	/**
	 * 获取系统变量
	 * @param var
	 * @return
	 */
	private static String getSystemProperty(String var) {
		Properties props = System.getProperties();
		return props.getProperty(var);
	}
	
}
