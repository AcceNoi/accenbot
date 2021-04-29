package org.accen.dmzj.core.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.api.pixivc.PixivicApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 画师定时检索图片的schedule
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class PixivcSchedule {
	private static final Logger logger = LoggerFactory.getLogger(PixivcSchedule.class);
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private CmdBuSubMapper cmdBuSubMapper;
	@Autowired
	private PixivicApiClient pixivicApiClient;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Value("${coolq.bot}")
	private String botId;
	/**
	 * 扫描artist。每天十点执行一次，因为接口只返回了创建日期，没有具体的时间
	 */
	@Scheduled(cron = "0 0 10 * * *")
	public void scanArtist() {
		java.util.Date curDate = new Date();
		List<CmdBuSub> subs = cmdBuSubMapper.findBySubType("group", "pixiv", "artist");
		if(subs!=null&&!subs.isEmpty()) {
			//形如CmdBuSub.subObj -> CmdBuSub.targetId->List<CmdBuSub>
			Map<String, Map<String,List<CmdBuSub>>> subMap = new HashMap<String, Map<String,List<CmdBuSub>>>();
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
			subMap.forEach((artistId,subTarget)->{
				Map<String,Object> illusts = pixivicApiClient.artistIllusts(Integer.parseInt(artistId),1, 5);
				if(illusts.containsKey("data")) {
					List<Map<String,Object>> illustDatas = (List<Map<String, Object>>) illusts.get("data");
					if(illustDatas!=null&&!illustDatas.isEmpty()) {
						StringBuffer msgIllust = new StringBuffer();
						long count = illustDatas.stream().filter(illustData->{
							try {
								return sdf.parse(illustData.get("createDate")+" 23:59:59").getTime()>=curDate.getTime();
							} catch (ParseException e) {
								e.printStackTrace();
							}
							return false;
						}).map(illustData->{
							msgIllust.append(CQUtil.image(((List<Map<String,Object>>)illustData.get("imageUrls")).get(0).get("large").toString().replace("i.pximg.net", "i.pixiv.cat")));
							msgIllust.append("pid:"+illustData.get("id")).append("，title:"+illustData.get("title")).append("\n");
							return 1;
						}).count();
						
						
						subTarget.forEach((targetId,subscribers)->{
							StringBuffer msg = new StringBuffer();
							String ats = subscribers.stream()
									.map(subscri->CQUtil.at(subscri.getSubscriber()))
									.collect(Collectors.joining(""));
							msg.append(ats);
							msg.append(" 您订阅的P站画师【")
								.append(subscribers.get(0).getSubObjMark())
								.append("】更新了"+count+"篇作品：")
								.append("\n")
								.append(msgIllust);
							logger.debug(msg.toString());
							taskManager.addGeneralTaskQuick(botId, "group", targetId, msg.toString());
							
						});
						
					}
				}
			});
		}
	}
}
