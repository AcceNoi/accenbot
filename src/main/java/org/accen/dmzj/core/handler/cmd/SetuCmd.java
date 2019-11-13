package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.callbacker.CallbackListener;
import org.accen.dmzj.core.handler.callbacker.CallbackManager;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.task.api.LoliconApiClientPk;
import org.accen.dmzj.core.timer.CacheMap;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CfgResourceMapper;
import org.accen.dmzj.web.vo.CfgResource;
import org.accen.dmzj.web.vo.Qmessage;
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
	private LoliconApiClientPk loliconApiClientPk;
	@Autowired
	private CfgResourceMapper cfgResourceMapper;
	@Autowired
	private FuncSwitchUtil funcSwitchUtil;

	@Autowired
	private TaskManager taskManager;
	
	@Value("${coolq.setu.coin.decrease:-3}")
	private int decrease ;
	@Autowired
	private CheckinCmd checkinCmd;
	
	private static final Pattern pattern = Pattern.compile("^随机(色图|瑟图|涩图)$");
	private static final Pattern collectPattern = Pattern.compile("^随机收藏$");
	
	
	private CacheMap<String, String> waitingCollect = new CacheMap<String, String>();
	
//	private Boolean locked = false;//未知原因使得此功能被滥用则回系统崩溃，可能是coolq pro接收数据的超时设置问题，这里为了防止滥用，同一时间段只接收一个请求。
	
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
						
						int curCoin = checkinCmd.getCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
						if(curCoin<0) {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未绑定哦，暂时无法查看涩图，发送[绑定]即可绑定个人信息喵~");
							return task;
						}else if(curCoin-Math.abs(decrease)<0) {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 金币不够啦，没钱就别看涩图喵~");
							return task;
						}else {
							String imageUrl = loliconApiClientPk.setu();
							if(imageUrl!=null&&funcSwitchUtil.isImgReviewPass(imageUrl, qmessage.getMessageType(), qmessage.getGroupId())) {
								int factDecrease = Math.abs(decrease);
								if(decrease<0) {
									factDecrease = RandomUtil.randomInt(factDecrease+1);
								}
								int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -factDecrease);
								task.setMessage(CQUtil.imageUrl(imageUrl)+"\n"+CQUtil.at(qmessage.getUserId())+"无尽的欲望消耗了您"+factDecrease+"枚金币~");
								//添加到收藏监听
								waitingCollect.put(qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId(), imageUrl,15000);
								callbackManager.addCallbackListener(this, qmessage);
								
//								locked = false;
								return task;
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
							task.setMessage(CQUtil.imageUrl(collect.getCfgResource()));
						}
						return task;
					}
//					locked = false;
					return null;
//				}
				
//			}
//		}
		
		
	}

	private static final String COLLECT_PTRN = "收藏";
	@Override
	public boolean listen(Qmessage originQmessage, Qmessage qmessage, String selfQnum) {
		if(originQmessage!=qmessage&&originQmessage.getGroupId().equals(qmessage.getGroupId())&&originQmessage.getUserId().equals(qmessage.getUserId())) {
			if(COLLECT_PTRN.equals(qmessage.getMessage().trim())&&waitingCollect.containsKey(qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId())) {
				String imgResource = waitingCollect.get(qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId());
				CfgResource rsc = new CfgResource();
				rsc.setCfgKey("collect"+"_"+qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId());
				rsc.setCfgResource(imgResource);
				rsc.setResourceType("image");
				cfgResourceMapper.insert(rsc);
				taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), CQUtil.at(qmessage.getUserId())+" 收藏成功喵，发送[随机收藏]就有机会随机到这张图喵！");
			}
			return true;
		}
		return false;
	}

}
