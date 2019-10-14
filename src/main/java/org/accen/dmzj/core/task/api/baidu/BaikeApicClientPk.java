package org.accen.dmzj.core.task.api.baidu;

import java.util.regex.Pattern;

import org.accen.dmzj.core.task.api.vo.BaikeResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class BaikeApicClientPk {
	@Autowired
	private BaikeApiClient baikeApiClient;
	
	public BaikeResult baike(String word) {
		String html = baikeApiClient.baike(word);
		return parseHtml(html);
	}
	private final static Pattern pattern = Pattern.compile("^.*?baike.baidu.com/item/.*?/\\d+");
	public BaikeResult parseHtml(String html) {
		Document dom = Jsoup.parse(html);
		if(pattern.matcher(dom.baseUri()).matches()) {
			//找到了
			String summary = dom.select("div.content div.main-content div.lemma-summary").text();
			String title = dom.select("div.content div.main-content dd.lemmaWgt-lemmaTitle-title h1").text()
					+dom.select("div.content div.main-content dd.lemmaWgt-lemmaTitle-title h2").text();
			String url = dom.baseUri();
			BaikeResult br = new BaikeResult();
			br.setSummary(summary);
			br.setUrl(url);
			br.setTitle(title);
			return br;
		}else {
			//没找到
			return null;
		}
	}
}
