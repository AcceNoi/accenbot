package org.accen.dmzj.core.task.api;

import org.accen.dmzj.core.task.api.vo.Music163Result;

import feign.Param;

public interface MusicApiClient {
	/**
	 * 网易云音乐搜索api
	 * @param s 搜索的内容
	 * @param offset 偏移（分页）
	 * @param limit 数量
	 * @param type 1-歌曲，10-专辑，100-歌手，1000-歌单，1002-歌手，1004-mv，1006-歌词，1009电台
	 * @return
	 */
	public Music163Result music163Search(@Param("s")String s,@Param("offset")String offset,@Param("limit")String limit,@Param("type")String type);
}
