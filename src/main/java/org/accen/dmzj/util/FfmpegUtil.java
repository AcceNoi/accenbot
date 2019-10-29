package org.accen.dmzj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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
	@Value("${silkv3.bin}")
	private String silkBin;
	
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
		String cmd = bin+"ffmpeg -i "+src+" -ss "+ss+" -t "+t+" -acodec "+targetFmt+" -vn "+target;
		logger.info("ffmpeg cmd: "+cmd);
		Runtime run = Runtime.getRuntime();
		try {
			String[] exe = new String[3];
			if(SystemUtil.getOs().startsWith("LINUX")) {
				exe[0] = "sh";
				exe[1] = "-c";
			}else if(SystemUtil.getOs().startsWith("WINDOWS")) {
				exe[0] = "cmd ";
				exe[1] = "/c";
			}
			exe[2] = cmd;
			Process p = run.exec(exe);
//			p.getOutputStream().close();
//			p.getInputStream().close();
//			p.getErrorStream().close();
			dealStream(p);
			p.waitFor();
			p.destroy();
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
	@Deprecated
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
	 * 要满足XX:XX:XX格式，同时如果两个参数都有值，
	 * @param time
	 * @return 如果格式错误，则返回0，正确则返回差值，单位s
	 */
	public int checkTimeIllegalEx(String beginTime,String endTime) {
		int bh = 0;int bm = 0;int bs = 0;
		int eh = 999;int em = 59;int es = 59;
		if(beginTime!=null) {
			Matcher bMatcher =  timePattern.matcher(beginTime);
			if(!bMatcher.matches()) {
				return 0;
			}else {
				bh = Integer.parseInt(bMatcher.group(1));
				bm = Integer.parseInt(bMatcher.group(2));
				bs = Integer.parseInt(bMatcher.group(3));
			}
		}
		if(endTime!=null) {
			Matcher eMatcher = timePattern.matcher(endTime);
			if(!eMatcher.matches()) {
				return 0;
			}else {
				eh = Integer.parseInt(eMatcher.group(1));
				em = Integer.parseInt(eMatcher.group(2));
				es = Integer.parseInt(eMatcher.group(3));
			}
		}
		
		return (bh*3600+bm*60+bs)-(eh*3600+em*60+es);
		
		
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
	
	private void dealStream(Process process) {
	    if (process == null) {
	        return;
	    }
	    // 处理InputStream的线程
	    new Thread() {
	        @Override
	        public void run() {
	            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String line = null;
	            try {
	                while ((line = in.readLine()) != null) {
	                    logger.info("output: " + line);
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }.start();
	    // 处理ErrorStream的线程
	    new Thread() {
	        @Override
	        public void run() {
	            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	            String line = null;
	            try {
	                while ((line = err.readLine()) != null) {
	                    logger.info("err: " + line);
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    err.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }.start();
	}
	
	/**
	 * qq录音中的silk格式转wav
	 * @param silk
	 * @return
	 */
	public String silk2Wav(String silk) {
		String[] exe = new String[3];
		if(SystemUtil.getOs().startsWith("LINUX")) {
			String cmd= silkBin+"converter.sh "+silk+" wav";
			logger.info("silk cmd: "+cmd);
			exe[0] = "sh";
			exe[1] = "-c";
		}else if(SystemUtil.getOs().startsWith("WINDOWS")) {
//			String cmd = silkBin+"silk_v3_decoder.exe "
		}
		Runtime run = Runtime.getRuntime();
		Process p;
		try {
			p = run.exec(exe);
			p.getOutputStream().close();
			p.getInputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			return silk.substring(0, silk.lastIndexOf(".silk"))+".wav";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * qq录音中的silk格式转wav
	 * @param silk
	 * @return
	 */
	public String silk2Wav(String silk) {
		String[] exe = new String[3];
		if(SystemUtil.getOs().startsWith("LINUX")) {
			String cmd= silkBin+"converter.sh "+silk+" wav";
			logger.info("silk cmd: "+cmd);
			exe[0] = "sh";
			exe[1] = "-c";
		}else if(SystemUtil.getOs().startsWith("WINDOWS")) {
//			String cmd = silkBin+"silk_v3_decoder.exe "
		}
		Runtime run = Runtime.getRuntime();
		Process p;
		try {
			p = run.exec(exe);
			p.getOutputStream().close();
			p.getInputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			return silk.substring(0, silk.lastIndexOf(".silk"))+".wav";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
