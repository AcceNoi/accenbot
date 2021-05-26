package org.accen.dmzj;

import java.util.HashMap;
import java.util.Map;

import org.accen.dmzj.core.api.cq.CqHttpConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.yyuueexxiinngg.onebot.MainKt;
import com.github.yyuueexxiinngg.onebot.PluginSettings;
import com.github.yyuueexxiinngg.onebot.PluginSettings.BotSettings;
import com.github.yyuueexxiinngg.onebot.PluginSettings.HTTPSettings;
import com.github.yyuueexxiinngg.onebot.PluginSettings.HeartbeatSettings;
import com.github.yyuueexxiinngg.onebot.PluginSettings.WebsocketReverseClientSettings;
import com.github.yyuueexxiinngg.onebot.PluginSettings.WebsocketServerSettings;

import kotlin.collections.CollectionsKt;

@Component
public class OnebotKotlinDaemon {
	@Value("${server.port}")
	private int port;
	@Autowired
	private CqHttpConfigurationProperties cqProp;
	private void buildPluginSetting() {
		HeartbeatSettings hb = new HeartbeatSettings(true, 1500);
		HTTPSettings h = new HTTPSettings(true, "0.0.0.0", 5700, cqProp.token(), "http://127.0.0.1:"+port+"/event/accept", "string", "", 0);
		BotSettings botSettings1 = new BotSettings(
				true
				, true
				, hb
				, h
				, CollectionsKt.mutableListOf(new WebsocketReverseClientSettings())
				, new WebsocketServerSettings());
		Map<String, BotSettings> bots = new HashMap<String, PluginSettings.BotSettings>();
		bots.put(cqProp.botId(), botSettings1);
		PluginSettings.INSTANCE.setBots(bots);
	}
	public void setUpOnebotKotlin(String[] args) {
		final String[] argss = args;
		buildPluginSetting();
		new Thread(()->{
			MainKt.main(argss);
		}).start();
	}
}
