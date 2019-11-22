package org.accen.dmzj.core.handler.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomMeta;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CmdSvCardMapper;
import org.accen.dmzj.web.vo.CmdMyCard;
import org.accen.dmzj.web.vo.CmdSvCard;
import org.accen.dmzj.web.vo.CmdSvPk;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
@FuncSwitch("cmd_sv_draw")
@Transactional
@Component
public class SvDrawCardCmd implements CmdAdapter {
	@Autowired
	private CheckinCmd checkinCmd;
	@Autowired
	private CmdSvCardMapper cmdSvCardMapper;
	@Override
	public String describe() {
		return "影之诗卡包抽卡";
	}

	@Override
	public String example() {
		return "影之诗抽卡森罗咆哮";
	}
	@Value("${coolq.sv.drawcount:8}")
	private int drawCount = 8;//单次抽取张数
	@Value("${coolq.sv.coin.descrease:8}")
	private int decrease = 10;//抽取金币消耗
	
	private String[] careers = new String[] {"铜","银","金","虹","异画"};

	private int[] returnCoin = new int[] {0,1,3,8,16};
	
	private final static Pattern pattern = Pattern.compile("^影之诗(十连)?抽卡(.*)");
	private static final Pattern myPattern = Pattern.compile("^我的影之诗图鉴(\\d*)$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		Matcher myMatcher = myPattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			//是否10连 TODO 后续可能会有20连，但是需要考虑优化随机算法了
			int type = StringUtils.isEmpty(matcher.group(1))?1:10;
			
			//金币检验
			int curCoin = checkinCmd.getCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			if(curCoin<0) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还未绑定哦，暂时无法抽卡，发送[绑定]即可绑定个人信息喵~");
			}else if(curCoin-decrease*type<0) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您库存金币不够了哦，暂无法抽卡喵~");
			}else {
				String pkName = matcher.group(2);
				CmdSvPk pk = null;
				if(StringUtils.isEmpty(pkName)) {
					//如果没有写卡包名，则取当前最新的
					pk = cmdSvCardMapper.getTopPk();
				}else {
					pk = cmdSvCardMapper.selectPkByName(pkName, pkName, pkName, pkName);
				}
				
				if(pk!=null) {
					List<CmdSvCard> cards = cmdSvCardMapper.findCardByPk(pk.getId());
					//抽取
					List<RandomMeta<CmdSvCard>> cardsO = cards.stream()
							.map(card->new RandomMeta<CmdSvCard>(card,(int)(card.getProbability()*10000)))
							.collect(Collectors.toList());
					//赠送的卡包
					int giftCount = type/10;
					List<CmdSvCard> rs = RandomUtil.randomObjWeight(cardsO, drawCount*(type+giftCount));
					
					if(rs!=null&&rs.size()==drawCount*(type+giftCount)) {
						//消耗金币
						int newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), -decrease*type);
						//抽取成功，格式化消息
						StringBuffer msgBuf = new StringBuffer();
//						msgBuf.append("抽卡成功喵~消耗金币"+decrease+"，剩余"+newCoin+"。本次抽取卡包为["+pk.getPkName()+"]:\n");
						msgBuf.append("抽卡成功喵~消耗金币"+(decrease*type)+"。抽取卡包为["+pk.getPkName()+"]:\n");
						
//						boolean legend = false;//虹卡
//						boolean diffPaint = false;//异画
						/*int bronze = 0;
						int silver = 0;
						int gold = 0;
						int legend = 0;
						int diffPaint = 0;*/
						int[] cdrrts = new int[] {0,0,0,0,0};
						
						//单抽不会用到，因为会打乱顺序
						Map<CmdSvCard, Integer> cardCountMap = new HashMap<CmdSvCard, Integer>();
						
						for (int i = 0; i <rs.size(); i++) {
//							legend |= rs.get(i).getCardRarity()==4;
//							diffPaint |= rs.get(i).getCardRarity()==5;
							
							/*switch (rs.get(i).getCardRarity()) {
							case 1:
								bronze++;
								break;
							case 2:
								silver++;
								break;
							case 3:
								gold++;
								break;
							case 4:
								legend++;
								break;
							case 5:
								diffPaint++;
							default:
								break;
							}*/
							if(rs.get(i).getCardRarity()==4||rs.get(i).getCardRarity()==5) {
								CmdMyCard mycard = cmdSvCardMapper.selectMyCardBySelf(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), rs.get(i).getId());
								if(mycard!=null) {
									//已有这张卡，更新
									cmdSvCardMapper.updateMyCardTime(mycard.getId(), new Date());
								}else {
									//没有，则插入
									mycard = new CmdMyCard();
									mycard.setPkId(rs.get(i).getPkId());mycard.setCardId(rs.get(i).getId());mycard.setTargetType(qmessage.getMessageType());mycard.setTargetId(qmessage.getGroupId());
									mycard.setUserId(qmessage.getUserId());mycard.setCreateTime(new Date());mycard.setIsDeleted((short) 0);
									cmdSvCardMapper.insertMyCard(mycard);
								}
								
							}
							cdrrts[rs.get(i).getCardRarity()-1]++;
							
							if(type ==1 ) {
								//单抽在循环时就开始写消息了
								String desc =rs.get(i).getCareer()
										+" "
										+careers[rs.get(i).getCardRarity()-1];
										
								msgBuf.append(i+1+". ["+desc+"]"+rs.get(i).getCardName()+"\n");
							}else {
								//十连则仅仅格式化 抽到的卡组
								if(cardCountMap.containsKey(rs.get(i))) {
									cardCountMap.put(rs.get(i), cardCountMap.get(rs.get(i))+1);
								}else {
									cardCountMap.put(rs.get(i), 1);
								}
							}
							
							
						}
						
						//十连再来写消息
						if(type!=1) {
							int index = 0;
							List<CmdSvCard> cardsList = new ArrayList<CmdSvCard>(cardCountMap.keySet());
							Collections.sort(cardsList, (o1,o2)-> {
								return o2.getCardRarity()-o1.getCardRarity();
							});
							for(CmdSvCard card:cardsList) {
								String desc =card.getCareer()
										+" "
										+careers[card.getCardRarity()-1];
										
								msgBuf.append(++index+". ["+desc+"]"+card.getCardName()+" * "+cardCountMap.get(card)+"\n");
							}
						}
						
						
						if(cdrrts[4]>0) {
							msgBuf.append("抽到异画啦！欧狗吃矛！");
						}else if(cdrrts[3]>0) {
							msgBuf.append("抽到虹卡啦！恭喜这个B......站用户。");
						}
						
						//返还金币
						int returnC = cdrrts[0]*returnCoin[0]+cdrrts[1]*returnCoin[1]+cdrrts[2]*returnCoin[2]+cdrrts[3]*returnCoin[3]+cdrrts[4]*returnCoin[4];
						
						newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), returnC);
						msgBuf.append("本次获得金币："+returnC+"，库存："+newCoin);
						if(type==1&&RandomUtil.randomInt(2)==1) {
							msgBuf.append("\n")
								.append(StringUtil.SPLIT_FOOT)
								.append("Tips：现已支持其他卡包了喵~发送[影之诗抽卡扭曲次元]或[/抽卡]看看喵~");
						}else {
							msgBuf.append("\n")
							.append(StringUtil.SPLIT_FOOT)
							.append("Tips：发送[我的影之诗图鉴]看看喵~");
						}
						task.setMessage(CQUtil.at(qmessage.getUserId())+msgBuf.toString());
						
					}else {
						//抽取失败
						task.setMessage(CQUtil.at(qmessage.getUserId())+" 抽卡失败~但我不会道歉的哦");
					}
					
				}else {
					//没找到
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 未找到此卡包喵~");
				}
			}
			
			return task;
			
		}else if(myMatcher.matches()) {
			GeneralTask task =  new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			//影之诗的分页有所不一样，第一页是最新卡包的，每页展示一个卡包的
			String pageNoStr = myMatcher.group(1);
			int pageNo = StringUtils.isEmpty(pageNoStr)?1:Integer.parseInt(pageNoStr);
			int offset = pageNo-1;
			//先找到卡包
			CmdSvPk curPk = cmdSvCardMapper.selectSvPkOrder(offset);
			if(curPk==null) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 未找到此卡包喵~");
			}else {
				List<CmdSvCard> myCards = cmdSvCardMapper.findCardMyCardByPkId(curPk.getId(), qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
				StringBuffer msgBuf = new StringBuffer(CQUtil.at(qmessage.getUserId()));
				if(myCards!=null&&!myCards.isEmpty()) {
					//有抽到卡
					msgBuf.append(" 您在【"+curPk.getPkName()+"】["+pageNo+"]抽到了：\n");
					int index = 0;
					for(CmdSvCard card:myCards) {
						String desc =card.getCareer()
								+" "
								+careers[card.getCardRarity()-1];
								
						msgBuf.append(++index+". ["+desc+"]"+card.getCardName()+"\n");
					}
					
				}else {
					//还没抽到过卡
					
					msgBuf.append(" 您在【"+curPk.getPkName()+"】["+pageNo+"]抽到了...")
							.append("什么都没抽到喵~宁就是真实非酋？（笑\n");
				}
				//渲染分页
				StringUtil.drawPageFoot(cmdSvCardMapper.countSvPk(), msgBuf);
				if(RandomUtil.randomInt(2)==1) {
					msgBuf.append("\n")
							.append(StringUtil.SPLIT_FOOT)
							.append("Tips:发送我的影之诗图鉴+[分页]看看其它的卡包喵~");
				}
				task.setMessage(msgBuf.toString());
			}
			return task;
			
		}
		
		return null;
	}
	/**
	 * 获取格式化的图鉴收集程度 卡包名： 集卡数/卡总数
	 * @param targetType
	 * @param targetId
	 * @param userId
	 * @return
	 */
	public String formatMyCardCompletion(String targetType,String targetId,String userId) {
		List<CmdSvPk> pks = cmdSvCardMapper.findSvPk();
		if(pks!=null&&!pks.isEmpty()) {
			StringBuffer fmtBuf = new StringBuffer();
			pks.forEach(pk->{
				fmtBuf.append(pk.getPkName())
						.append(": ");
				int mine = cmdSvCardMapper.countCardMyCardByPkId(pk.getId(), targetType, targetId, userId);
				int all = cmdSvCardMapper.countCardByPkAndCareerAndRarity(pk.getId(), null, "4")+cmdSvCardMapper.countCardByPkAndCareerAndRarity(pk.getId(), null, "5");
				fmtBuf.append(mine).append("/").append(all).append("\n");
			});
			return fmtBuf.substring(0, fmtBuf.length());
		}
		return null;
	}

}
