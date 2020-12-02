package org.accen.dmzj.core.task.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FeignApi;
import org.accen.dmzj.core.autoconfigure.FeignApiInitBefore;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
public class PixivApiClientInitBefore implements FeignApiInitBefore{
	
	@Value("${pixivc.account}")
	private static String pixivcAccount ;
	@Value("${pixivc.password}")
	private static String pixivcPassword ;
	private final static Logger logger = LoggerFactory.getLogger(PixivApiClientInitBefore.class);
	private final static Pattern vertificationCodePattern = Pattern.compile("[a-zA-Z0-9]{4}");
	@Override
	public Object before(Class<?> thisClass, FeignApi feignApi) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		GsonBuilder gb = new GsonBuilder()
				.setLongSerializationPolicy(LongSerializationPolicy.STRING);
				
		Gson gson = gb.create();
		
		
		//1.获取验证码
		HttpGet get = new HttpGet("https://pix.ipv4.host/verificationCode");
		try {
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode()==200) {
				String result = EntityUtils.toString(resp.getEntity(),Charset.forName("utf-8"));
				@SuppressWarnings("unchecked")
				Map<String, Object> bodyMap = gson.fromJson(result, Map.class);
				@SuppressWarnings("unchecked")
				String vid = (String) ((Map<String,Object>)bodyMap.get("data")).get("vid");
				@SuppressWarnings("unchecked")
				String imageBase64 = (String) ((Map<String,Object>)bodyMap.get("data")).get("imageBase64");
				File vertificationCodeFile = new File("vertificationCode.bmp");
				Files.write(Paths.get(vertificationCodeFile.getAbsolutePath()), Base64.getDecoder().decode(imageBase64), StandardOpenOption.CREATE);
				logger.warn("vid:{},vertificationCodeFile:{}",vid,vertificationCodeFile.getAbsolutePath());
				logger.warn("请输入验证码>>>");
				try(Scanner scan = new Scanner(System.in);){
				if(scan.hasNext()) {
					String vertificationCode = scan.nextLine().trim();
					if(vertificationCodePattern.matcher(vertificationCode).matches()) {
						//2.登录
						HttpPost loginPost = new HttpPost("https://pix.ipv4.host/users/token?vid="+vid+"&value="+vertificationCode);
						loginPost.addHeader("content-type", "application/json;charset=UTF-8");
						loginPost.addHeader("referer", "https://pixivic.com/");
						loginPost.setEntity(new StringEntity("{\"username\":\""+pixivcAccount+"\",\"password\":\""+pixivcPassword+"\"}"));
						HttpResponse loginResp = httpClient.execute(loginPost);
						if(loginResp.getStatusLine().getStatusCode()==200) {
//							String loginResult =  EntityUtils.toString(loginResp.getEntity(),Charset.forName("utf-8"));
							String auth = loginResp.getHeaders("authorization")[0].getValue().trim();
							logger.warn("authorization:{}",auth);
							return auth;
						}
					}else {
						logger.warn("请输入验证码>>>");
					}
				}}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
