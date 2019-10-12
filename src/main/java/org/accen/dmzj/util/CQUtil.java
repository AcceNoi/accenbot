package org.accen.dmzj.util;

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
}
