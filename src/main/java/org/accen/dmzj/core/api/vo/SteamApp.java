package org.accen.dmzj.core.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("preview")
@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamApp(@JsonProperty("applist")AppList applist) {
	public record AppList(@JsonProperty("apps") App[] apps) {
		public record App(@JsonProperty("appid")int appid,@JsonProperty("name") String name) {}
	}
}
