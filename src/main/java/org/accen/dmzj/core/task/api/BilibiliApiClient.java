package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;

/**
 * 
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignApi(host = "https://api.bilibili.com")
public interface BilibiliApiClient {
	/**
	 * b站用户
	 */
	public final static String SEARCH_TYPE_USER = "bili_user";
	/**
	 * 番剧
	 */
	public final static String SEARCH_TYPE_BANGUMI = "media_bangumi";
	
	
	@RequestLine("GET /x/web-interface/search/type?page={page}&keyword={kw}&search_type={type}")
	public Map<String, Object> search(@Param("page")int page,@Param("kw")String kw,@Param("type")String type);
}
