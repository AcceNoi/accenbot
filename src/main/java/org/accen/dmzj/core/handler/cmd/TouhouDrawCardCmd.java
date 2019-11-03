package org.accen.dmzj.core.handler.cmd;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.handler.callbacker.CallbackListener;
import org.accen.dmzj.core.handler.callbacker.CallbackManager;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomMeta;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CmdSvCardMapper;
import org.accen.dmzj.web.vo.CmdMyCard;
import org.accen.dmzj.web.vo.CmdSvCard;
import org.accen.dmzj.web.vo.CmdSvPk;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TouhouDrawCardCmd implements CmdAdapter,CallbackListener {

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String example() {
		// TODO Auto-generated method stub
		return null;
	}
	@Autowired
	private CmdSvCardMapper cmdSvCardMapper;

	@Autowired
	private CallbackManager callbackManager;
	
	@Autowired
	private TaskManager taskManager;
	
	@Autowired
	private CheckinCmd checkinCmd;
	
	private String[] careers = new String[] {"[N]","[R]","[SR]","[SSR]","[UR]"};
	
	/**
	 * 翻牌所用的map，targetType_targetId_userId_Touhou->(随机串->上次随机出来待选择的card)
	 */
	private static Map<String, Map<String,CmdSvCard>> pokerMap = new HashMap<String, Map<String,CmdSvCard>>();
	/**
	 * 翻牌的张数
	 */
	@Value("${coolq.touhou.pokersize:3}")
	private int pokerSize;
	
	@Value("${coolq.touhou.coin.descrease:10}")
	private int decrease = 10;//抽取金币消耗
	
	private static final Pattern drawPattern = Pattern.compile("^东方(十连|单抽|翻牌)$");
	private static final Pattern myPattern = Pattern.compile("^我的图鉴$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher drawMatcher = drawPattern.matcher(message);
		Matcher myMatcher = myPattern.matcher(message);
		if(drawMatcher.matches()) {
			
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			String drawType = drawMatcher.group(1);
			//1.检查是否含有此卡包
			CmdSvPk pk = cmdSvCardMapper.selectPkByName("东方Project", "TH", "東方Project", "Touhou Project");
			if(pk==null) {
				task.setMessage("东方Project抽卡暂未初始化，请联系[クロノス/Accen]进行初始化喵~");
				return task;
			}
			List<CmdSvCard> cards = cmdSvCardMapper.findCardByPk(pk.getId());
			//转换成随机抽取对象
			List<RandomMeta<CmdSvCard>> cardsO = cards.stream()
					.map(card->new RandomMeta<CmdSvCard>(card,(int)(card.getProbability()*10000)))
					.collect(Collectors.toList());
			//2.三种不同抽取方式
			//当前金币
			int curCoin = checkinCmd.getCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			if(curCoin<0) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未绑定哦，暂时无法抽卡，发送[绑定]即可绑定个人信息喵~");
				return task;
			}
			
			if("单抽".equals(drawType)) {
				//2.1单抽
				
				if(curCoin<decrease) {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您库存金币不够了哦，暂无法抽卡喵~");
					return task;
				}
				
				List<CmdSvCard> rss = RandomUtil.randomObjWeight(cardsO, 1);
				if(rss!=null&&!rss.isEmpty()) {
					CmdSvCard rs = rss.get(0);
					CmdMyCard mycard = cmdSvCardMapper.selectMyCardBySelf(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getGroupId(), rs.getId());
					if(mycard!=null) {
						//已有这张卡，更新
						cmdSvCardMapper.updateMyCardTime(mycard.getId(), new Date());
					}else {
						//没有，则插入
						mycard = new CmdMyCard();
						mycard.setPkId(rs.getPkId());mycard.setCardId(rs.getId());mycard.setTargetType(qmessage.getMessageType());mycard.setTargetId(qmessage.getGroupId());
						mycard.setUserId(qmessage.getUserId());mycard.setCreateTime(new Date());mycard.setIsDeleted((short) 0);
						cmdSvCardMapper.insertMyCard(mycard);
					}
					//金币消耗
					int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -decrease);
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 抽卡成功喵！\n"+careers[rs.getCardRarity()-1]+" "+rs.getCardName()+"\n本次抽卡消耗金币："+decrease+"，剩余："+newCoin);
					return task;
				}else {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 抽卡失败~但我不会道歉的哦");
					return task;
				}
			}else if("十连".equals(drawType)) {
				//2.2十连
				if(curCoin<decrease*10) {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您库存金币不够了哦，暂无法抽卡喵~");
					return task;
				}
				
				List<CmdSvCard> rss = RandomUtil.randomObjWeight(cardsO, 10);
				if(rss!=null&&!rss.isEmpty()&&rss.size()==10) {
					StringBuffer msgBuff = new StringBuffer(CQUtil.at(qmessage.getUserId()));
					msgBuff.append("")
							.append("抽卡成功喵！\n");
					for(int index = 0;index<rss.size();index++) {
						CmdSvCard rs = rss.get(index);
						
						msgBuff.append(index+1)
								.append(". ")
								.append(careers[rs.getCardRarity()-1])
								.append(" ")
								.append(rs.getCardName())
								.append("\n");
						
						CmdMyCard mycard = cmdSvCardMapper.selectMyCardBySelf(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getGroupId(), rs.getId());
						if(mycard!=null) {
							//已有这张卡，更新
							cmdSvCardMapper.updateMyCardTime(mycard.getId(), new Date());
						}else {
							//没有，则插入
							mycard = new CmdMyCard();
							mycard.setPkId(rs.getPkId());mycard.setCardId(rs.getId());mycard.setTargetType(qmessage.getMessageType());mycard.setTargetId(qmessage.getGroupId());
							mycard.setUserId(qmessage.getUserId());mycard.setCreateTime(new Date());mycard.setIsDeleted((short) 0);
							cmdSvCardMapper.insertMyCard(mycard);
						}
					}
					//金币消耗
					int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -decrease*10);
					//task.setMessage(CQUtil.at(qmessage.getUserId())+" 抽卡成功！\n"+careers[rs.getCardRarity()-1]+" "+rs.getCardName()+"\n本次抽卡消耗金币："+decrease+"，剩余："+newCoin);
					msgBuff.append("本次抽卡消耗金币："+decrease+"，剩余："+newCoin);
					return task;
				}else {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 抽卡失败~但我不会道歉的哦");
					return task;
				}
				
			}else if("翻牌".equals(drawType)) {
				//先随机出pokersize个card
				List<CmdSvCard> rss = RandomUtil.randomObjWeight(cardsO, pokerSize);
				if(rss!=null&&!rss.isEmpty()&&rss.size()==pokerSize) {
					//即使当前有poker任务，也直接覆盖掉
					String key = qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId()+"_Touhou";
					Map<String, CmdSvCard> myPoker = new HashMap<String, CmdSvCard>(pokerSize);
					//再随机出等量的字符串，用于唯一标识card
					String[] pokerKey = RandomUtil.randZhNumEx(2, pokerSize);
					//再关联这两者
					
					if(pokerKey!=null) {
						StringBuffer msgBuff = new StringBuffer(CQUtil.at(qmessage.getUserId()));
						msgBuff.append(" 发送@Bot+下面任意字符串进行翻牌：\n");
						
						for(int index = 0;index<pokerSize;index++) {
							myPoker.put(pokerKey[index], rss.get(index));
							msgBuff.append(pokerKey[index])
									.append("  ");
						}
						pokerMap.put(key, myPoker);

						//添加回调任务听取用户的选择
						callbackManager.addCallbackListener(this,qmessage);
						
						task.setMessage(msgBuff.toString());
						return task;
					}
					
					
				}else {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 抽卡失败~但我不会道歉的哦");
					return task;
				}
			}
		}else if(myMatcher.matches()) {
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			//1.检查是否含有此卡包
			CmdSvPk pk = cmdSvCardMapper.selectPkByName("东方Project", "TH", "東方Project", "Touhou Project");
			if(pk==null) {
				task.setMessage("东方Project抽卡暂未初始化，请联系[クロノス/Accen]进行初始化喵~");
				return task;
			}
			//查询我的图鉴
			List<CmdSvCard>  cards = cmdSvCardMapper.findCardMyCardByPkId(pk.getId(), qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			StringBuffer msgBuff = new StringBuffer(CQUtil.at(qmessage.getUserId()));
			msgBuff.append(" 东方Project图鉴为：\n");
			for(int index=0;index<cards.size();index++) {
				msgBuff.append(index+1)
						.append(" ")
						.append(careers[cards.get(index).getCardRarity()-1])
						.append(" ")
						.append(cards.get(index).getCardName())
						.append("\n");
			}
			task.setMessage(msgBuff.toString());
			return task;
		}
		return null;
	}

	@Override
	public boolean listen(Qmessage originQmessage, Qmessage qmessage, String selfQnum) {
		if(originQmessage!=qmessage&&originQmessage.getGroupId().equals(qmessage.getGroupId())&&originQmessage.getUserId().equals(qmessage.getUserId())) {
			String choose = CQUtil.subAtAfter(qmessage.getMessage().trim(), selfQnum);
			if(pokerMap.containsKey(qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId()+"_Touhou")&&choose!=null) {
				Map<String, CmdSvCard> poker = pokerMap.remove(qmessage.getMessageType()+"_"+qmessage.getGroupId()+"_"+qmessage.getUserId()+"_Touhou");
				if(poker.containsKey(choose.trim())) {
					//正确选择
					CmdSvCard card = poker.get(choose.trim());
					CmdMyCard mycard = cmdSvCardMapper.selectMyCardBySelf(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getGroupId(), card.getId());
					if(mycard!=null) {
						//已有这张卡，更新
						cmdSvCardMapper.updateMyCardTime(mycard.getId(), new Date());
					}else {
						//没有，则插入
						mycard = new CmdMyCard();
						mycard.setPkId(card.getPkId());mycard.setCardId(card.getId());mycard.setTargetType(qmessage.getMessageType());mycard.setTargetId(qmessage.getGroupId());
						mycard.setUserId(qmessage.getUserId());mycard.setCreateTime(new Date());mycard.setIsDeleted((short) 0);
						cmdSvCardMapper.insertMyCard(mycard);
					}
					//金币消耗
					int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -decrease);
					
					StringBuffer msgBuff = new StringBuffer(CQUtil.at(qmessage.getUserId()));
					msgBuff.append(" 本次翻到了")
							.append(choose.trim())
							.append(" ")
							.append(careers[card.getCardRarity()-1])
							.append(" ")
							.append(card.getCardName())
							.append("。其他卡片为：\n");
					String others = poker.keySet().stream().filter(pokerKey->!pokerKey.equals(choose.trim())).map(pokerKey->{
										CmdSvCard curCard = poker.get(pokerKey);
										return pokerKey+" "+careers[curCard.getCardRarity()-1]+" "+curCard.getCardName();
									}).collect(Collectors.joining("\n"));
					msgBuff.append(others).append("\n本次抽卡消耗金币："+decrease+"，剩余："+newCoin);
					taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), msgBuff.toString());
					return true;
				}else {
					//选择有误
					taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), CQUtil.at(qmessage.getUserId())+" 选择有误喵~");
					return false;
				}
			}
		}
		return false;
	}

}
