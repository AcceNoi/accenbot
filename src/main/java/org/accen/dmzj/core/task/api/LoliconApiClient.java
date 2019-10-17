package org.accen.dmzj.core.task.api;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.RequestLine;
import feign.codec.StringDecoder;

@FeignApi(host = "https://api.lolicon.app",decoder = StringDecoder.class)
public interface LoliconApiClient {
	@RequestLine("GET /setu/view.php")
	public String setu();
}
