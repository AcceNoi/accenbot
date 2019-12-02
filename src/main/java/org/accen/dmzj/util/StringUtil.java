package org.accen.dmzj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.SwitchPoint;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
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
	
	public static final String SPLIT = "--------------------\n";
	public static final String SPLIT_FOOT = "====================\n";
	
	/**
	 * 渲染消息的分页展示
	 * @param maxPage
	 * @param msgBuff
	 */
	public static void drawPageFoot(int maxPage,StringBuffer msgBuff) {
		if(maxPage>5) {
			//大于5，则中间以省略号展示
			msgBuff.append("[1] [2]···["+(maxPage-1)+"] ["+maxPage+"]");
		}else {
			//小于等于5，就全部展示了
			for(int i=1;i<=maxPage;i++) {
				msgBuff.append("["+i+"]");
				if(i<maxPage) {
					msgBuff.append(" ");
				}
			}
		}
		
	}
	
	/**
	 * unicode转utf-8的方法
	 * @param str
	 * @return
	 */
	public static String unicodeToString(String str) {
		 
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			String group = matcher.group(2);
			ch = (char) Integer.parseInt(group, 16);
			String group1 = matcher.group(1);
			str = str.replace(group1, ch + "");
		}
		return str;
	}
	
	private static final Pattern URL_PATTERN=Pattern.compile("^(http://|https://|ftp://)?([a-zA-Z0-9\\.\\-]+)\\.([a-zA-Z]{2,4})?(:(\\d+))?(.*)");
	/**
	 * 格式化url（暂不支持file）
	 * @param url
	 * @return 0-protocol，1-host，2-port，3-path
	 */
	public static String[] formatUrl(String url) {
		Matcher urlMatcher = URL_PATTERN.matcher(url);
		if(urlMatcher.matches()) {
			String[] fmt = new String[4] ;
			fmt[0] = StringUtils.isEmpty(urlMatcher.group(1))?"http":urlMatcher.group(1).replaceAll("://", "");//protocol
			fmt[1] = urlMatcher.group(2)+"."+urlMatcher.group(3);//host
			String port = urlMatcher.group(5);
			if(StringUtils.isEmpty(port)) {
				switch (fmt[0]) {
				case "http":
					port = "80";
					break;
				case "https":
					port = "443";
					break;
				case "ftp":
					port = "21";
					break;
				default:
					break;
				}
			}
			fmt[2] = port;
			fmt[3] = urlMatcher.group(6);
			return fmt;
		}else {
			return null;
		}
	}
	public static String is2Base64(InputStream is) {
		try {
			String bs64 = Base64.getEncoder().encodeToString(is.readAllBytes());
			is.close();
			return bs64;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
