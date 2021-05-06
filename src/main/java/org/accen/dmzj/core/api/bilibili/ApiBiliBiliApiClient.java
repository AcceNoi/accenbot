package org.accen.dmzj.core.api.bilibili;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.exception.BiliBiliCookieNeverInit;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class ApiBiliBiliApiClient {
	@Autowired
	private BilibiliApiClient apiClient;
	@Value("${sys.static.html.mime}")
	private String tempMimePath;//usr/local/niginx/music/

	@Autowired
	private CfgConfigValueMapper configMapper;
	private final static String B_COOKIE_NAME="SESS_DATA";
	private String sessData = null;//登陆cookie的SESSDATA
	
	public void setSessData(String sessData) {
		this.sessData = sessData;
		CfgConfigValue config = configMapper.selectByTargetAndKey("system", "0", B_COOKIE_NAME);
		if(config==null) {
			config = new CfgConfigValue();
			config.setTargetType("system");config.setTarget("0");config.setConfigKey(B_COOKIE_NAME);config.setConfigValue(sessData);
			configMapper.insert(config);
		}else {
			config.setConfigValue(sessData);
			configMapper.updateValue(config);
		}
	}
	public String getSessData() {
		if(this.sessData==null) {
			CfgConfigValue config = configMapper.selectByTargetAndKey("system", "0", B_COOKIE_NAME);
			if(config!=null) {
				this.sessData = config.getConfigValue();
			}
		}
		return this.sessData;
	}
	
	public void checkCookie() throws BiliBiliCookieNeverInit {
		if(getSessData()==null) {
			throw new BiliBiliCookieNeverInit();
		}
	}
	private final HttpClient httpClient = HttpClientBuilder.create().build();
	private final Gson gson = new Gson();
	
	private final static Pattern urlPattern = Pattern.compile(".*?(bilibili|b23).(com|tv)/(video/){0,1}(av|AV|BV|bv)([0-9a-zA-Z]+)?.*");
	/**
	 * 
	 * @param str
	 * @param qn
	 * @return 0-本地视频地址, 1-封面地址，2-标题（后续作为content），3-视频地址
	 * @throws BiliBiliCookieNeverInit
	 */
	@Deprecated
	public String[] downLoadAdaptive(String str,int qn) throws BiliBiliCookieNeverInit {
		if(StringUtil.isNumberString(str)) {
			return downLoadByAvid(str, qn);
		}else {
			return downLoadByUrl(str, qn);
		}
	}
	/**
	 * 
	 * @param url
	 * @param qn
	 * @return 0-本地视频地址, 1-封面地址，2-标题（后续作为content），3-视频地址
	 * @throws BiliBiliCookieNeverInit
	 */
	@Deprecated
	public String[] downLoadByUrl(String url,int qn) throws BiliBiliCookieNeverInit {
		Matcher mt = urlPattern.matcher(url);
		if(mt.matches()) {
			if("av".equalsIgnoreCase(mt.group(4))) {
				return downLoadByAvid(mt.group(5), qn);
			}else if("bv".equalsIgnoreCase(mt.group(4))) {
				return downloadByBvid(mt.group(5), qn);
			}
			
		}
		return null;
	}
	public String[] downloadByVNo(String vType,String vNo,int qn) throws BiliBiliCookieNeverInit{
		if("AV".equalsIgnoreCase(vType)) {
			return downLoadByAvid(vNo, qn);
		}else if("BV".equalsIgnoreCase(vType)) {
			return downloadByBvid(vNo, qn);
		}
		return null;
	}

	/**
	 * 同构bv号获取视频信息
	 * @param bvid
	 * @param qn
	 * @return
	 * @throws BiliBiliCookieNeverInit
	 */
	private String[] downloadByBvid(String bvid,int qn) throws BiliBiliCookieNeverInit {
		checkCookie();
		String avid = getAvidByBvid(bvid);
		if(avid==null) {
			return null;
		}
		return downLoadByAvid(avid, qn);
	}
	
	/**
	 * 
	 * @param avid
	 * @param qn
	 * @return 0-本地视频地址, 1-封面地址，2-标题（后续作为content）,3-视频地址
	 * @throws BiliBiliCookieNeverInit
	 */
	private String[] downLoadByAvid(String avid,int qn) throws BiliBiliCookieNeverInit {
		checkCookie();
		
		//1.获取cid
		String[] rs = getAvCid(avid);
		if(rs==null) {
			return null;
		}
		String cid = rs[0];
		
		//2.获取playurl
		String url = getUrl(cid, avid, qn);
		if(url==null) {
			return null;
		}
		//3.下载
		String videoName = tempMimePath+avid+"_"+qn+".flv";
		File video = new File(videoName);
		if(!video.getParentFile().exists()) {
			video.getParentFile().mkdirs();
		}
		InputStream is = getDownload(url, avid);
		try {
			IOUtils.copy(is,new FileOutputStream(video));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String[] {videoName,rs[1],rs[2],"https://www.bilibili.com/video/av"+avid};
		
	}
//	private final static String BILIBILI_API_VIEW="https://api.bilibili.com/x/web-interface/view?aid=%s";
	
	/**
	 * 解析视频的基本信息
	 * @param avid
	 * @return 0-cid, 1-封面地址，2-标题（后续作为content）
	 */
	public String[] getAvCid(String avid) {
		Map<String, Object> data = apiClient.viewByAid(avid);
		Integer cid = (Integer)((List<Map<String, Object>>)((Map<String, Object>)data.get("data")).get("pages")).get(0).get("cid");
		String imageUrl = (String) ((Map<String, Object>)data.get("data")).get("pic");
		String title = (String) ((Map<String, Object>)data.get("data")).get("title");
		return new String[] {cid.toString(),imageUrl,title};
	}
	public String getAvidByBvid(String bvid) {
		
		Map<String, Object> data = apiClient.viewByBid(bvid);
		return ((Integer) ((Map<String, Object>)data.get("data")).get("aid")).toString();
	}
	private final static String BILIBILI_API_PLAYURL="https://api.bilibili.com/x/player/playurl?cid=%s&avid=%s&qn=%s";
	public String getUrl(String cid,String avid,int qn) {
		int qni = 16;//默认360p
		switch(qn) {
		case 1080:qni = 80;break;
		case 720:qni = 64;break;
		case 480:qni = 32;break;
		case 360:qni = 16;break;
		default:break;
		}
		HttpGet urlGet = new HttpGet(String.format(BILIBILI_API_PLAYURL, cid,avid,""+qni));
		urlGet.setHeader("Cookie", "SESSDATA="+getSessData());
		urlGet.setHeader("Host", "api.bilibili.com");
		try {
			HttpResponse viewResp = httpClient.execute(urlGet);
			if(viewResp.getStatusLine().getStatusCode()==200) {
				String ctt = EntityUtils.toString(viewResp.getEntity(),"UTF-8");
				
				Map<String, Object> data = gson.fromJson(ctt, Map.class);
				String url= ((List<Map<String, Object>>)((Map<String, Object>)data.get("data")).get("durl")).get(0).get("url").toString();
				return url;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public InputStream getDownload(String downloadUrl,String avid) {
		HttpGet dlGet = new HttpGet(downloadUrl);
		dlGet.setHeader("Referer", "https://api.bilibili.com/x/web-interface/view?aid="+avid);
		dlGet.setHeader("Range","bytes=0-");
		dlGet.setHeader("Origin", "https://www.bilibili.com");
		
		try {
			HttpResponse dlResp = httpClient.execute(dlGet);
			return dlResp.getEntity().getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
