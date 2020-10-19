package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignApi(host = "https://api.pixivic.com")
@Headers({"Authorization:eyJhbGciOiJIUzUxMiJ9.eyJwZXJtaXNzaW9uTGV2ZWwiOjEsInJlZnJlc2hDb3VudCI6MSwiaXNCYW4iOjEsInVzZXJJZCI6NDM4OTg4LCJpYXQiOjE2MDIyMTQ4OTQsImV4cCI6MTYwMzk0Mjg5NH0.VnPcn3cdnTKT38CqjW9ELxp8ttSSKuwFby93Mgyvds3cAudL5EhnJ_4vYB8N_40Il9FLQn2EN-ZQTrZ9wc2XQQ"})
public interface PixivicApiClient {
	@RequestLine("GET /illustrations?keyword={keyword}&page={page}&pageSize=30&illustType=illust&searchType=original&maxSanityLevel=4")
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
