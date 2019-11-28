package org.accen.dmzj.core.task.api.bilibili;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;

@FeignApi(host = "https://api.live.bilibili.com")
public interface LiveBilibiliApiClient {
	/**
	 * 获取直播间相关信息
	 * @param roomId
	 * @return
	 */
	@RequestLine("GET /xlive/web-room/v1/index/getInfoByRoom?room_id={roomId}")
	public Map<String, Object> infoByRoom(@Param("roomId")String roomId);
}
