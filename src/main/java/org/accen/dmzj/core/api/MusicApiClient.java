package org.accen.dmzj.core.api;

import org.accen.dmzj.core.api.vo.Music163Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import feign.Param;
@FeignClient(
		name="music163",
		url="http://music.163.com")
public interface MusicApiClient {
	/**
	 * 网易云音乐搜索api
	 * @param s 搜索的内容
	 * @param offset 偏移（分页）
	 * @param limit 数量
	 * @param type 1-歌曲，10-专辑，100-歌手，1000-歌单，1002-歌手，1004-mv，1006-歌词，1009电台
	 * @return
	 */
	@GetMapping("/api/search/get/?type=1")
	public Music163Result music163Search(@RequestParam("s")String s,@RequestParam("offset")int offset,@RequestParam("limit")int limit,@Param("type")int type);
}
