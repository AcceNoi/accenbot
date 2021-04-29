package org.accen.dmzj.core.handler.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.callbacker.CallbackListener;
import org.accen.dmzj.core.handler.callbacker.CallbackManager;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.api.LoliconApiClient;
import org.accen.dmzj.core.api.pixivc.PixivicApiClient;
import org.accen.dmzj.core.timer.CacheMap;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.util.RandomMeta;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.util.setu.SetuCatcher;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@FuncSwitch("cmd_setu")
@Component
@Transactional
public class SetuCmd implements CmdAdapter,CallbackListener {

	@Override
	public String describe() {
		return "随机获取网上的一张p站图";
	}

	@Override
	public String example() {
		return "随机涩图";
	}
	@Autowired
	private CallbackManager callbackManager;
	@Autowired
	private LoliconApiClient loliconApiClient;
	@Autowired
	private CfgResourceMapper cfgResourceMapper;
	@Autowired
	private FuncSwitchUtil funcSwitchUtil;
	@Autowired
	private PixivicApiClient pixivicApiClient;
	@Autowired
	private TaskManager taskManager;
	
	@Value("${coolq.setu.coin.decrease:-3}")
	private int decrease ;
	@Autowired
	private CheckinCmd checkinCmd;
	@Autowired
	private SetuCatcher setuCatcher;
	
	private static final Pattern pattern = Pattern.compile("^(随机|来点|发点)(色图|瑟图|涩图)$");
	private static final Pattern collectPattern = Pattern.compile("^随机收藏$");
	private static final Pattern searchPattern = Pattern.compile("^(P|p)站搜图(.+)");
	private static final Pattern searchPattern2 = Pattern.compile("^(随机|来点|发点|搞点)(.+)");
	private static final Pattern getPattern = Pattern.compile("^(P|p)站找图((\\d+)?(-){0,1}(\\d*))");
	private static final String proxyPreffix = "https://i.pixiv.cat";
	
	//待收藏的map  type_group-> randomZh -> imageUrl
	private Map<String, CacheMap<String, String>> waitingCollect = new HashMap<String, CacheMap<String,String>>();
	
//	private Boolean locked = false;//未知原因使得此功能被滥用则回系统崩溃，可能是coolq pro接收数据的超时设置问题，这里为了防止滥用，同一时间段只接收一个请求。
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
//		if(locked) {
//			return null;
//		}else {
//			synchronized (locked) {
//				
//				if(locked) {
//					return null;
//				}else {
//					locked = true;
		//金币检验
		
