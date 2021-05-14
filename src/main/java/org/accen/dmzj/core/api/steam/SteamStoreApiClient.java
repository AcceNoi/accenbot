package org.accen.dmzj.core.api.steam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="steam-store",url="https://store.steampowered.com")
public interface SteamStoreApiClient {
	/**
	 * 获取
	 * @param appid
	 * @return
	 */
	@GetMapping("/app/{appid}")
	public String app(@PathVariable("appid")int appid);
}
