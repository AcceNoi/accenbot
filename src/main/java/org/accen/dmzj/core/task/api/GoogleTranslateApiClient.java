package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;
import org.accen.dmzj.core.task.api.vo.YoudaoTranslateResult;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "http://translate.google.cn")
public interface GoogleTranslateApiClient {
	/**
	 * google翻译api
	 * @param lang
	 * @param word
	 * @return
	 */
	@RequestLine("GET /translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=zh_CN&tl={lang}&q={word}")
	public Map<String,Object> translate(@Param("lang")String lang,@Param("word")String word);
}
