package org.accen.dmzj.util;

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
}
