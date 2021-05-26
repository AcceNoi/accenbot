package org.accen.dmzj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.api.cq.CqHttpConfigurationProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
/**
 * 用于将网络图片持久化到本地
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class FilePersistentUtil {
	public FilePersistentUtil(@Autowired CqHttpConfigurationProperties cqProp) {
		imageHome = cqProp.imageLocation();
		recordHome = cqProp.recordLocation();
	}
	@Value("${accenbot.persistent}")
	private String localFilePath;
	@Value("${accenbot.persistent-map}")
	private String localUrl;
	
	/*@Value("${coolq.base.home}")
	private String coolqHome;*/
	private String imageHome;
	private String recordHome;
	
	private HttpClient httpClient = HttpClientBuilder.create().build();
	/**
	 * 持久化网络文件
	 * @param url
	 * @param fileName
	 * @return 长度为2的数组，0-本地文件地址，1-生成的网络文件地址
	 */
	@Deprecated
	public String[] persistent(String url,String fileName) {
		return persistent(url, fileName, localUrl);
	}
	
	private final static Pattern patternCq = Pattern
			.compile("^\\[CQ\\:image,file=(.*?),url=(.*?)\\]$");
	/**
	 * 将cq image持久化输出，如果不需要持久化，则输出原本的样子
	 * @param cq
	 * @return
	 */
	public String persistent(String cqImg) {
		return persistentByCq(cqImg, localUrl);
	}
	public String persistentByCq(String cqImg,String localDir) {
		Matcher matcher = patternCq.matcher(cqImg);
		if(matcher.matches()) {
			String url = matcher.group(2);
			String fileName = matcher.group(1);
			String[] persistentResult = persistent(url, fileName,localDir);
			if(persistentResult!=null) {
				//转化成本地文件模式
				cqImg = String.format("[CQ:image,file=file:///%s]", persistentResult[0]);
			}
		}
		return cqImg;
	}
	public String persistentByCq(String cqImg,String fileName,String localDir) {
		Matcher matcher = patternCq.matcher(cqImg);
		if(matcher.matches()) {
			String url = matcher.group(2);
			String[] persistentResult = persistent(url, fileName,localDir);
			if(persistentResult!=null) {
				//转化成本地文件模式
				cqImg = String.format("[CQ:image,file=file:///%s]", persistentResult[0]);
			}
		}
		return cqImg;
	}
	public String[] persistent(String url,String fileName,String localDir) {
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse resp = httpClient.execute(get);
			HttpEntity responseEntity = resp.getEntity();
			if(resp.getStatusLine().getStatusCode()==200) {
				String localFileName = localDir+"/"+fileName;
				OutputStream os = new FileOutputStream(localFileName);
				InputStream is = responseEntity.getContent();
				IOUtils.copy(is, os);
				os.close();
				is.close();
				return new String[] {localFileName,localDir+"/"+fileName};
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private final static Pattern patternCqEx = Pattern
			.compile("^\\[CQ\\:(image|record),file=(.+?)(,url=(.*?))?\\]$");
	/**
	 * 将cq image持久化输出，如果不需要持久化，则输出原本的样子
	 * @param cqImg
	 * @param isCq 是否以cq码返回
	 * @return 返回本地文件目录
	 */
	public String persistentLocal(String cqImg,boolean isCq) {
		Matcher matcher = patternCqEx.matcher(cqImg);
		if(matcher.matches()) {
			String urlPart = matcher.group(3);
			if(StringUtils.isEmpty(urlPart)) {
				//url段没有，说明是本地文件
				if(isCq) {
					return cqImg;
				}else {
					return ("image".equals(matcher.group(1))?imageHome:recordHome)+"/"+matcher.group(2);
				}
				
			}else {
				//url段有，则说明是网路文件
				String url = matcher.group(4);
				String fileName = matcher.group(2);
				String[] persistentResult = persistent(url, fileName, localFilePath);//persistent(url, fileName);
				if(persistentResult!=null) {
					if(isCq) {
						return String.format("[CQ:image,file=file:///%s]", persistentResult[0]);
					}else {
						return persistentResult[0];
					}
					
				}
			}
		}
		return null;
	}
	/**
	 * 获取cqimg格式的图片基本信息
	 * @param cqImg
	 * @return 0-md5.1-width,2-height,3-size,4-url,5-addtime
	 */
	public String[] getImageMetaInfo(String cqImg) {
		Matcher matcher = patternCq.matcher(cqImg);
		if(matcher.matches()) {
			String fileName = matcher.group(1);
			try {
//				System.out.println(imageHome+"/"+fileName+".cqimg");
				FileReader cqimgFileReader = new FileReader(imageHome+"/"+fileName+".cqimg");
				BufferedReader cqimgBuf = new BufferedReader(cqimgFileReader);
				String[] meta = new String[6];
				cqimgBuf.readLine();
				meta[0] = cqimgBuf.readLine().substring(4);//md5
				/*meta[1] = cqimgBuf.readLine().substring(6);//width
				meta[2] = cqimgBuf.readLine().substring(7);//height*/
				meta[3] = cqimgBuf.readLine().substring(5);//size
				meta[4] = cqimgBuf.readLine().substring(4);//url
				meta[5] = cqimgBuf.readLine().substring(8);//addtime
				cqimgFileReader.close();
				return meta;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}
}
