package org.accen.dmzj.core.task.api.baidu;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Headers;
import feign.RequestLine;
import feign.codec.StringDecoder;

@FeignApi(host = "https://baike.baidu.com",decoder = StringDecoder.class,client = BaikeRedirectClient.class)
@Headers({"Referer: https://baike.baidu.com"})
public interface BaikeApiClient {
	@RequestLine("GET /item/Java")
	public String baike(String word);
}
