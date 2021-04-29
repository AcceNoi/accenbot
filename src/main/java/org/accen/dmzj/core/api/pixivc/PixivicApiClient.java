package org.accen.dmzj.core.api.pixivc;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
		name="pixivc-backend",
		url="https://pix.ipv4.host",
		configuration = PixivcAuthConfiguration.class)
public interface PixivicApiClient {
	@GetMapping("/illustrations?pageSize=30&illustType=illust&searchType=original&maxSanityLevel=4")
	public Map<String,Object> search(@RequestParam("keyword")String keyword,@RequestParam("page")int page);
	/**
	 * 搜索建议
	 * @param keyword
	 * @return
	 */
	@GetMapping("/keywords/{keyword}/pixivSuggestions")
	public Map<String,Object> suggestions(@PathVariable("keyword")String keyword);
	/**
	 * 排行榜 
	 * @param page
	 * @param date
	 * @param mode 支持day,week,month
	 * @return
	 */
	@GetMapping("/ranks")
	public Map<String,Object> rank(@RequestParam("page")int page,@RequestParam("date")String date,@RequestParam("mode")String mode);
	/**
	 * 画师详情
	 * @param id
	 * @return
	 */
	@GetMapping("/artists/{id}")
	public Map<String,Object> artist(@PathVariable("id")int id);
	/**
	 * 画师的作品
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/artists/{id}/illusts/illust?maxSanityLevel=4")
	public Map<String,Object> artistIllusts(@PathVariable("id")int id,@RequestParam("page")int page,@RequestParam("pageSize")int pageSize);
}
