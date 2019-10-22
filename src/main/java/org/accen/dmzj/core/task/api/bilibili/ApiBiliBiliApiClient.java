package org.accen.dmzj.core.task.api.bilibili;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.exception.BiliBiliCookieExpired;
import org.accen.dmzj.core.exception.BiliBiliCookieNeverInit;
import org.accen.dmzj.util.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class ApiBiliBiliApiClient {
	@Value("${sys.static.html.mime}")
	private String tempMimePath;//usr/local/niginx/music/

	public String sessData = null;//登陆cookie的SESSDATA
	
	public void checkCookie() throws BiliBiliCookieNeverInit {
		if(sessData==null) {
			throw new BiliBiliCookieNeverInit();
		}
	}
	private final HttpClient httpClient = HttpClientBuilder.create().build();
	private final Gson gson = new Gson();
	
	private final static Pattern urlPattern = Pattern.compile(".*?www.bilibili.com/video/av(\\d+)?.*");
	public String downLoadAdaptive(String str,int qn) throws BiliBiliCookieNeverInit {
		if(StringUtil.isNumberString(str)) {
			return downLoadByAvid(str, qn);
		}else {
			return downLoadByUrl(str, qn);
		}
	}
	public String downLoadByUrl(String url,int qn) throws BiliBiliCookieNeverInit {
		Matcher mt = urlPattern.matcher(url);
		if(mt.matches()) {
			return downLoadByAvid(mt.group(1), qn);
		}
		return null;
	}
	public String downLoadByAvid(String avid,int qn) throws BiliBiliCookieNeverInit {
		checkCookie();
		
		//1.获取cid
		String cid = getAvCid(avid);
		if(cid==null) {
			return null;
		}
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
		
		return videoName;
		
	}
	private final static String BILIBILI_API_VIEW="https://api.bilibili.com/x/web-interface/view?aid=%s";
	
	public String getAvCid(String avid) {
		HttpGet viewGet = new HttpGet(String.format(BILIBILI_API_VIEW, avid));
		try {
			HttpResponse viewResp = httpClient.execute(viewGet);
			if(viewResp.getStatusLine().getStatusCode()==200) {
				String ctt = EntityUtils.toString(viewResp.getEntity(),"UTF-8");
				
				Map<String, Object> data = gson.fromJson(ctt, Map.class);
				Double cid = (Double)((List<Map<String, Object>>)((Map<String, Object>)data.get("data")).get("pages")).get(0).get("cid");
				return new BigDecimal(cid).stripTrailingZeros().toPlainString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		urlGet.setHeader("Cookie", "SESSDATA="+sessData);
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
