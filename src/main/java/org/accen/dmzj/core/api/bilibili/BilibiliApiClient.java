package org.accen.dmzj.core.api.bilibili;

import java.util.Map;

import org.accen.dmzj.core.api.vo.BilibiliSearch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignClient(
		name="bilibili-api",
		url="https://api.bilibili.com")
public interface BilibiliApiClient {
	/**
	 * b站用户
	 */
	public final static String SEARCH_TYPE_USER = "bili_user";
	/**
	 * 番剧
	 */
	public final static String SEARCH_TYPE_BANGUMI = "media_bangumi";
	
	
	@GetMapping("/x/web-interface/search/type")
	public BilibiliSearch search(@RequestParam("page")int page,@RequestParam("keyword")String kw,@RequestParam("search_type")String type);

	@GetMapping("/x/web-interface/view")
	public Map<String, Object> viewByAid(@RequestParam("aid")String avid);
	
	@GetMapping("/x/web-interface/view")
	public Map<String, Object> viewByBid(@RequestParam("bvid")String bvid);
}
