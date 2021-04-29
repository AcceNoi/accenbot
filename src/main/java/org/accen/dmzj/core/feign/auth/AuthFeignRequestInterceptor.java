package org.accen.dmzj.core.feign.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
/**
 * 用于认证的拦截器
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public abstract class AuthFeignRequestInterceptor implements RequestInterceptor {
	/**
	 * 通过实现此方法，为request添加认证所需
	 * @param template
	 */
	abstract protected void resolveRequestTemplate(RequestTemplate template);
	@Override
	public void apply(RequestTemplate template) {
		resolveRequestTemplate(template);
	}

}
