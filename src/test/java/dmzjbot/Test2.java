package dmzjbot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.google.gson.Gson;

public class Test2 {

	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		//1.view,获取cid
		HttpGet get = new HttpGet("https://api.bilibili.com/x/web-interface/view?aid=64689940");
		try {
			HttpResponse resp = client.execute(get);
			String ctt = EntityUtils.toString(resp.getEntity(),"UTF-8");
			
			Gson gson = new Gson();
			Map<String, Object> data = gson.fromJson(ctt, Map.class);
			Double cid = (Double)((List<Map<String, Object>>)((Map<String, Object>)data.get("data")).get("pages")).get(0).get("cid");
			String cidS = new BigDecimal(cid).stripTrailingZeros().toPlainString();
			
			//2.playurl
			get = new HttpGet("https://api.bilibili.com/x/player/playurl?cid="+cidS+"&avid=64689940&qn=16");
			get.setHeader("Cookie", "SESSDATA=d96c2f63%2C1574302559%2Cdc63a3a1");
			get.setHeader("Host", "api.bilibili.com");
			
			resp = client.execute(get);
			ctt = EntityUtils.toString(resp.getEntity(),"UTF-8");
			
			data = gson.fromJson(ctt, Map.class);
			String url= ((List<Map<String, Object>>)((Map<String, Object>)data.get("data")).get("durl")).get(0).get("url").toString();
			
			
			//3.下载
			get = new HttpGet(url);
			get.setHeader("Referer", "https://api.bilibili.com/x/web-interface/view?aid=64689940");
			get.setHeader("Range","bytes=0-");
			get.setHeader("Origin", "https://www.bilibili.com");
			
			resp = client.execute(get);
			
			IOUtils.copy(resp.getEntity().getContent(),new FileOutputStream("D:\\Software\\a.flv"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
