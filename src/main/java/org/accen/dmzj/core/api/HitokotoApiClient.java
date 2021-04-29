package org.accen.dmzj.core.api;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @see <a href="https://developer.hitokoto.cn/">一言开发者中心</a>
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignClient(
		name="hitokoto-v1",
		url="https://v1.hitokoto.cn")
public interface HitokotoApiClient {
	@GetMapping("/?c=a&c=b&c=c&encode=json&charset=utf-8")
	Map<String, Object> hitokoto();
}
