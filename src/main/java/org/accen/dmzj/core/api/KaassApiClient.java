package org.accen.dmzj.core.api;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
		name="kaass",
		url="https://api.kaaass.net")
public interface KaassApiClient {
	@GetMapping("/biliapi/user/space")
	public Map<String, Object> space(@RequestParam("id")long id);
	
	@GetMapping("/biliapi/user/contribute")
	public Map<String,Object> contribute(@RequestParam("id")long id,@RequestParam("page") int page,@RequestParam("pageCount") int pageCount);
}
