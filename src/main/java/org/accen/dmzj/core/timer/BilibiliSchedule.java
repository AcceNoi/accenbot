package org.accen.dmzj.core.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.task.api.BilibiliSearchApiClientPk;
import org.accen.dmzj.core.task.api.vo.BilibliVideoInfo;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BilibiliSchedule {
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private CmdBuSubMapper cmdBuSubMapper;
	@Autowired
	private BilibiliSearchApiClientPk bilibiliSearchApiClientPk;
	
	@Value("${coolq.bot}")
	private String botId;
	
	private static final Logger logger = LoggerFactory.getLogger(BilibiliSchedule.class);
	/**
	 * 每15分钟执行一次，也就是说最糟糕情况下，会有153*订阅up总数分钟的延迟。但是由于api是<a href="http://docs.kaaass.net/showdoc/web/#/2?page_id=3">Kaass</a>提供的，还是不要调用过于频繁
	 */
	@Scheduled(cron = "0 */15 * * * ?")
	public void bilibiUpScan() {
		//先获取当前时间戳，用于15min内投稿即为新投稿
		long curTimestamp=new Date().getTime();
		
		List<CmdBuSub> subs = cmdBuSubMapper.findBySubType("group", "bilibili", "up");
		if(subs!=null&&!subs.isEmpty()) {
			//形如CmdBuSub.subObj -> CmdBuSub.targetId->List<CmdBuSub>
			Map<String, Map<String,List<CmdBuSub>>> subMap = new HashMap<String, Map<String,List<CmdBuSub>>>();
			subs.stream().filter(sub->botId.equals(sub.getBotId())).collect(Collectors.toList());
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
			//========初始化结束
			
			//========开始调用
			subMap.forEach((key,value)->{
				List<BilibliVideoInfo>  infos = bilibiliSearchApiClientPk.searchUpVideo(Long.parseLong(key));
				logger.info(infos.toString());
				if(infos!=null&&!infos.isEmpty()) {
					infos.forEach(info->{
						if(curTimestamp-info.getPostTime()<=15*60*1000) {
							//15分钟以内，则认为是最新投稿的视频
							value.forEach((targetId,subscribers)->{
								GeneralTask task = new GeneralTask();
								task.setSelfQnum(botId);
								task.setType("group");
								task.setTargetId(targetId);
								String ats = subscribers.stream()
											.map(subscri->CQUtil.at(subscri.getSubscriber()))
											.collect(Collectors.joining(" "));
											
								task.setMessage(ats+"您订阅的B站UP主"+subscribers.get(0).getSubObjMark()+"("+subscribers.get(0).getSubObj()+")更新视频啦："
										+info.getTitle()+"[https://www.bilibili.com/video/av"+info.getaId()+"] 快去围观吧喵~");
								taskManager.addGeneralTask(task);
							});
						}
					});
				}
			});
		}
		
	}
}
