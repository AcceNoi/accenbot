package org.accen.dmzj.core.api.bilibili;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.api.KaassApiClient;
import org.accen.dmzj.core.api.vo.BilibiliBangumiInfo;
import org.accen.dmzj.core.api.vo.BilibiliUserInfo;
import org.accen.dmzj.core.api.vo.BilibliVideoInfo;
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
	private KaassApiClient kaassApiClient;
	@Autowired
	private BilibiliApiClient bilibiliApiClient;
	private final static Logger logger = LoggerFactory.getLogger(BilibiliSearchApiClientPk.class);
	
	
	public BilibiliUserInfo searchUser(String name) {
		/*String html = client.searchUser(name);
		return parseHtml(html);*/
		Map<String,Object> searchResult = bilibiliApiClient.search(1, name, BilibiliApiClient.SEARCH_TYPE_USER);
		if(Double.parseDouble(((Map<String, Object>)searchResult.get("data")).get("numResults").toString())>=1.0) {
			//numResult有值
			BilibiliUserInfo info = new BilibiliUserInfo();
			Map<String,Object> firstUserMap = ((List<Map<String, Object>>)((Map<String, Object>)searchResult.get("data")).get("result")).get(0);
			info.setMid((long)Double.parseDouble(firstUserMap.get("mid").toString()));
			info.setName(firstUserMap.get("uname").toString());
			info.setRoomId((int)firstUserMap.get("room_id"));
			info.setUsign((String)firstUserMap.get("usign"));
			return info;
		}
		return null;
	}
	public BilibiliUserInfo searchUser(long mid) {
		try {
			Map<String, Object> upSpace = kaassApiClient.space(mid);
			if(upSpace!=null) {
				BilibiliUserInfo info = new BilibiliUserInfo();
				info.setMid(mid);
				info.setName(((Map<String, Object>)((Map<String, Object>)upSpace.get("data")).get("card")).get("name").toString());
				info.setRoomId((long)((Map<String, Object>)((Map<String, Object>)upSpace.get("data")).get("live")).get("roomid"));
				info.setUsign(((Map<String, Object>)((Map<String, Object>)upSpace.get("data")).get("card")).get("sign").toString());
				return info;
			}
		}catch (Exception e) {
			//由于kaass给的api当找不到时会返回404而不是错误码
			logger.warn("无法找到此b站用户，id:{0}",mid);
		}
		return null;
	}
	
	
	
	private final static Pattern bilispacePattern = Pattern.compile("//space.bilibili.com/(\\d+?)\\?.*");
	@Deprecated
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
	/**
	 * 查找番剧完整信息
	 * @param title
	 * @return
	 */
	public BilibiliBangumiInfo searchBangumi(String title) {
		Map<String,Object> searchResult = bilibiliApiClient.search(1, title, BilibiliApiClient.SEARCH_TYPE_BANGUMI);
		if(Double.parseDouble(((Map<String, Object>)searchResult.get("data")).get("numResults").toString())>=1.0) {
			BilibiliBangumiInfo info = new BilibiliBangumiInfo();
			Map<String,Object> firstBangumiMap = ((List<Map<String, Object>>)((Map<String, Object>)searchResult.get("data")).get("result")).get(0);
			info.setMediaId((long)Double.parseDouble(firstBangumiMap.get("media_id").toString()));
//			info.setName(firstBangumiMap.get("title").toString());
			//由于b站的api给的title会添加<em></em>，需要格式化以下
			Document titleDoc = Jsoup.parse("<html>"+firstBangumiMap.get("title").toString()+"</html>");
			info.setName(titleDoc.text());
			return info;
		}else {
			return null;
		}
	}
	
	/**
	 * 获取up投稿的视频
	 * @param mid
	 * @return
	 */
	public List<BilibliVideoInfo> searchUpVideo(long mid){
		Map<String, Object> searchResult = kaassApiClient.contribute(mid, 1, 10);
		if(("OK").equals(searchResult.get("status"))) {
			List<Map<String,Object>> datas = (List<Map<String, Object>>) searchResult.get("data");
			if(!datas.isEmpty()) {
				
				List<BilibliVideoInfo> infos = 	datas.stream()
					.map(data->
						new BilibliVideoInfo((long)(Double.parseDouble(data.get("id").toString()))
							, data.get("title").toString()
							, (long)(Double.parseDouble(data.get("postTime").toString()))
						))
					.collect(Collectors.toList());
				return infos;
			}
		}
		return null;
	}
}
