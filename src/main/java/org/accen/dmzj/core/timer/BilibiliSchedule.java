package org.accen.dmzj.core.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.task.api.bilibili.ApiVcBilibiliApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CmdBuSubMapper;
import org.accen.dmzj.web.vo.CmdBuSub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

@Component
public class BilibiliSchedule {
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private CmdBuSubMapper cmdBuSubMapper;
	//@Autowired
	//private BilibiliSearchApiClientPk bilibiliSearchApiClientPk;
	@Autowired
	private ApiVcBilibiliApiClient apiVc;
	
	@Value("${coolq.bot}")
	private String botId;
	
	private static final Logger logger = LoggerFactory.getLogger(BilibiliSchedule.class);
	/**
	 * 每2分钟执行一次，也就是说最糟糕情况下，数分钟的延迟。但是由于api是<a href="http://docs.kaaass.net/showdoc/web/#/2?page_id=3">Kaass</a>提供的，还是不要调用过于频繁
	 */
	@Scheduled(cron = "0 */2 * * * *")
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
			/*subMap.forEach((key,value)->{
				List<BilibliVideoInfo>  infos = bilibiliSearchApiClientPk.searchUpVideo(Long.parseLong(key));
				
				if(infos!=null&&!infos.isEmpty()) {
					logger.info(infos.toString());
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
			});*/
			
			//accen@20191122不再使用各个分散的API，统一使用动态
			
