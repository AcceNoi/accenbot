package org.accen.dmzj.core.feign.auth;

import feign.RequestTemplate;
/**
 * 
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public abstract class AbstractHeaderAuthCounterFeignRequestInterceptor 
						extends HeaderAuthFeignRequestInterceptor 
						implements FirstTimeCallCase{
	private int callCount = 0;
	@Override
	public void resolveRequestTemplate(RequestTemplate template) {
		if(callCount == 0) {
			firstTimeDo();
		}
		super.resolveRequestTemplate(template);
		callCount++;
	}
	
}
