package org.accen.dmzj.core.task.api;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;
import feign.codec.StringDecoder;

@FeignApi(host = "http://search.bilibili.com",decoder = StringDecoder.class)
@Deprecated
public interface BilibiliSearchApiClient {
	@RequestLine("GET /upuser?keyword={kw}")
	public String searchUser(@Param("kw")String kw);
	
	@RequestLine("GET /bangumi?keyword={kw}")
	public String searchBangumi(@Param("kw")String kw);
}
