package org.accen.dmzj.core.feign.auth;

import java.util.HashMap;
import java.util.Map;

import feign.RequestTemplate;
/**
 * 一个简单的实现，使用Header来做登录验证的拦截器
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class HeaderAuthFeignRequestInterceptor extends AuthFeignRequestInterceptor{
	private Map<String, String> authHeaders =new HashMap<>(2);
	@Override
	public void resolveRequestTemplate(RequestTemplate template) {
		authHeaders.keySet().forEach(key->{
			if(!template.headers().containsKey(key)) {
				template.header(key, authHeaders.get(key));
			}
		});
	}
	protected void addHeader(String headerKey,String headerValue) {
		authHeaders.put(headerKey, headerValue);
	}
}
