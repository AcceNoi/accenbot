package org.accen.dmzj.core.task.api;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.api.vo.BilibiliUserInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BilibiliSearchApiClientPk {
	@Autowired
	private BilibiliSearchApiClient client;
	@Autowired
	private KaassApiClient kaassApiClient;
	
	private final static Logger logger = LoggerFactory.getLogger(BilibiliSearchApiClientPk.class);
	
	
	public BilibiliUserInfo searchUser(String name) {
		String html = client.searchUser(name);
		return parseHtml(html);
	}
	public BilibiliUserInfo searchUser(long mid) {
		try {
			Map<String, Object> upSpace = kaassApiClient.space(mid);
			if(upSpace!=null) {
				BilibiliUserInfo info = new BilibiliUserInfo();
				info.setMid(mid);
				info.setName(((Map<String, Object>)((Map<String, Object>)upSpace.get("data")).get("card")).get("name").toString());
				return info;
			}
		}catch (Exception e) {
			//由于kaass给的api当找不到时会返回404而不是错误码
			logger.warn("无法找到此b站用户，id:{0}",mid);
		}
		return null;
	}
	
	private final static Pattern bilispacePattern = Pattern.compile("//space.bilibili.com/(\\d+?)\\?.*");
	private BilibiliUserInfo parseHtml(String html) {
		Document dom = Jsoup.parse(html);
		Elements usersElmts = dom.select("#user-list ul li.user-item");
		if(!usersElmts.isEmpty()) {
			Element userElmt = usersElmts.first().select("div.headline a").first();
			BilibiliUserInfo info = new BilibiliUserInfo();
			Matcher matcher = bilispacePattern.matcher(userElmt.attr("href"));	
			if(matcher.matches()) {
				info.setMid(Long.parseLong(matcher.group(1)));
				info.setName(userElmt.text());
				return info;
			}
			
		}
		return null;
	}
}
