package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "https://api.pixivic.com")
public interface PixivicApiClient {
	@RequestLine("GET /illustrations?keyword={keyword}&page={page}")
	public Map<String,Object> search(@Param("keyword")String keyword,@Param("page")int page);
	/**
	 * 搜索建议
	 * @param keyword
	 * @return
	 */
	@RequestLine("GET /keywords/{keyword}/pixivSuggestions")
	public Map<String,Object> suggestions(@Param("keyword")String keyword);
	/**
	 * 排行榜
	 * @param page
	 * @param date
	 * @param mode 支持day,week,month
	 * @return
	 */
	@RequestLine("GET /ranks?page={page}&date={date}&mode={mode}")
	public Map<String,Object> rank(@Param("page")int page,@Param("date")String date,@Param("mode")String mode);
}
