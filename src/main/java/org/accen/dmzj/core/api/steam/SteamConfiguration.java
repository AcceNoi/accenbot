package org.accen.dmzj.core.api.steam;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.accen.dmzj.core.api.vo.SteamApp;
import org.accen.dmzj.core.feign.GloabalFeignConfigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties("steam")
@ConstructorBinding
public class SteamConfiguration {
	private String key;
	private String steamAppListLocation;
	public String steamAppListLocation() {return this.steamAppListLocation;}
	public String key() {
		return this.key;
	}
	public SteamConfiguration(@Name("key")String key,@DefaultValue("steam_app_list.json")String steamAppListLocation) {
		this.key = key;
		this.steamAppListLocation = steamAppListLocation;
	}
	
	SteamApp steamApp;
	public SteamApp steamApp() {return steamApp;}
	@Bean
	public ApplicationRunner steamAppListCheckRunner(@Autowired SteamPoweredApiClient apiClient,@Autowired SteamConfiguration conf) {
		final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
		return args->{
			File localAppListFile = new File(conf.steamAppListLocation);
			if(!localAppListFile.exists()) {
				localAppListFile.createNewFile();
			}
			if(localAppListFile.length()>0) {
				try{
					conf.steamApp = GloabalFeignConfigration
							.recordSupportObjectMapper()
							.readValue(Files.readString(localAppListFile.toPath(), Charset.forName("UTF-8")), SteamApp.class);
					return;
				}catch(Exception e) {
					//解析json出错
					logger.warn("从{}获取的steam app list出错，将使用{}获取...",conf.steamAppListLocation,"SteamPoweredApiClient");
				}
			}
			
			//配置文件为空，或者内容解析失败，则从接口中获取
			conf.steamApp = apiClient.appList();
			Files.writeString(localAppListFile.toPath()
					, GloabalFeignConfigration.recordSupportObjectMapper().writeValueAsString(conf.steamApp)
					, Charset.forName("UTF-8"));
		};
	}
}


