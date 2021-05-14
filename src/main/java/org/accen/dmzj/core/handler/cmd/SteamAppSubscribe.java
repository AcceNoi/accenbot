package org.accen.dmzj.core.handler.cmd;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.api.steam.SteamConfiguration;
import org.accen.dmzj.core.api.steam.SteamStoreApiClient;
import org.accen.dmzj.core.api.vo.SteamApp.AppList.App;
import org.accen.dmzj.core.handler.group.Subscribe;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.accen.dmzj.web.vo.Qmessage;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@FuncSwitch(title = "订阅Steam游戏",format = "订阅Steam游戏+[appid]",showMenu = true,groupClass = Subscribe.class)
@Component
@Transactional
public class SteamAppSubscribe implements CmdAdapter{
	private final static String TARGET_SIGN = "steam";
	private final static String TARGET_TYPE_SIGN = "app";
	
//	@Autowired
//	private SteamStoreApiClient steamStoreApiClient;
	@Autowired
	private CmdBuSubMapper subMapper;
	@Autowired
	private SteamConfiguration steamConfig;
	
	private final static Pattern subPattern = Pattern.compile("^订阅Steam游戏(\\d+)$",Pattern.CASE_INSENSITIVE);
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		Matcher matcher = subPattern.matcher(qmessage.getMessage());
		if(matcher.matches()) {
			String appid = matcher.group(1);
			List<CmdBuSub> existedSubs = subMapper.findBySubscriberAndObj(qmessage.getMessageType(), qmessage.getGroupId()
					, qmessage.getUserId(), TARGET_SIGN, TARGET_TYPE_SIGN, appid);
			if(existedSubs!=null&&!existedSubs.isEmpty()) {
				return new GeneralTask(qmessage.getMessageType(), qmessage.getGroupId()
						, "%s 你已经订阅了%s[%s]，无需重复订阅喵！".formatted(CQUtil.at(qmessage.getUserId()),existedSubs.get(0).getSubObjMark(),appid), selfQnum);
			}else {
//				String html = steamStoreApiClient.app(Integer.valueOf(appid));
//				Elements appEles = Jsoup.parse(html).select("div.apphub_AppName");
				int appidI = Integer.valueOf(appid);
				Optional<App> fi = Arrays.stream(steamConfig.steamApp().applist().apps())
						.filter(app->app.appid() == appidI)
						.findFirst();
				if(fi.isEmpty()) {
					//没查到此appid
					return new GeneralTask(qmessage.getMessageType(), qmessage.getGroupId()
							, "%s 未找到此appid：%s喵！".formatted(CQUtil.at(qmessage.getUserId()),appid), selfQnum);
				}else {
					String appName = fi.get().name();
					CmdBuSub subRecord = new CmdBuSub();
					subRecord.setType(qmessage.getMessageType());
					subRecord.setTargetId(qmessage.getGroupId());
					subRecord.setSubscriber(qmessage.getUserId());
					subRecord.setSubTarget(TARGET_SIGN);
					subRecord.setSubType(TARGET_TYPE_SIGN);
					subRecord.setSubObj(appid);
					subRecord.setSubObjMark(appName);
					subRecord.setSubTime(new Date());
					subRecord.setStatus("1");
					subMapper.insert(subRecord);
					return new GeneralTask(qmessage.getMessageType(), qmessage.getGroupId()
							, "%s 已成功订阅%s[%s]喵！".formatted(CQUtil.at(qmessage.getUserId()),appName,appid), selfQnum);
				}
			}
		}
		return null;
		
	}

}
