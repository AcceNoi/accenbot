package org.accen.dmzj.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.core.pattern.parser.Parser;

public class CQUtil {
	
	/**
	 * at的CQ码
	 * @param targetQq
	 * @return
	 */
	public static String at(String targetQq) {
		return "[CQ:at,qq="+targetQq+"]";
	}
	/**
	 * 发送音乐分享的CQ码
	 * @param type qq/163/xiami
	 * @param id
	 * @return
	 */
	public static String music(String type,String id) {
//		return "[CQ:music,id="+id+",type="+type+"]";
		return "[CQ:music,id="+id+",type=163]";
	}
	/**
	 * 发送音乐分享的CQ码，但是会通过名字去匹配<br>
	 * 暂时使用各个应用提供的搜索功能去匹配第一条，未匹配到返回空串同时记录
	 * @param type
	 * @param wd
	 * @return
	 */
	public static String musicFuzzy(String type,String wd) {
		//https://music.163.com/#/search/m/?s=a&type=1
		return "";
	}
	public static String selfMusic(String url,String audio,String title,String content,String imageUrl) {
		return "[CQ:music,type=custom,url="+url+",audio="+audio+",title="+title+",content="+content+",image="+imageUrl+"]";
	}
	
	public static String imageUrl(String url) {
		return "[CQ:image,cache=0,file="+url+"]";
	}
	/**
	 * 网络语音
	 * @param url
	 * @return
	 */
	public static String recordUrl(String url) {
		return "[CQ:record,cache=0,file="+url+"]";
	}
	/**
	 * 截取at之后的字符串
	 * @param str
	 * @param selfNum
	 * @return
	 */
	public static String subAtAfter(String str,String selfNum) {
		Pattern pattern  = Pattern.compile("^\\[CQ:at,qq="+selfNum+"\\](.*)");
		Matcher mt = pattern.matcher(str);
		if(mt.matches()) {
			return mt.group(1);
		}
		return null;
	}
}
