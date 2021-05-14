package org.accen.dmzj.core.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.api.cq.CqHttpConfigurationProperties;
import org.accen.dmzj.core.api.steam.SteamPoweredApiClient;
import org.accen.dmzj.core.api.steam.SteamVariableConfiguration;
import org.accen.dmzj.core.api.vo.SteamNew;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SteamNewsSchedule {
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private CmdBuSubMapper cmdBuSubMapper;
	@Autowired
	private CqHttpConfigurationProperties cqProp;
	@Autowired
	private SteamVariableConfiguration steamVar;
	@Autowired
	private SteamPoweredApiClient steamApi;
	
	@Scheduled(cron = "0 */30 * * * *")
	public void newsScan() {
		int cur = (int) (new Date().getTime()/1000);
		List<CmdBuSub> subs = cmdBuSubMapper.findBySubType("group", "steam", "app");
		if(subs!=null&&!subs.isEmpty()) {
			Map<String, Map<String,List<CmdBuSub>>> subMap = new HashMap<String, Map<String,List<CmdBuSub>>>();
			subs.stream().filter(sub->cqProp.botId().equals(sub.getBotId())).collect(Collectors.toList());
			subs.forEach(sub->{
				String key = sub.getSubObj();
				
				if(!subMap.containsKey(key)) {
					subMap.put(key, new HashMap<String, List<CmdBuSub>>());
				}
				if(!subMap.get(key).containsKey(sub.getTargetId())) {
					subMap.get(key).put(sub.getTargetId(), new LinkedList<CmdBuSub>());
				}
				subMap.get(key).get(sub.getTargetId()).add(sub);
			});
			
			subMap.forEach((appid,subrecord)->{
				//只取第一条
				SteamNew news = steamApi.appNews(Integer.valueOf(appid), 1);
				/**
				 * 如果最新的一条新闻是在30min之内的，则发出
				 */
				if(news.appnews().count()>=1&&news.appnews().newsitems()[0].date()>=cur-30*60) {
					String content = steamVar.newsFormatter().formatContents(news.appnews().newsitems()[0].contents());
					String url = news.appnews().newsitems()[0].url();
					String title = news.appnews().newsitems()[0].title();
					subrecord.forEach((target,subscribers)->{
						//需要at的信息
						String ats = subscribers.stream()
								.map(subscri->CQUtil.at(subscri.getSubscriber()))
								.collect(Collectors.joining(""));
						StringBuffer msg = new StringBuffer();
						msg.append(ats)
							.append("您订阅的Steam游戏")
							.append(subscribers.get(0).getSubObjMark())
							.append("[")
							.append(appid)
							.append("]有新动态：【")
							.append(title)
							.append("】\n")
							.append(content)
							.append("\n详见:")
							.append(url);
						taskManager.addGeneralTaskQuick(cqProp.botId(), "group", target, msg.toString());
					});
					
					
				}
			});
		}
	}
	
}
