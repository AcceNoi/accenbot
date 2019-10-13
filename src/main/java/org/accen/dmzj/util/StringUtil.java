package org.accen.dmzj.util;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class StringUtil {
	public static String transferPattern(String source) {
		if(StringUtils.isEmpty(source)) {
			return source;
		}
		return source.replace("\\", "\\\\")
				.replace("*", "\\*")
				.replace("{", "\\{")
				.replace("}", "\\}")
				.replace("[", "\\[")
				.replace("]", "\\]")
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("|", "\\|")
				.replace("^", "\\^")
				.replace("$", "\\$")
				.replace("?", "\\?")
				.replace(".", "\\.")
				.replace("&", "\\&");
	}
	/**
	 * 是否是纯数字
	 * @param source
	 * @return
	 */
	public static boolean isNumberString(String source) {
		return Pattern.matches("^\\d+$", source);
	}
}
