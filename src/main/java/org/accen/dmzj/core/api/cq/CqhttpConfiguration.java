package org.accen.dmzj.core.api.cq;

import java.util.concurrent.TimeUnit;

import org.accen.dmzj.core.feign.auth.HeaderAuthFeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import feign.Request.Options;
import feign.RequestInterceptor;
import feign.Retryer;

@Configuration
public class CqhttpConfiguration {
	private CqHttpConfigurationProperties prop;
	public CqhttpConfiguration(CqHttpConfigurationProperties prop) {
		this.prop = prop;
	}
	@Bean
	public RequestInterceptor cqhttpRequestInterceptor() {
		return new CqhttpAuthRequestInterceptor(this.prop);
	}
	@Bean
	public Retryer cqhttpRetryer() {
		return new Retryer.Default(this.prop.period(), this.prop.maxPeriod(), this.prop.maxAttempt());
	}
	@Bean
	public Options cqhttpOptions() {
		return new Options(this.prop.connectTimeout(),TimeUnit.MILLISECONDS, this.prop.readTimeout(),TimeUnit.MILLISECONDS,true);
	}
}
class CqhttpAuthRequestInterceptor extends HeaderAuthFeignRequestInterceptor{
	private CqHttpConfigurationProperties prop;
	public CqhttpAuthRequestInterceptor(CqHttpConfigurationProperties prop) {
		this.prop = prop;
		if(StringUtils.hasLength(this.prop.token())){
			super.addHeader("Authorization", "Bearer "+this.prop.token());
		}
	}
}
