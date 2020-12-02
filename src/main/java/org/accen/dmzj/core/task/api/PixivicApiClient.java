package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;
import org.accen.dmzj.core.autoconfigure.FeignApiInitBefore;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignApi(host = "https://pix.ipv4.host",before = PixivApiClientInitBefore.class)
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
	/**
	 * 画师详情
	 * @param id
	 * @return
	 */
	@RequestLine("GET /artists/{id}")
	public Map<String,Object> artist(@Param("id")int id);
	/**
	 * 画师的作品
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestLine("GET /artists/{id}/illusts/illust?page={page}&pageSize={pageSize}&maxSanityLevel=4")
	public Map<String,Object> artistIllusts(@Param("id")int id,@Param("page")int page,@Param("pageSize")int pageSize);
}
