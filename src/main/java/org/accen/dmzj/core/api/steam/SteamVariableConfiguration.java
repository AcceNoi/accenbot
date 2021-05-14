package org.accen.dmzj.core.api.steam;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties("steam.variable")
@ConstructorBinding
public class SteamVariableConfiguration {
	private SteamNewsFormatter newsFormatter;
	public SteamNewsFormatter newsFormatter() {return this.newsFormatter;}
	
	private String STEAM_CLAN_IMAGE;
	public String STEAM_CLAN_IMAGE() {
		return this.STEAM_CLAN_IMAGE;
	}
	public SteamVariableConfiguration(@Name("STEAM_CLAN_IMAGE")@DefaultValue("https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/clans") String STEAM_CLAN_IMAGE) {
		this.STEAM_CLAN_IMAGE = STEAM_CLAN_IMAGE;
		this.newsFormatter = new SteamNewsFormatter(this);
	}
}
