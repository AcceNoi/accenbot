package org.accen.dmzj.core.api.bilibili;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.accen.dmzj.core.api.bilibili.BilibiliApiClient.BilibiliPlayUrl;
import org.accen.dmzj.core.api.vo.BilibiliView;
import org.accen.dmzj.core.exception.BiliBiliCookieNeverInit;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiBiliBiliApiClient {
	@Autowired
	private BilibiliApiClient apiClient;
	@Value("${accenbot.persistent}")
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
	

	public String[] downloadByVNo(String vType,String vNo,int qn) throws BiliBiliCookieNeverInit{
		return downloadByVNo(vType, vNo, qn, 0);
	}
	public String[] downloadByVNo(String vType,String vNo,int qn,int p) throws BiliBiliCookieNeverInit{
		String url = null;
		String refer = null;
		BilibiliView view = null;
		if("AV".equalsIgnoreCase(vType)) {
			view = apiClient.view(vNo, null);
			BilibiliPlayUrl playUrl = apiClient.playurl(view.data().pages()[p].cid(), qn, vNo, null);
			url = playUrl.data().durl()[0].url();
			refer = "https://api.bilibili.com/x/player/playurl?cid=%d&qn=%d&aid=%s".formatted(view.data().pages()[p].cid(),qn,vNo);
		}else if("BV".equalsIgnoreCase(vType)) {
			view = apiClient.view(null, vNo);
			BilibiliPlayUrl playUrl = apiClient.playurl(view.data().pages()[p].cid(), qn, null, vNo);
			url = playUrl.data().durl()[0].url();
			refer = "https://api.bilibili.com/x/player/playurl?cid=%d&qn=%d&bvid=%s".formatted(view.data().pages()[p].cid(),qn,vNo);
		}
		String videoName = tempMimePath+vType+vNo+"_"+qn+".flv";
		File video = new File(videoName);
		if(!video.getParentFile().exists()) {
			video.getParentFile().mkdirs();
		}
		InputStream is = getDownloadRefer(url, refer);
		try {
			IOUtils.copy(is,new FileOutputStream(video));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[] {videoName,view.data().pic(),view.data().title(),"https://www.bilibili.com/video/"+vType+vNo};
	}

	
	/**
	 * 解析视频的基本信息
	 * @param avid
	 * @return 0-cid, 1-封面地址，2-标题（后续作为content）
	 */
	@Deprecated
	public String[] getAvCid(String avid) {
		Map<String, Object> data = apiClient.viewByAid(avid);
		Integer cid = (Integer)((List<Map<String, Object>>)((Map<String, Object>)data.get("data")).get("pages")).get(0).get("cid");
		String imageUrl = (String) ((Map<String, Object>)data.get("data")).get("pic");
		String title = (String) ((Map<String, Object>)data.get("data")).get("title");
		return new String[] {cid.toString(),imageUrl,title};
	}
	@Deprecated
	public String getAvidByBvid(String bvid) {
		
		Map<String, Object> data = apiClient.viewByBid(bvid);
		return ((Integer) ((Map<String, Object>)data.get("data")).get("aid")).toString();
	}
	private final static String BILIBILI_API_PLAYURL="https://api.bilibili.com/x/player/playurl?cid=%s&avid=%s&qn=%s";
	
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
	public InputStream getDownloadRefer(String downloadUrl,String refer) {
		HttpGet dlGet = new HttpGet(downloadUrl);
		dlGet.setHeader("Referer", refer);
		dlGet.setHeader("Range","bytes=0-");
		
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
