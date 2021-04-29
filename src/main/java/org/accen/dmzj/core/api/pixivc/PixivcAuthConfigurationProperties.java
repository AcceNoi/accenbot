package org.accen.dmzj.core.api.pixivc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(PixivcAuthConfigurationProperties.CONFIG_PIXIV_PREFIX)
@ConstructorBinding
public class PixivcAuthConfigurationProperties{
	/*@Bean
	public PixivcAuthConfigurationProperties pixivcAuthConfigurationProperties() {
		return this;
	}*/
	
	public static final String CONFIG_PIXIV_PREFIX = "pixivc";
	/*
	 * 登录失败重试次数
	 */
	private int loginRetry;
	public int loginRetry() {return loginRetry;}
	/*
	 * 本地存储的authorization地址
	 */
	private String authLocation;
	public String authLocation() {return authLocation;}
	/*
	 * 登录用户名
	 */
	private String username;
	public String username() {return username;}
	/*
	 * 密码
	 */
	private String password;
	public String password() {return password;}
	/*
	 * verificationCode获取链接
	 */
	private String vcUrl;
	public String vcUrl() {return vcUrl;}
	private Class<? extends AuthHolder> authHolderClass;
	public Class<? extends AuthHolder> authHolderClass(){return authHolderClass;}
	private Class<? extends AuthFresher> authFresherClass = org.accen.dmzj.core.api.pixivc.HttpAuthFresher.class;
	public Class<? extends AuthFresher> authFresherClass(){return authFresherClass;}
	public PixivcAuthConfigurationProperties(int loginRetry
								,@DefaultValue("pixivc.auth")String authLocation
								,@Name("account")String username
								,String password
								,@DefaultValue("https://pix.ipv4.host/verificationCode") String vcUrl
								,@DefaultValue("org.accen.dmzj.core.api.pixivc.LocalAuthHolder") Class<? extends AuthHolder> authHolderClass
								,@DefaultValue("org.accen.dmzj.core.api.pixivc.HttpAuthFresher") Class<? extends AuthFresher> authFresherClass) {
		this.loginRetry = loginRetry;
		this.authLocation = authLocation;
		this.username = username;
		this.password = password;
		this.vcUrl = vcUrl;
		this.authHolderClass = authHolderClass;
		this.authFresherClass = authFresherClass;
	}
	
}
