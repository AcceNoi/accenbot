package org.accen.dmzj.core.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.api.vo.ImageResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Component
public class SaucenaoApiClientPk {
	@Autowired
	private SaucenaoApiClient apiClient;
	
	public ImageResult search(File file) {
		MultipartFile multiFile;
		try {
			FileItem fi = new DiskFileItem("file", "image/jpeg", true, file.getName(), (int) file.length(), file.getParentFile());
			IOUtils.copy(new FileInputStream(file), fi.getOutputStream());
			multiFile = new CommonsMultipartFile(fi);
			String resp = apiClient.search(multiFile);
			return parseSearchResult(resp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ImageResult search(String url) {
		try (InputStream is = new URL(url).openStream()){
			File imgFile = new File(""+url.hashCode());
			Files.copy(is,imgFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
			ImageResult ir = search(imgFile);
			imgFile.delete();
			return ir;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
