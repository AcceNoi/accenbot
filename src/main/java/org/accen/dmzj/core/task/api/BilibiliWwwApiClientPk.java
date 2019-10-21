package org.accen.dmzj.core.task.api;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class BilibiliWwwApiClientPk {
	@Autowired
	private BilibiliWwwApiClient bilibiliWwwApiClient;
	public String video(String avId) {
		String html = bilibiliWwwApiClient.video(avId);
		return parseHtml(html);
	}
	private static final String scriptPattern = "^window.__playinfo__={(.*)}$";
	public String parseHtml(String html) {
		String json = null;
		Document dom = Jsoup.parse(html);
		Elements scripts = dom.select("script");
		for(int index = 0;index<scripts.size();index++) {
			if(Pattern.matches(scriptPattern, scripts.get(index).text())) {
				json = "{"+scripts.get(index).text()+"}";
				break;
			}
		}
		
		if(json!=null) {
			Gson gson = new Gson();
			@SuppressWarnings("unchecked")
			Map<String, Object> obj = gson.fromJson(json, Map.class);
			List<Map<String,Object>> mimes = (List<Map<String, Object>>) ((Map<String,Object>)((Map<String,Object>)obj.get("data")).get("dash")).get("video");
			if(mimes!=null&&!mimes.isEmpty()) {
				List<Map<String,Object>> audioes = mimes
						.stream()
						.filter(mime->"audio/mp4".equals(mime.get("mimeType")))
						.collect(Collectors.toList());
				return (String) audioes.get(0).get("baseUrl");
			}
		}
		return null;
	}
}
