package org.accen.dmzj.core.task.api;
/**
 * <a href="http://api.jirengu.com/">饥人谷</a>个人开放的api
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "http://api.jirengu.com")
public interface JiRenGuApiClient {
	/**
	 * 天气api
	 * @param city
	 * @return
	 */
	@RequestLine("GET /getWeather.php?city={city}")
	public Map<String, Object> weather(@Param("city")String city);
}
