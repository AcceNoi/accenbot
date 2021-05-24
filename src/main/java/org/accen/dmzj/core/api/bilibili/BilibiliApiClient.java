package org.accen.dmzj.core.api.bilibili;

import java.util.Map;

import org.accen.dmzj.core.api.vo.BilibiliSearch;
import org.accen.dmzj.core.api.vo.BilibiliView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignClient(
		name="bilibili-api",
		url="https://api.bilibili.com")
public interface BilibiliApiClient {
	/**
	 * b站用户
	 */
	public final static String SEARCH_TYPE_USER = "bili_user";
	/**
	 * 番剧
	 */
	public final static String SEARCH_TYPE_BANGUMI = "media_bangumi";
	
	
	@GetMapping("/x/web-interface/search/type")
	public BilibiliSearch search(@RequestParam("page")int page,@RequestParam("keyword")String kw,@RequestParam("search_type")String type);

	@Deprecated
	@GetMapping("/x/web-interface/view")
	public Map<String, Object> viewByAid(@RequestParam("aid")String avid);
	
	@Deprecated
	@GetMapping("/x/web-interface/view")
	public Map<String, Object> viewByBid(@RequestParam("bvid")String bvid);
	
	/**
	 * 获取视频的基本信息，比如获得封面、cid等，avid和bvid择一即可
	 * @param avid
	 * @param bvid
	 */
	@GetMapping("/x/web-interface/view")
	public BilibiliView view(@RequestParam("aid")String avid,@RequestParam("bvid")String bvid);
	/**
	 * 获取视频的播放连接
	 * @param cid 
	 * @param qn 16-360p,32-480p,64-720p,80-1080p
	 * @param aid
	 * @param bvid
	 */
	@GetMapping("/x/player/playurl")
	public BilibiliPlayUrl playurl(@RequestParam("cid")int cid,@RequestParam("qn")int qn,@RequestParam("aid")String aid,@RequestParam("bvid")String bvid);
	@SuppressWarnings("preview")
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record BilibiliPlayUrl(@JsonProperty("code") int code,@JsonProperty("message")String message,@JsonProperty("ttl")int ttl,@JsonProperty("data")BilibiliPlayUrlData data) {
		public record BilibiliPlayUrlData(@JsonProperty("from")String from,@JsonProperty("result")String result,@JsonProperty("message")String message,@JsonProperty("quality")int quality,
				@JsonProperty("format")String format,@JsonProperty("timelength")int timelength,@JsonProperty("accept_format")String accept_format,@JsonProperty("accept_description")String[] accept_description,
				@JsonProperty("accept_quality")int[] accept_quality,@JsonProperty("video_codecid")int video_codecid,@JsonProperty("seek_param")String seek_param,@JsonProperty("seek_type")String seek_type,
				@JsonProperty("durl")BilibiliPlayUrlDurl[] durl) {}
		public record BilibiliPlayUrlDurl(@JsonProperty("order")int order,@JsonProperty("length")int length,@JsonProperty("size")int size,@JsonProperty("ahead")String ahead,@JsonProperty("vhead")String vhead,
				@JsonProperty("url")String url,@JsonProperty("back_url")String[] back_url) {}
	}
}
