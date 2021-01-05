package org.accen.dmzj.core.task.api;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;
import org.springframework.stereotype.Component;

import feign.RequestLine;
/**
 * @see <a href="https://developer.hitokoto.cn/">一言开发者中心</a>
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignApi(host = "https://v1.hitokoto.cn")
@Component
public interface HitokotoApiClient {
	@RequestLine("GET /?c=a&c=b&c=c&encode=json&charset=utf-8")
	Map<String, Object> hitokoto();
}
