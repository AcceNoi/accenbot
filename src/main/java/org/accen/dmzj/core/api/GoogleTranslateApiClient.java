package org.accen.dmzj.core.api;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
		name="google-translate",
		url="http://translate.google.cn")
public interface GoogleTranslateApiClient {
	/**
	 * google翻译api
	 * @param lang
	 * @param word
	 * @return
	 */
	@GetMapping("/translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8")
	public Map<String,Object> translate(@RequestParam("tl")String lang,@RequestParam("q")String word,@RequestParam("sl")String sl);
}
