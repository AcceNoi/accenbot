package org.accen.dmzj.core.api.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("preview")
@JsonIgnoreProperties(ignoreUnknown = true)
public record BilibiliSearch(@JsonProperty("code") int code,@JsonProperty("message") String message,@JsonProperty("ttl") int ttl,@JsonProperty("data") BilibiliSearchData data) {
	public record BilibiliSearchData(@JsonProperty("seid") String seid,@JsonProperty("page") int page,@JsonProperty("pagesize") int pagesize,@JsonProperty("numResults") int numResults
			,@JsonProperty("numPages") int numPages,@JsonProperty("suggest_keyword") String suggest_keyword,@JsonProperty("rqt_type") String rqt_type,@JsonProperty("result") Map<String,Object>[] result) {
		/*public record BilibiliSearchDataResult(@JsonProperty("type") String type,@JsonProperty("mid") int mid,@JsonProperty("uname") String uname,@JsonProperty("usign") String usign
				,@JsonProperty("fans") int fans,@JsonProperty("videos") int videos,@JsonProperty("upic") String upic,@JsonProperty("verify_info") String verify_info,@JsonProperty("level") int level
				,@JsonProperty("gender") int gender,@JsonProperty("is_upuser") int is_upuser,@JsonProperty("is_live") int is_live,@JsonProperty("room_id") int room_id) {}
		*/
	}
}