			subMap.forEach((upid,subTarget)->{
				Map<String, Object> dynHises = apiVc.dynamic(null, upid, 0);
				if((int)dynHises.get("code")==0) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> cards = (List<Map<String, Object>>) ((Map<String,Object>)dynHises.get("data")).get("cards");
					if(cards!=null&&!cards.isEmpty()) {
						for(Map<String,Object> card:cards) {
							@SuppressWarnings("unchecked")
							Map<String, Object> desc = (Map<String, Object>) card.get("desc");
							long timestamp = (long) ((double)desc.get("timestamp"))*1000;
							if(curTimestamp-timestamp<=2*60*1000) {
								String cardContent = parseDynamicCard(card);//更新的内容
								subTarget.forEach((targetId,subscribers)->{
									StringBuffer msg = new StringBuffer();
									String ats = subscribers.stream()
											.map(subscri->CQUtil.at(subscri.getSubscriber()))
											.collect(Collectors.joining(""));
									msg.append(ats);
									msg.append(" 您订阅的B站up主【")
										.append(subscribers.get(0).getSubObjMark())
										.append("】")
										.append(cardContent)
										.append("\n")
										.append(" 快去看看吧喵~");
									logger.debug(msg.toString());
									taskManager.addGeneralTaskQuick(botId, "group", targetId, msg.toString());
								});
								
								
							}else {
								//如果更新的都在2分钟之后，后面直接跳出
								break;
							}
							
						}
						
					}
				}
			});
			
			
		}
		
	}
	private static final Gson gson = new GsonBuilder()
			.setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
	/**
	 * 解析B站动态发布API 的内容
	 * @param card
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String parseDynamicCard(Map<String,Object> card) {
		
		Map<String, Object> desc = (Map<String, Object>) card.get("desc");
		int type = (int) ((double)desc.get("type"));
		String cardJson = StringUtil.unicodeToString(((String) card.get("card"))).replaceAll("\n", " ");
//		logger.info(card.get("card").toString());
		logger.debug(cardJson);
		Map<String, Object> cardMap = gson.fromJson(cardJson, Map.class);
		return parseDynamicCardMap(cardMap,type,0,null);
		
	}
	/**
	 * 对card内容的解析，暂时可以解析1-转发动态/2-普通动态/8-视频/64-专栏
	 * @param cardMap
	 * @param type 动态类型 1 2 8 64
	 * @param depth 深度，当动态为转发动态时，会叠加一次深度，从0开始记，一般认为所关注的人发布的动态深度为0
	 * @param nextUname 当当前动态不是顶层动态时（是被转发的），也就是depth>0时，需要提供这条动态的发布者。
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String parseDynamicCardMap(Map<String,Object> cardMap,int type,int depth,String nextUname) {
		StringBuffer msgBuf = new StringBuffer();
		if(type==1) {
			//转发动态
			String content = (String) ((Map<String,Object>)cardMap.get("item")).get("content");
			if(depth==0) {
				msgBuf.append("转发了动态：\n");
			}
			msgBuf.append(content)
					.append("//");
			if(depth>0) {
				//如果深度>=1，也就是我关注的这个人不是第一发布者，则带上当前发布者名
				msgBuf.append(nextUname)
						.append("：");
			}
			String originUname = (String) ((Map<String,Object>) ((Map<String,Object>)cardMap.get("origin_user")).get("info")).get("uname");
			//转发的动态拿到原始的动态
			int originType = (int) ((double)((Map<String,Object>)cardMap.get("item")).get("orig_type"));
			String orginJson = (String)cardMap.get("origin");
			return msgBuf.append(parseDynamicCardMap(gson.fromJson(orginJson, Map.class),originType, ++depth,originUname)).toString();
		}else if(type>>1==1) {
			String description = (String) ((Map<String,Object>)cardMap.get("item")).get("description");
			description = description.length()>83?(description.substring(0, 80)+"..."):description;
			List<Map<String,Object>> pics = (List<Map<String, Object>>) ((Map<String,Object>)cardMap.get("item")).get("pictures");
			//普通动态
			if(depth>0) {
				//如果深度>=1，也就是我关注的这个人不是第一发布者，则带上当前发布者名
				msgBuf.append(nextUname)
						.append("：");
			}else if(depth==0) {
				msgBuf.append("发布了新动态：\n");
			}
			msgBuf.append(description)
					.append((pics!=null&&!pics.isEmpty())?CQUtil.imageUrl((String) pics.get(0).get("img_src")):"");
			return msgBuf.toString();
		}else if(type>>2==1){
			//也是普通动态
			String content = (String) ((Map<String,Object>)cardMap.get("item")).get("content");
			if(depth>0) {
				//如果深度>=1，也就是我关注的这个人不是第一发布者，则带上当前发布者名
				msgBuf.append(nextUname)
						.append("：");
			}else if(depth==0) {
				msgBuf.append("发布了新动态：\n");
			}
			msgBuf.append(content);
			return msgBuf.toString();
		}else if(type>>3==1) {
			//视频
			if(depth>0) {
				//如果深度>=1，也就是我关注的这个人不是第一发布者，则带上当前发布者名
				msgBuf.append(nextUname)
						.append("：");
			}else if(depth==0) {
				msgBuf.append("更新了一个视频：\n");
			}
			long aid = ((Double)cardMap.get("aid")).longValue();
			String description = (String) cardMap.get("desc");
			description = description.length()>83?(description.substring(0, 80)+"..."):description;
			String pic = (String) cardMap.get("pic");
			msgBuf.append(description)
						.append("[")
						.append("https://www.bilibili.com/video/av")
						.append(aid)
						.append("]")
						.append(StringUtils.isEmpty(pic)?"":CQUtil.imageUrl(pic));
			return msgBuf.toString();
		}else if(type>>6==1) {
			//专栏
			if(depth>0) {
				//如果深度>=1，也就是我关注的这个人不是第一发布者，则带上当前发布者名
				msgBuf.append(nextUname)
						.append("：");
			}else if(depth==0) {
				msgBuf.append("发布了一则专栏：\n");
			}
			long cvId = ((Double)cardMap.get("id")).longValue();
			String title = (String) cardMap.get("title");
			String summary = (String) cardMap.get("summary");
			String description = title.trim()+"\n"+summary;
			String description1 = description.length()>83?(description.substring(0, 80)+"..."):description;
			List<String> pics = (List<String>) cardMap.get("image_urls");
			msgBuf.append(description1)
						.append("[")
						.append("https://www.bilibili.com/read/cv")
						.append(cvId)
						.append("]")
						.append((pics!=null&&!pics.isEmpty())?CQUtil.imageUrl( pics.get(0)):"");
			return msgBuf.toString();
		}else if(type>>8==1){
			//音乐
			if(depth>0) {
				//如果深度>=1，也就是我关注的这个人不是第一发布者，则带上当前发布者名
				msgBuf.append(nextUname)
						.append("：");
			}else if(depth==0) {
				msgBuf.append("发布了一首音乐：\n");
			}
			long mId = ((Double)cardMap.get("id")).longValue();
			String title = (String) cardMap.get("title");
			String intro = (String) cardMap.get("intro");
			String description = title.trim()+"\n"+intro.trim();
			String description1 = description.length()>83?(description.substring(0, 80)+"..."):description;
			String cover = (String) cardMap.get("cover");
			msgBuf.append(description1)
					.append("[")
					.append("https://www.bilibili.com/audio/au")
					.append(mId)
					.append("]")
					.append(StringUtils.isEmpty(cover)?"":CQUtil.at(cover));
			return msgBuf.toString();
		}else {
			return msgBuf.toString();
		}
		
	}
}
