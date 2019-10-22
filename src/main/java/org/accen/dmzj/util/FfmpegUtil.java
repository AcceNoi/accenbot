package org.accen.dmzj.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FfmpegUtil {
	@Value("${ffmpeg.bin}")
	private String ffmpegBin ;
	
	private static final Logger logger = LoggerFactory.getLogger(FfmpegUtil.class);
	/**
	 * 将原视频转化为音频
	 * @param src 原视频url，支持本地文件
	 * @param target 目标文件
	 * @param targetFmt 目标格式，建议aac
	 * @param ss 截取开始时间
	 * @param t 截取结束时间
	 * @return
	 */
	public String convertVideo2Audio(String src,String target,String targetFmt,String ss,String t) {
		String bin = ffmpegBin;
		String cmd = "ffmpeg -i "+src+" -ss "+ss+" -t "+t+" -acodec "+targetFmt+" -vn "+target;
		logger.info("ffmpeg cmd: "+cmd);
		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec(bin+cmd);
			p.getOutputStream().close();
			p.getInputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			return target;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			 run.freeMemory();
		}
		return null;
	}
	
	private final static Pattern timePattern = Pattern.compile("^(\\d)+:([0-5][0-9]):([0-5][0-9])$");
	/**
	 * 要满足XX:XX:XX格式，同时如果两个参数都有值，
	 * @param time
	 * @return
	 */
	public boolean checkTimeIllegal(String beginTime,String endTime) {
		int bh = 0;int bm = 0;int bs = 0;
		int eh = 999;int em = 59;int es = 59;
		if(beginTime!=null) {
			Matcher bMatcher =  timePattern.matcher(beginTime);
			if(!bMatcher.matches()) {
				return false;
			}else {
				bh = Integer.parseInt(bMatcher.group(1));
				bm = Integer.parseInt(bMatcher.group(2));
				bs = Integer.parseInt(bMatcher.group(3));
			}
		}
		if(endTime!=null) {
			Matcher eMatcher = timePattern.matcher(endTime);
			if(!eMatcher.matches()) {
				return false;
			}else {
				eh = Integer.parseInt(eMatcher.group(1));
				em = Integer.parseInt(eMatcher.group(2));
				es = Integer.parseInt(eMatcher.group(3));
			}
		}
		
		if(eh<=bh&&em<=bm&&es<=bs) {
			return false;
		}
		return true;
	}
	/**
	 * 判断当前服务器是否安装有ffmpeg
	 * @return
	 */
	public boolean isFfmpegExists() {
		String bin = ffmpegBin;
		if(!new File(bin+"ffmpeg").exists()) {
			return false;
		}
		if(!new File(bin+"ffmpeg.exe").exists()) {
			return false;
		}
		return true;
	}
}
