package org.accen.dmzj.core.api;

import org.accen.dmzj.core.api.vo.YoudaoTranslateResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
		name="youdao-fanyi",
		url="http://fanyi.youdao.com")
public interface YoudaoApiClient {
	
	/**
	 * 翻译api
	 * @param src
	 * @param lang
	 * @return
	 */
	@GetMapping("/translate?doctype=json")
	public YoudaoTranslateResult translate(@RequestParam("i") String src,@RequestParam("type") String lang);
}
