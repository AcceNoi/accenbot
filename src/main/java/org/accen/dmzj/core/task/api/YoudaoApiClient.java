package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;
import org.accen.dmzj.core.task.api.vo.YoudaoTranslateResult;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "http://fanyi.youdao.com")
public interface YoudaoApiClient {
	
	/**
	 * 翻译api
	 * @param src
	 * @param lang
	 * @return
	 */
	@RequestLine("GET /translate?doctype=json&type={type}&i={i}")
	public YoudaoTranslateResult translate(@Param("i") String src,@Param("type") String lang);
}
