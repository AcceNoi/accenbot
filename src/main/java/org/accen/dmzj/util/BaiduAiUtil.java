package org.accen.dmzj.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.baidu.aip.speech.AipSpeech;

@Component
public class BaiduAiUtil {
	private static final Logger logger = LoggerFactory.getLogger(BaiduAiUtil.class);
	private CfgConfigValueMapper cfgConfigValueMapper;
	
	private final static String BAIDU_APP_ID = "BAIDU_APP_ID";
	private final static String BAIDU_API_KEY = "BAIDU_API_KEY";
	private final static String BAIDU_SECRET_KEY = "BAIDU_SECRET_KEY";
	
	public String sppechReg(String wav) {
		CfgConfigValue appidCfg = cfgConfigValueMapper.selectByTargetAndKey("system", "0", BAIDU_APP_ID);
		if(appidCfg==null) {
			logger.error("Baidu appId未初始化，无法使用语音识别功能！！！");
			return null;
		}
		CfgConfigValue apikeyCfg = cfgConfigValueMapper.selectByTargetAndKey("system", "0", BAIDU_API_KEY);
		if(apikeyCfg==null) {
			logger.error("Baidu apiKey未初始化，无法使用语音识别功能！！！");
			return null;
		}
		CfgConfigValue secretkeyCfg = cfgConfigValueMapper.selectByTargetAndKey("system", "0", BAIDU_SECRET_KEY);
		if(secretkeyCfg==null) {
			logger.error("Baidu secret key未初始化，无法使用语音识别功能！！！");
			return null;
		}
		/**
		 * @see https://ai.baidu.com/docs#/ASR-Online-Java-SDK/top
		 */
		AipSpeech client = new AipSpeech(appidCfg.getConfigValue(), apikeyCfg.getConfigValue(), secretkeyCfg.getConfigValue());
		
		
		
	}
	
}
