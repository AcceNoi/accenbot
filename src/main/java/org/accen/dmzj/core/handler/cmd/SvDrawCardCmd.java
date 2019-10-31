package org.accen.dmzj.core.handler.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomMeta;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CmdSvCardMapper;
import org.accen.dmzj.web.vo.CmdSvCard;
import org.accen.dmzj.web.vo.CmdSvPk;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
		return "影之诗抽卡森林咆哮";
	}
	@Value("${coolq.sv.drawcount:8}")
	private int drawCount = 8;//单次抽取张数
	@Value("${coolq.sv.coin.descrease:8}")
	private int decrease = 10;//抽取金币消耗
	
	private String[] careers = new String[] {"铜","银","金","虹","异画"};

	private int[] returnCoin = new int[] {0,1,3,8,16};
	
	private final static Pattern pattern = Pattern.compile("^影之诗(十连)?抽卡(.*)");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
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
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您库存金币不够了哦，暂无法抽卡添加词条喵~");
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
						int bronze = 0;
						int silver = 0;
						int gold = 0;
						int legend = 0;
						int diffPaint = 0;
						
						//单抽不会用到，因为会打乱顺序
						Map<CmdSvCard, Integer> cardCountMap = new HashMap<CmdSvCard, Integer>();
						
						for (int i = 0; i <rs.size(); i++) {
//							legend |= rs.get(i).getCardRarity()==4;
//							diffPaint |= rs.get(i).getCardRarity()==5;
							
							switch (rs.get(i).getCardRarity()) {
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
							}
							
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
							for(CmdSvCard card:cardCountMap.keySet()) {
								String desc =card.getCareer()
										+" "
										+careers[card.getCardRarity()-1];
										
								msgBuf.append(++index+". ["+desc+"]"+card.getCardName()+" * "+cardCountMap.get(card)+"\n");
							}
						}
						
						
						if(diffPaint>0) {
							msgBuf.append("抽到异画啦！欧狗吃矛！");
						}else if(legend>0) {
							msgBuf.append("抽到虹卡啦！恭喜这个B......站用户。");
						}
						
						//返还金币
						int returnC = bronze*returnCoin[0]+silver*returnCoin[1]+gold*returnCoin[2]+legend*returnCoin[3]+diffPaint*returnCoin[4];
						
						newCoin = checkinCmd.modifyCoin(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId(), returnC);
						msgBuf.append("本次获得金币："+returnC+"，库存："+newCoin);
						if(type==1) {
							msgBuf.append("\n")
								.append(StringUtil.SPLIT_FOOT)
								.append("Tips：现已支持十连抽卡了喵~发送影之诗十连抽卡看看喵~");
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
			
		}
		return null;
	}

}
