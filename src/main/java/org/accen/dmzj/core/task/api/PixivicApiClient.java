package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "https://api.pixivic.com")
public interface PixivicApiClient {
	@RequestLine("GET /illustrations?keyword={keyword}&page={page}")
	public Map<String,Object> search(@Param("keyword")String keyword,int page);
}
