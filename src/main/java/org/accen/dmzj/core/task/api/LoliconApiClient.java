package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.RequestLine;
import feign.codec.StringDecoder;

@FeignApi(host = "https://api.lolicon.app")
public interface LoliconApiClient {
	@RequestLine("GET /setu/")
	public Map<String, Object> setu();
	
}
