package org.accen.dmzj.core.api.pixivc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
/**
 * 通过调用验证码和登录接口实现的Auth Fresher，貌似没有其他实现方式了
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class HttpAuthFresher implements AuthFresher{
	private final static Logger logger = LoggerFactory.getLogger(HttpAuthFresher.class);
	private String vertifyUrl = "https://pix.ipv4.host/illustrations?page=1&keyword=a";
	private int retry;
	private String vcUrl;
	private String username;
	private String password;
	private Gson gson = new GsonBuilder()
			.setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
	private final static Pattern vertificationCodePattern = Pattern.compile("[a-zA-Z0-9]{4}");
	public HttpAuthFresher(PixivcAuthConfigurationProperties prop) {
		this.retry = prop.loginRetry();
		this.vcUrl = prop.vcUrl();
		this.username = prop.username();
		this.password = prop.password();
	}
	@Override
	public boolean vertify(Auth auth) {
		try {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest authGet = HttpRequest.newBuilder()
					.uri(URI.create(vertifyUrl))
					.GET().header("authorization", auth.auth()).build();
			HttpResponse<String> authResp = httpClient.send(authGet, HttpResponse.BodyHandlers.ofString());
			if(authResp.statusCode()==401) {
				//unauthorized
				//进行下面登录
				logger.warn("缓存的authorization不存在或已失效");
				return false;
			}else if(authResp.statusCode()==200){
				logger.info("缓存的authorization依旧有效。");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Auth fresh(Auth auth) {
		Auth newAuth = pixivcLogin(0);
		return newAuth;
	}
	/**
	 * 递归的登录获取认证凭证
	 * @param curDepth 递归深度
	 * @return
	 */
	private Auth pixivcLogin(int curDepth) {
		HttpClient httpClient = HttpClient.newHttpClient();
		if(curDepth>retry) {
			logger.warn("pixivc多次登录验证失败，涩图及榜单功能将无法使用！");
			return null;
		}
		//1.获取验证码
		HttpRequest get = HttpRequest.newBuilder()
							.uri(URI.create(vcUrl))
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
								.POST(HttpRequest.BodyPublishers.ofString("{\"username\":\""+username+"\",\"password\":\""+password+"\"}"))
								.header("content-type", "application/json;charset=UTF-8")
								.build();
						HttpResponse<String> loginResp = httpClient.send(loginPost,HttpResponse.BodyHandlers.ofString());
						if(loginResp.statusCode()==200) {
							String authStr = loginResp.headers().firstValue("authorization").get();
							logger.warn("authorization:{}",authStr);
							Auth auth = new Auth(authStr);
							logger.info("已缓存authorization到{}",auth);
							return auth;
						}
					}
					logger.warn("登录验证失败！将进行第{}次重试...",++curDepth);
					return pixivcLogin(curDepth);
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
