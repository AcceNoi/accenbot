package org.accen.dmzj.core.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.api.vo.ImageResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaucenaoApiClientPk {
	@Autowired
	private SaucenaoApiClient apiClient;
	
	public ImageResult search(File file) {
		String resp = apiClient.search(file);
		return parseSearchResult(resp);
	}
	public ImageResult search(String url) {
		String resp = apiClient.search(url);
		return parseSearchResult(resp);
	}
	private static final Pattern imageUrlPattern = Pattern.compile("https://saucenao.com/search.php.*?\\&url=(.*)");
	private ImageResult parseSearchResult(String responseHtml) {
		ImageResult imageResult = new ImageResult();
		Document dom = Jsoup.parse(responseHtml);
		
		Elements urls = dom.select("div.result:not(.hidden) table.resulttable div.resultimage a");
		if(urls!=null&&!urls.isEmpty()) {
			String encodingUrl = urls.first().attr("href");
			try {
				Matcher matcher = imageUrlPattern.matcher(URLDecoder.decode(encodingUrl, "utf-8"));
				if(matcher.matches()) {
					imageResult.setUrl(matcher.group(1));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		Elements similarityEles = dom.select("div.result:not(.hidden) table.resulttable div.resultsimilarityinfo");
		if(similarityEles!=null&&!similarityEles.isEmpty()) {
			imageResult.setSimilarity(similarityEles.first().text());
		}
		Elements titleEles = dom.select("div.result:not(.hidden) table.resulttable div.resulttitle");
		if(titleEles!=null&&!titleEles.isEmpty()) {
			imageResult.setTitle(titleEles.first().text());
		}
		Elements contentEles = dom.select("div.result:not(.hidden) table.resulttable div.resultcontentcolumn");
		if(contentEles!=null&&!contentEles.isEmpty()) {
			Elements contents = contentEles.first().children();
			String content = contents
				.stream()
				.filter(ctt->ctt.hasText())
				.map(ctt->("a".equals(ctt.tagName()))?ctt.text()+"：["+ctt.attr("href")+"]":ctt.text())
				.collect(Collectors.joining(" "));
			imageResult.setContent(content);
			imageResult.setSuccess(true);
		}
		return imageResult;
	}
}
