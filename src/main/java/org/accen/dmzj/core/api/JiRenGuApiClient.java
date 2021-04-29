package org.accen.dmzj.core.api;
/**
 * <a href="http://api.jirengu.com/">饥人谷</a>个人开放的api
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
		name="jirengu",
		url="http://api.jirengu.com")
public interface JiRenGuApiClient {
	/**
	 * 天气api
	 * @param city
	 * @return
	 */
	@GetMapping("/getWeather.php")
	public Map<String, Object> weather(@RequestParam("city")String city);
}
