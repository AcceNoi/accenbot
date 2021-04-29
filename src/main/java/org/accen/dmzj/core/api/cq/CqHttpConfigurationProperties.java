package org.accen.dmzj.core.api.cq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(CqHttpConfigurationProperties.CONFIG_CQHTTP_PREFIX)
@ConstructorBinding
public class CqHttpConfigurationProperties {
	public final static String CONFIG_CQHTTP_PREFIX = "cq";
	private String pushUrl;
	/**
	 * 推送cq的地址
	 * @return
	 */
	public String pushUrl() {
		return this.pushUrl;
	}
	/**
	 * 调用cq api时所需的token
	 */
	private String token;
	public String token() {
		return this.token;
	}
	/**
	 * 最大重试次数
	 */
	private int maxAttempt;
	public int maxAttempt() {return this.maxAttempt;}
	private long period;
	public long period() {return this.period;}
	private long maxPeriod;
	public long maxPeriod() {return this.maxPeriod;}
	/**
	 * 超时
	 */
	private int readTimeout;
	public int readTimeout() {return this.readTimeout;}
	/**
	 * 链接超时
	 */
	private int connectTimeout;
	public int connectTimeout() {return this.connectTimeout;}

	public CqHttpConfigurationProperties(@DefaultValue("http://localhost:5700")String pushUrl,
								String token,
								@DefaultValue("1")int maxAttempt,
								@DefaultValue("60000")int readTimeout,
								@DefaultValue("10000")int connectTimeout) {
		this.pushUrl = pushUrl;
		this.token = token;
		this.maxAttempt = maxAttempt;
		this.readTimeout = readTimeout;
		this.connectTimeout = connectTimeout;
	}
}
