package org.accen.dmzj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
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
	/**
	 * 获取随机uuid
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
	/**
	 * 计算文件的sha值
	 * @param file
	 * @return
	 */
	public static String cacuSHA(File file) {
		try {
			InputStream is = new FileInputStream(file);
			MessageDigest messageDigest = MessageDigest.getInstance("SHA");
			
			byte[] buffer = new byte[1024 * 1024 * 10];
			
	        int len = 0;
	        while ((len = is.read(buffer)) > 0)
	        {
	        	messageDigest.update(buffer, 0, len);
	        }
	        is.close();
			return toHex(messageDigest.digest());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
	public static String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
}
