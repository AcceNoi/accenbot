package org.accen.dmzj.core.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import feign.Response;

@FeignClient(
		name="pixivcat",
		url="https://pixiv.cat")
public interface PixivcatApiClient {
	
	@GetMapping("/{pPath}.jpg")
	public Response pixivImage(@PathVariable("pPath")String pPath);
}
