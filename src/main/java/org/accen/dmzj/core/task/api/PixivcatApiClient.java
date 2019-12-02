package org.accen.dmzj.core.task.api;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.RequestLine;
import feign.Response;
import feign.codec.Decoder;

@FeignApi(host = "https://i.pixiv.cat",decoder = Decoder.Default.class)
public interface PixivcatApiClient {
	
	@RequestLine("GET {pPath}")
	public Response pixivImage(String pPath);
}
