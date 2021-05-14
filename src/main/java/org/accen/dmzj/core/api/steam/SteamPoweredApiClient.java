package org.accen.dmzj.core.api.steam;

import org.accen.dmzj.core.api.vo.SteamApp;
import org.accen.dmzj.core.api.vo.SteamNew;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="steam-api",url="https://api.steampowered.com/")
public interface SteamPoweredApiClient {
	/**
	 * 获取某个steam app的新闻
	 * @param appid required steam appid
	 * @param maxlength
	 * @param enddatestamp unix epoch timestamp
	 * @param count
	 * @param feeds
	 * @param tags
	 * @return
	 */
	@GetMapping("/ISteamNews/GetNewsForApp/v2/")
	public SteamNew appNews(@RequestParam("appid")int appid,@RequestParam(name="maxlength",required = false)int maxlength,
										@RequestParam(name="enddate",required = false)int enddatestamp,@RequestParam(name="count",required = false)int count,
										@RequestParam(name="feeds",required = false)String feeds,@RequestParam(name="tags",required = false)String tags);
	/**
	 * @see SteamPoweredApiClient#appNews(int, int, int, int, String, String)
	 * @param appid
	 * @param count
	 * @return
	 */
	@GetMapping("/ISteamNews/GetNewsForApp/v2/")
	public SteamNew appNews(@RequestParam("appid")int appid,@RequestParam(name="count",required = false)int count);
	
	/**
	 * 获取steam app列表
	 * @return
	 */
	@GetMapping("/ISteamApps/GetAppList/v2/")
	public SteamApp appList();
	
	/**
	 * 获取steam app列表
	 * @return
	 */
	@GetMapping("/ISteamApps/GetAppList/v2/")
	public String appListStr();
}