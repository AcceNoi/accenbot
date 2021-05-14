package org.accen.dmzj.core.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("preview")
@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamNew(@JsonProperty("appnews") AppNew appnews) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record AppNew(@JsonProperty("appid") int appid,@JsonProperty("newsitems") NewsItem[] newsitems,@JsonProperty("count") int count) {}
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record NewsItem(@JsonProperty("gid") long gid,@JsonProperty("title") String title,@JsonProperty("url") String url,@JsonProperty("is_external_url") boolean is_external_url,@JsonProperty("author") String author,@JsonProperty("contents") String contents,@JsonProperty("feedlabel") String feedlabel,@JsonProperty("date") int date,@JsonProperty("feedname") String feedname,@JsonProperty("feed_type") int feed_type,@JsonProperty("appid") int appid) {}
}
