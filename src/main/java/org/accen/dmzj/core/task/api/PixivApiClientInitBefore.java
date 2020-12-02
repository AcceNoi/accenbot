package org.accen.dmzj.core.task.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FeignApi;
import org.accen.dmzj.core.autoconfigure.FeignApiInitBefore;

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
	/*登录最大重试次数*/
	private static final int maxPixivcVertifyRetryTimes = 3;
	private static String authTxt = "pixivc.auth";
	private final static Logger logger = LoggerFactory.getLogger(PixivApiClientInitBefore.class);
	private final static Pattern vertificationCodePattern = Pattern.compile("[a-zA-Z0-9]{4}");
	
	private HttpClient httpClient = HttpClient.newHttpClient();
	private Gson gson = new GsonBuilder()
			.setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
	@Override
	public Object before(Class<?> thisClass, FeignApi feignApi) {
		
		
		//0.从authTxt中取
		File authTxtFile = new File(authTxt);
		if(authTxtFile.exists()) {
			try {
				String auth = Files.readString(Paths.get(authTxtFile.getAbsolutePath()),  StandardCharsets.UTF_8);
				//0.1 判断是否过期
				HttpRequest authGet = HttpRequest.newBuilder()
										.uri(URI.create("https://pix.ipv4.host/illustrations?page=1&keyword=a"))
										.GET().header("authorization", auth).build();
				HttpResponse<String> authResp = httpClient.send(authGet, HttpResponse.BodyHandlers.ofString());
				if(authResp.statusCode()==401) {
					//unauthorized
					//进行下面登录
					logger.warn("缓存的authorization不存在或已失效，即将登录...");
				}else if(authResp.statusCode()==200){
					logger.info("缓存的authorization（{}）依旧有效。",auth);
					return auth;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		return pixivcVertify(0);
	}
	private String pixivcVertify(int curDepth) {
		if(curDepth>maxPixivcVertifyRetryTimes) {
			logger.warn("pixivc多次登录验证失败{}，涩图及榜单功能将无法使用！");
			return null;
		}
		//1.获取验证码
		HttpRequest get = HttpRequest.newBuilder()
							.uri(URI.create("https://pix.ipv4.host/verificationCode"))
							.GET().build();
		try {
			HttpResponse<String> resp = httpClient.send(get, HttpResponse.BodyHandlers.ofString());
			if(resp.statusCode()==200) {
				String result = resp.body();
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
						HttpRequest loginPost = HttpRequest.newBuilder()
								.uri(URI.create("https://pix.ipv4.host/users/token?vid="+vid+"&value="+vertificationCode))
								.POST(HttpRequest.BodyPublishers.ofString("{\"username\":\""+pixivcAccount+"\",\"password\":\""+pixivcPassword+"\"}"))
								.header("content-type", "application/json;charset=UTF-8")
//								.header("referer", "https://pixivic.com/")
								.build();
						HttpResponse<String> loginResp = httpClient.send(loginPost,HttpResponse.BodyHandlers.ofString());
						if(loginResp.statusCode()==200) {
//									String loginResult =  EntityUtils.toString(loginResp.getEntity(),Charset.forName("utf-8"));
							String auth = loginResp.headers().firstValue("authorization").get();
							logger.warn("authorization:{}",auth);
							Files.writeString(Paths.get(new File(authTxt).getAbsolutePath()), auth, StandardCharsets.UTF_8);
							logger.info("已缓存authorization到{}",auth);
							return auth;
						}
					}
					logger.warn("登录验证失败！将进行第{}次重试...",++curDepth);
					return pixivcVertify(curDepth);
				}} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