					String message = qmessage.getMessage().trim();
					Matcher matcher = pattern.matcher(message);
					if(matcher.matches()) {
						GeneralTask task =  new GeneralTask();
						
						task.setSelfQnum(selfQnum);
						task.setType(qmessage.getMessageType());
						task.setTargetId(qmessage.getGroupId());
						
						if(RandomUtil.randomPass(0)) {
							//80%从api取
							int curCoin = checkinCmd.getCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
							if(curCoin<0) {
								task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未绑定哦，暂时无法查看涩图，发送[绑定]即可绑定个人信息喵~");
								return task;
							}else if(curCoin-Math.abs(decrease)<0) {
								task.setMessage(CQUtil.at(qmessage.getUserId())+" 金币不够啦，没钱就别看涩图喵~");
								return task;
							}else {
//								String imageUrl = loliconApiClientPk.setu();
								String imageUrl = (String) ((List<Map<String, Object>>)(loliconApiClient.setu().get("data"))).get(0).get("url");
								if(imageUrl!=null&&funcSwitchUtil.isImgReviewPass(imageUrl, qmessage.getMessageType(), qmessage.getGroupId())) {
									int factDecrease = Math.abs(decrease);
									if(decrease<0) {
										factDecrease = RandomUtil.randomInt(factDecrease+1);
									}
									//消耗金币
									int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -factDecrease);
									
									//添加到收藏监听
									if(!waitingCollect.containsKey(qmessage.getMessageType()+"_"+qmessage.getGroupId())) {
										waitingCollect.put(qmessage.getMessageType()+"_"+qmessage.getGroupId(), new CacheMap<String, String>());
									}
									//当前群所等待收藏的图片
									CacheMap<String,String> curGroupWaitingCollectImags = waitingCollect.get(qmessage.getMessageType()+"_"+qmessage.getGroupId());
									//随机一个不在等待map中的随机数字
									String rdZh = RandomUtil.randZhNumExclude(2, curGroupWaitingCollectImags.keySet());
									curGroupWaitingCollectImags.put(rdZh, imageUrl,60000);
									//把收藏的提示放上去
									task.setMessage(CQUtil.imageUrl(imageUrl)+"\n"+CQUtil.at(qmessage.getUserId())+"无尽的欲望消耗了您"+factDecrease+"枚金币~收藏此涩图请发送[收藏"+rdZh+"]喵~");
									callbackManager.addResidentListener(this);
									
//									locked = false;
									return task;
								}
							}
						}else {
							
							/*File localSetu = setuCatcher.randomSetu();
							byte[] buffer = new byte[(int) localSetu.length()];
							try(InputStream is = new FileInputStream(localSetu)){							;
								is.read(buffer);
								task.setMessage(CQUtil.imageBs64(new Base64().encodeToString(buffer))
										+"by "+localSetu.getName().substring(0, localSetu.getName().lastIndexOf(SetuCatcher.SETU_SUFFIX)));
								return task;
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} 
							return null;*/
							File[] localSetus = setuCatcher.randomSetu(RandomUtil.randomInt(4)+1);
							final Base64 base64 = new Base64();
							String msg = Arrays.stream(localSetus).map(localSetu->{
								byte[] buffer = new byte[(int) localSetu.length()];
								try(InputStream is = new FileInputStream(localSetu)){							;
									is.read(buffer);
									return CQUtil.imageBs64(base64.encodeToString(buffer));
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								return "";
							}).collect(Collectors.joining(""));
							if(msg!=null&&!msg.isBlank()) {
								task.setMessage(msg);
								return task;
							}else {
								return null;
							}
							
						}
						
						
						
						
						
						
						
					}
					Matcher collectMatcher = collectPattern.matcher(message);
					if(collectMatcher.matches()) {
						GeneralTask task =  new GeneralTask();
						
						task.setSelfQnum(selfQnum);
						task.setType(qmessage.getMessageType());
						task.setTargetId(qmessage.getGroupId());
						CfgResource collect = cfgResourceMapper.selectRandomCollectByKey("collect"+"_"+qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId());
						if(collect==null) {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未收藏过图片喵~");
						}else {
							task.setMessage(CQUtil.image(collect.getCfgResource()));
						}
						return task;
					}
					Matcher searchMatcher = searchPattern.matcher(message);
					Matcher searchMatcher2 = searchPattern2.matcher(message);
					if(searchMatcher.matches()||searchMatcher2.matches()) {
						String keyword = searchMatcher.matches()?searchMatcher.group(2).trim():searchMatcher2.group(2).trim();
						GeneralTask task =  new GeneralTask();
						
						task.setSelfQnum(selfQnum);
						task.setType(qmessage.getMessageType());
						task.setTargetId(qmessage.getGroupId());
						Map<String,Object> rs = pixivicApiClient.search(keyword, 1);
//						int total = (int)((double)((Map<String, Object>)rs.get("data")).get("total"));
						if(!rs.containsKey("data")) {
							//重试一次
							rs = pixivicApiClient.search(keyword, 1);
						}
						if(rs.containsKey("data")) {
							int total = ((List<Map<String, Object>>)rs.get("data")).size();
							if(total>0) {
								total = total<20?total:20;//只取前20个
								int rdIndex = RandomUtil.randomInt(total);
								Map<String,Object> rdRs = ((List<Map<String, Object>>)rs.get("data")).get(rdIndex);
								String largeImgUrl = (String)((List<Map<String,Object>>)rdRs.get("imageUrls")).get(0).get("original");
								long pid = (long)rdRs.get("id");
								String title = (String)rdRs.get("title");
								String author = (String) ((Map<String,Object>)rdRs.get("artistPreView")).get("name");
								String[] fmtPixivImgUrl = StringUtil.formatUrl(largeImgUrl);
								String proxyLargeImgUrl = proxyPreffix+fmtPixivImgUrl[3];
								/*try {
									InputStream is = pixivcatApiClient.pixivImage(fmtPixivImgUrl[3]).body().asInputStream();
									String bs64Img = StringUtil.is2Base64(is);*/
									//添加到收藏监听
									if(!waitingCollect.containsKey(qmessage.getMessageType()+"_"+qmessage.getGroupId())) {
										waitingCollect.put(qmessage.getMessageType()+"_"+qmessage.getGroupId(), new CacheMap<String, String>());
									}
									//当前群所等待收藏的图片
									CacheMap<String,String> curGroupWaitingCollectImags = waitingCollect.get(qmessage.getMessageType()+"_"+qmessage.getGroupId());
									//随机一个不在等待map中的随机数字
									String rdZh = RandomUtil.randZhNumExclude(2, curGroupWaitingCollectImags.keySet());
									curGroupWaitingCollectImags.put(rdZh, proxyLargeImgUrl,60000);
									
									
									callbackManager.addResidentListener(this);
									
									//搜索建议
									String[] sugArr = suggestions(keyword);
									
									//图片使用base64
									
									task.setMessage(CQUtil.imageUrl(proxyLargeImgUrl,true)+CQUtil.at(qmessage.getUserId())+"\n标题："+title+"，PID："+pid+"，Author："+author+"。收藏此图片请发送[收藏"+rdZh+"]喵~"+(sugArr==null?"":("\n更多搜索建议："+String.join("、",sugArr))));
//									locked = false;
									return task;
								/*} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
								
								
							}else {
								//未检索到
							}
						}
						
						
						
					}
					Matcher getMatcher = getPattern.matcher(message);
					if(getMatcher.matches()) {
						String pid = getMatcher.group(2);
						GeneralTask task =  new GeneralTask();
						
						task.setSelfQnum(selfQnum);
						task.setType(qmessage.getMessageType());
						task.setTargetId(qmessage.getGroupId());
						task.setMessage(CQUtil.imageUrl("https://pixiv.cat/"+pid+".jpg",true));
						return task;
					}
//					locked = false;
					return null;
//				}
				
//			}
//		}
		
		
	}

	private static final Pattern COLLECT_PTRN = Pattern.compile("^收藏(.+)");
	@Override
	public boolean listen(Qmessage originQmessage, Qmessage qmessage, String selfQnum) {
		Matcher clctMatcher = COLLECT_PTRN.matcher(qmessage.getMessage().trim());
		if(clctMatcher.matches()&&waitingCollect.containsKey(qmessage.getMessageType()+"_"+qmessage.getGroupId())) {
			String rdZh = clctMatcher.group(1).trim();
			if(waitingCollect.get(qmessage.getMessageType()+"_"+qmessage.getGroupId()).containsKey(rdZh)) {
				String imgResource = waitingCollect.get(qmessage.getMessageType()+"_"+qmessage.getGroupId()).get(rdZh);
				String key = "collect"+"_"+qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId();
				if(cfgResourceMapper.countByKeyAndResource(key, imgResource)>1) {
					//收藏过了
					taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), CQUtil.at(qmessage.getUserId())+" 您已经收藏过此图片喵，发送[随机收藏]就有机会随机到这张图喵！");
				}else {
					CfgResource rsc = new CfgResource();
					rsc.setCfgKey(key);
					rsc.setCfgResource(imgResource);
					rsc.setResourceType("image");
					cfgResourceMapper.insert(rsc);
					taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), CQUtil.at(qmessage.getUserId())+" 收藏成功喵，发送[随机收藏]就有机会随机到这张图喵！");
				}
				
			}
			
		}
		//这里返回true or false没有意义，但是为了以后考虑，建议返回false
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private String[] suggestions(String keyword) {
		Map<String,Object> sug = pixivicApiClient.suggestions(keyword);
		if(sug.containsKey("data")) {
			List<Map<String,Object>> sugs = (List<Map<String, Object>>) sug.get("data");
			if(sugs.size()>0) {
				//一般都是16个，随机取5个好了
				int total = sugs.size()<5?sugs.size():5;
				List<RandomMeta<Map<String,Object>>> tdMetas = sugs.stream().map(su->new RandomMeta<Map<String,Object>>(su, 1)).collect(Collectors.toList());
				List<Map<String, Object>> rdRses = RandomUtil.randomObjWeight(tdMetas, total);//随机出的结果
				return rdRses.stream().map(rsRs->(String)rsRs.get("keyword")).toArray(String[]::new);
			}else {
				return null;
			}
			
			
		}else {
			return null;
		}
	}

}
