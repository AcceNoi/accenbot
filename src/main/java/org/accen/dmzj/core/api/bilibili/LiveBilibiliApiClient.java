package org.accen.dmzj.core.api.bilibili;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="bilibili-live",
			url="https://api.live.bilibili.com")
public interface LiveBilibiliApiClient {
	/**
	 * 获取直播间相关信息
	 * @param roomId
	 * @return
	 */
	@GetMapping("/xlive/web-room/v1/index/getInfoByRoom")
	public Map<String, Object> infoByRoom(@RequestParam("room_id")String roomId);
}
