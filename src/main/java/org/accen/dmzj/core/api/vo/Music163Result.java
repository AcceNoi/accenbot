package org.accen.dmzj.core.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("preview")
@JsonIgnoreProperties(ignoreUnknown = true)
public record Music163Result(@JsonProperty("result") Music163Ctt result,@JsonProperty("code") String code,@JsonProperty("msg") String msg) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Music163Ctt(@JsonProperty("songs") Music163[] songs,@JsonProperty("songCount") int songCount) {}
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Music163(@JsonProperty("id") long id,@JsonProperty("name") String name,@JsonProperty("position") int position
			,@JsonProperty("alias") Object alias,@JsonProperty("status") int status,@JsonProperty("fee") int fee,@JsonProperty("copyrightId") long copyrightId
			,@JsonProperty("disc") String disc,@JsonProperty("no") int no,@JsonProperty("mp3Url") String mp3Url) {}
}