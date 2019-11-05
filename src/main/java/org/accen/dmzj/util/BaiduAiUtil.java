package org.accen.dmzj.util;



import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.baidu.aip.ocr.AipOcr;
import com.baidu.aip.speech.AipSpeech;

@Component
public class BaiduAiUtil {
	private static final Logger logger = LoggerFactory.getLogger(BaiduAiUtil.class);
	@Autowired
	private CfgConfigValueMapper cfgConfigValueMapper;
	
	private final static String BAIDU_APP_ID = "BAIDU_APP_ID";
	private  String appId;
	private final static String BAIDU_API_KEY = "BAIDU_API_KEY";
	private  String apiKey;
	private final static String BAIDU_SECRET_KEY = "BAIDU_SECRET_KEY";
	private  String secretKey;
	
	private boolean inited = false;
	
	public BaiduAiUtil() {
//		init();
	}
	private synchronized void init() {
		CfgConfigValue appidCfg = cfgConfigValueMapper.selectByTargetAndKey("system", "0", BAIDU_APP_ID);
		if(appidCfg==null) {
			logger.error("Baidu appId未初始化！！！");
			return;
		}else {
			appId = appidCfg.getConfigValue();
		}
		CfgConfigValue apikeyCfg = cfgConfigValueMapper.selectByTargetAndKey("system", "0", BAIDU_API_KEY);
		if(apikeyCfg==null) {
			logger.error("Baidu apiKey未初始化！！！");
			return;
		}else {
			apiKey = apikeyCfg.getConfigValue();
		}
		CfgConfigValue secretkeyCfg = cfgConfigValueMapper.selectByTargetAndKey("system", "0", BAIDU_SECRET_KEY);
		if(secretkeyCfg==null) {
			logger.error("Baidu secret key未初始化！！！");
			return;
		}else {
			secretKey = secretkeyCfg.getConfigValue();
		}
		inited = true;
	}
	
	public String webOcr(String imgUrl) {
		
		/**
		 * @see https://ai.baidu.com/docs#/ASR-Online-Java-SDK/top
		 */
		/*AipSpeech client = new AipSpeech(appidCfg.getConfigValue(), apikeyCfg.getConfigValue(), secretkeyCfg.getConfigValue());
		JSONObject rs = client.asr(wav, "wav", 16000, null);
		*/
		/*AipOcr client = new AipOcr(appidCfg.getConfigValue(), apikeyCfg.getConfigValue(), secretkeyCfg.getConfigValue());
		HashMap<String, String> mp = new HashMap<String, String>();
		mp.put("detect_language", "true");
		JSONObject rs = client.webImageUrl(imgUrl, mp);
		return rs.toString(2);*/
		return null;
		
	}
	
	/**
	 * 组合图像审核
	 * @see https://ai.baidu.com/docs#/ImageCensoring-API/top
	 * @param url
	 * @param demensions 审核维度包含： politician（政治敏感）、antiporn（色情识别）、terror（暴恐识别）、disgust（恶心图像）、watermark（广告）
	 * @return
	 */
	public JSONObject contentCensor(String url,String... demensions) {
		if(!inited) {
			init();
		}
		if(!inited) {
			return null;
		}
		AipContentCensor client = new AipContentCensor(appId, apiKey, secretKey);
//		HashMap<String, String> options = new HashMap<String, String>();
//		options.put("", value)
		JSONObject rs = client.imageCensorComb(url, EImgType.URL,Arrays.asList(demensions), null);
		logger.info(rs.toString(2));
		return rs;
	}
}
