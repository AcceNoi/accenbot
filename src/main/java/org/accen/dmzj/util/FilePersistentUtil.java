package org.accen.dmzj.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * 用于将网络图片持久化到本地
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class FilePersistentUtil {
	@Value("${sys.static.html.upload}")
	private String localFilePath;
	@Value("${sys.static.url.upload}")
	private String localUrl;
	
	private HttpClient httpClient = HttpClientBuilder.create().build();
	/**
	 * 持久化网络文件
	 * @param url
	 * @param fileName
	 * @return 长度为2的数组，0-本地文件地址，1-生成的网络文件地址
	 */
	public String[] persistent(String url,String fileName) {
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse resp = httpClient.execute(get);
			HttpEntity responseEntity = resp.getEntity();
			if(resp.getStatusLine().getStatusCode()==200) {
				String localFileName = localFilePath+fileName;
				OutputStream os = new FileOutputStream(localFileName);
				InputStream is = responseEntity.getContent();
				IOUtils.copy(is, os);
				os.close();
				is.close();
				return new String[] {localFileName,localUrl+fileName};
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private final static Pattern patternCq = Pattern
			.compile("^\\[CQ\\:image,file=(.*?),url=(.*?)\\]$");
	/**
	 * 将cq image持久化输出，如果不需要持久化，则输出原本的样子
	 * @param cq
	 * @return
	 */
	public String persistent(String cqImg) {
		Matcher matcher = patternCq.matcher(cqImg);
		if(matcher.matches()) {
			String url = matcher.group(2);
			String fileName = matcher.group(1);
			String[] persistentResult = persistent(url, fileName);
			if(persistentResult!=null) {
				//转化成本地文件模式
				cqImg = String.format("[CQ:image,file=file:///%s]", persistentResult[0]);
			}
		}
		return cqImg;
	}
}
