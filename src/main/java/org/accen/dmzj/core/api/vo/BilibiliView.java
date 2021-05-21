package org.accen.dmzj.core.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("preview")
@JsonIgnoreProperties(ignoreUnknown = true)
public record BilibiliView(@JsonProperty("code") int code,@JsonProperty("message")String message,@JsonProperty("ttl")int ttl,@JsonProperty("data")BilibiliViewData data) {
	public record BilibiliViewData(@JsonProperty("bvid")String bvid,@JsonProperty("aid")int aid,@JsonProperty("videos")int videos,@JsonProperty("tid")int tid,@JsonProperty("tname")String tname,
			@JsonProperty("copyright")int copyright,@JsonProperty("pic")String pic,@JsonProperty("title")String title,@JsonProperty("pubdate")long pubdate,@JsonProperty("ctime")long ctime,
			@JsonProperty("desc")String desc,@JsonProperty("state")int state,@JsonProperty("duration")int duration,@JsonProperty("rights")BilibiliViewDataRights rights,@JsonProperty("owner")BilibiliViewDataOwner owner,
			@JsonProperty("stat")BilibiliViewDataStat stat,@JsonProperty("dynamic")String dynamic,@JsonProperty("cid")int cid,@JsonProperty("dimension")BilibiliViewDataDimension dimension,
			@JsonProperty("no_cache")boolean no_cache,@JsonProperty("pages")BilibiliViewDataPage[] pages) {}
	public record BilibiliViewDataRights(@JsonProperty("bp")int bp,@JsonProperty("elec")int elec,@JsonProperty("download")int download,@JsonProperty("movie")int movie,@JsonProperty("play")int play,
			@JsonProperty("hd5")int hd5,@JsonProperty("no_reprint")int no_reprint,@JsonProperty("autoplay")int autoplay,@JsonProperty("ugc_pay")int ugc_pay,@JsonProperty("is_coopration")int is_cooperation,
			@JsonProperty("ugc_pay_preview")int ugc_pay_preview,@JsonProperty("no_background")int no_background,@JsonProperty("clean_mode")int clean_mode,@JsonProperty("is_stein_gate")int is_stein_gate) {}
	public record BilibiliViewDataOwner(@JsonProperty("mid")int mid,@JsonProperty("name")String name,@JsonProperty("face")String face) {}
	public record BilibiliViewDataStat(@JsonProperty("aid")int aid,@JsonProperty("view")int view,@JsonProperty("danmuku")int danmuku,@JsonProperty("reply")int reply,@JsonProperty("favorite")int favorite,
			@JsonProperty("coin")int coin,@JsonProperty("share")int share,@JsonProperty("now_rank")int now_rank,@JsonProperty("his_rank")int his_rank,@JsonProperty("like")int like,@JsonProperty("dislike")int dislike,
			@JsonProperty("evaluation")String evaluation,@JsonProperty("argue_msg")String argue_msg) {};
	public record BilibiliViewDataDimension(@JsonProperty("width")int width,@JsonProperty("height")int height,@JsonProperty("rotate")int rotate) {};
	public record BilibiliViewDataPage(@JsonProperty("cid")int cid,@JsonProperty("page")int page,@JsonProperty("from")String from,@JsonProperty("part")String part,@JsonProperty("duration")int duration,
			@JsonProperty("vid")String vid,@JsonProperty("weblink")String weblink,@JsonProperty("dimension")BilibiliViewDataDimension dimension) {}
}
