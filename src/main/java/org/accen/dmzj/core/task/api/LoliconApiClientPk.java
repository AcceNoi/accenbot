package org.accen.dmzj.core.task.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoliconApiClientPk {
	@Autowired
	private LoliconApiClient loliconApiClient;

	public String setu() {
		String html = loliconApiClient.setu();
		return parseHtml(html);
	}
	private String parseHtml(String html) {
		Document dom = Jsoup.parse(html);
		Elements images = dom.select("body img");
		if(images!=null&&!images.isEmpty()) {
			return images.first().attr("src");
		}
		return null;
	}
}
