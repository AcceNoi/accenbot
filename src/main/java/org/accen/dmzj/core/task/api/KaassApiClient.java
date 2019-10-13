package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "https://api.kaaass.net")
public interface KaassApiClient {
	@RequestLine("GET /biliapi/user/space?id={id}")
	public Map<String, Object> space(@Param("id")long id);
	
	@RequestLine("GET /biliapi/user/contribute?id={id}&page={page}&pageCount={pageCount}")
	public Map<String,Object> contribute(@Param("id")long id,@Param("page") int page,@Param("pageCount") int pageCount);
}
