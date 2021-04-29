package org.accen.dmzj.core.api;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
		name="lolicon",
		url="https://api.lolicon.app")
public interface LoliconApiClient {
	@GetMapping("/setu/")
	public Map<String, Object> setu();
	
}
