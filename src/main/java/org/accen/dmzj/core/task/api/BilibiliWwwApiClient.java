package org.accen.dmzj.core.task.api;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.codec.StringDecoder;

@FeignApi(host = "www.bilibili.com",decoder = StringDecoder.class)
public interface BilibiliWwwApiClient {
	@RequestLine("GET /video/av{avId}")
	@Headers({"User-Agent:[{\"key\":\"User-Agent\",\"value\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36\",\"description\":\"\",\"type\":\"text\",\"enabled\":true}]"})
	public String video(@Param("avId")String avId);
}
