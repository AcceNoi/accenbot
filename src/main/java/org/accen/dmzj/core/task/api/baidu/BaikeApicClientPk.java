package org.accen.dmzj.core.task.api.baidu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.api.vo.BaikeResult;
import org.accen.dmzj.util.RandomUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
@Component
public class BaikeApicClientPk {
	@Autowired
	private BaikeApiClient baikeApiClient;
	
	
	/**
	 * 使用httpclient实现
	 * @param word
	 * @return
	 */
	private final static HttpClient client = HttpClientBuilder.create().build();
	private String httpBaike(String url) {
		try {
			
			HttpGet getReq = new HttpGet(url);
			HttpResponse originResp = client.execute(getReq);
			HttpEntity responseEntity = originResp.getEntity();
			if(originResp.getStatusLine().getStatusCode()==200) {
				return EntityUtils.toString(responseEntity,"UTF-8");
			}else if(originResp.getStatusLine().getStatusCode()/100==3) {
				String redirectLocation = originResp.getHeaders("Location")[0].getValue();
				return httpBaike("https://baike.baidu.com"+redirectLocation);
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public BaikeResult baike(String word) {
//		String html = baikeApiClient.baike(word);
		try {
			String url = "https://baike.baidu.com/item/"+URLEncoder.encode(word, "UTF-8");
			String html = httpBaike(url);
			//先解析是否有多义词
			String secUrl = parsePolysemy(html);
			if(secUrl!=null) {
				html = httpBaike(secUrl);
			}
			BaikeResult br = parseHtml(html);
			br.setUrl(secUrl==null?url:secUrl);
			return br;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private final static Pattern pattern = Pattern.compile("^.*?baike.baidu.com/item/.*?/\\d+");
	/**
	 * 由于词条有多义词，需要解析下
	 * @param html
	 * @return
	 */
	public String parsePolysemy(String html) {
		Document dom = Jsoup.parse(html);
		Elements lis = dom.select("ul.custom_dot li.list-dot a");
		Elements sign = dom.select("div.lemmaWgt-subLemmaListTitle");
		if(lis!=null&&!lis.isEmpty()&&sign!=null&&!sign.isEmpty()) {
			return "https://baike.baidu.com"+lis.get(RandomUtil.randomInt(lis.size())).attr("href");
		}else {
			return null;
		}
	}
	public BaikeResult parseHtml(String html) {
		Document dom = Jsoup.parse(html);
//		if(pattern.matcher(dom.baseUri()).matches()) {
			//找到了
			String summary = dom.select("div.lemma-summary").text();
			String title = dom.select("dd.lemmaWgt-lemmaTitle-title h1").text()
					+dom.select("dd.lemmaWgt-lemmaTitle-title h2").text();
			Elements  imgs = dom.select("div.summary-pic a img");
			BaikeResult br = new BaikeResult();
			if(imgs!=null&&!imgs.isEmpty()) {
				String imageUrl = imgs.first().attr("src");
				int queryMark = imageUrl.indexOf("?");
				if(queryMark>=0&&queryMark<imageUrl.length()-1) {
					imageUrl = imageUrl.substring(0, queryMark+1)+Arrays.stream(imageUrl.substring(queryMark+1).split("&")).map(query->{
						String[] nameAndValue = query.split("=");
						/*return switch(nameAndValue.length) {
						case 1 -> nameAndValue[0];
						case 2 -> try {nameAndValue[0]+"="+URLEncoder.encode(nameAndValue[1],"utf-8");} catch (UnsupportedEncodingException e){// TODO Auto-generated catch block
e.printStackTrace();}
						default -> "";
						};*/
						switch(nameAndValue.length) {
						case 1:return nameAndValue[0];
						case 2:try {
								return nameAndValue[0]+"="+URLEncoder.encode(nameAndValue[1],"utf-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						default: return "";
						}
					}).collect(Collectors.joining("&"));
				}
				br.setImageUrl(imageUrl);
			}
			
			String url = dom.baseUri();
			
			br.setSummary(summary);
			br.setUrl(url);
			br.setTitle(title);
			return br;
//		}else {
//			//没找到
//			return null;
//		}
	}
}
