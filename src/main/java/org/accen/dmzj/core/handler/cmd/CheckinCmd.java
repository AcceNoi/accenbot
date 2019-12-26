package org.accen.dmzj.core.handler.cmd;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.util.render.CheckinRender;
import org.accen.dmzj.util.render.LocalFileRenderImage;
import org.accen.dmzj.util.render.UrlRenderImage;
import org.accen.dmzj.web.dao.SysGroupMemberMapper;
import org.accen.dmzj.web.vo.Qmessage;
import org.accen.dmzj.web.vo.SysGroupMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@FuncSwitch("cmd_checkin")
@Component
@Transactional
public class CheckinCmd implements CmdAdapter {
	@Value("${coolq.mem.iniCoin}")
	private int iniCoin = 20;//初始金币
	@Value("${coolq.mem.coinIncr}")
	private int coinIncr = 5;//金币递增
	@Value("${coolq.mem.iniFavorability}")
	private int iniFavorability = 0;//初始好感
	@Value("${coolq.mem.favorabilityIncr}")
	private int favorabilityIncr = 1;//好感递增
	@Value("${coolq.mem.ticketProb:0.05}")
	private double ticketProb ;//卡券获取概率
	
	@Value("${sys.static.html.upload}")
	private String groundTempHome ;

	private static final String groundDir = "pground/";
	private static final String tempDir = "checkinTemp/";
	@Autowired
	private SysGroupMemberMapper sysGroupMember; 
	@Autowired
	private SvDrawCardCmd svCmd;
	@Autowired
	private TaskManager taskManager;
	
	@Override
	public String describe() {
		return "签到或者绑定账号";
	}

	@Override
	public String example() {
		return "签到";
	}

	private final static Pattern pattern = Pattern.compile("^(个人信息|绑定|签到)$");
	private final static Pattern remarkPattern = Pattern.compile("^设置留言(.+)");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		Matcher remarkMatcher = remarkPattern.matcher(message);
		if(matcher.matches()) {
			
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			List<SysGroupMember> mems = sysGroupMember.selectByTarget(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			if("绑定".equals(matcher.group(1))) {
				if(mems!=null&&!mems.isEmpty()) {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您已绑定过了喵！库存金币："+mems.get(0).getCoin()+"枚，好感度："+mems.get(0).getFavorability());
				}else {
					SysGroupMember mem = new SysGroupMember();
					mem.setType(qmessage.getMessageType());
					mem.setTargetId(qmessage.getGroupId());
					mem.setUserId(qmessage.getUserId());
					mem.setCoin(iniCoin);
					mem.setCheckinCount(0);
					mem.setFavorability(iniFavorability);
					mem.setCreateTime(new Date());
					mem.setStatus(1);
					sysGroupMember.insert(mem);
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 绑定成功喵~");
				}
			}else if("签到".equals(matcher.group(1))) {
				if(mems!=null&&!mems.isEmpty()) {
					
					Date lastCheckinTime = mems.get(0).getLastCheckinTime();
					if(lastCheckinTime!=null) {
						LocalDateTime now = LocalDateTime.now();
						LocalDateTime last = lastCheckinTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
						if(now.getYear()==last.getYear()
								&&now.getMonthValue()==last.getMonthValue()
								&&now.getDayOfMonth()==last.getDayOfMonth()) {
							task.setMessage(CQUtil.at(qmessage.getUserId())+" 您今天已经签到过了喵~");
						}else {
							gainCardTicket(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
							boolean gained = gainCardTicket2(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
							
							SysGroupMember mem = mems.get(0);
							int ci = coinIncr;
							if(coinIncr<0) {
								//2019.10.25根据好感度增加下限 2 * log(1.5) (X+1)
								double lowerLimitD = 2*(Math.log(mem.getFavorability()+1)/Math.log(1.5));
								int lowLimit = lowerLimitD<0?0:((int)lowerLimitD);
								
								ci = lowLimit + RandomUtil.randomInt(-coinIncr);
							}
							mem.setCoin(mem.getCoin()+ci);
							mem.setCheckinCount(mem.getCheckinCount()+1);
//							int fi = favorabilityIncr;
//							if(favorabilityIncr<0) {
//								fi = RandomUtil.randomInt(6);
//							}
							mem.setFavorability(mem.getFavorability()+favorabilityIncr);
							mem.setLastCheckinTime(new Date());
							sysGroupMember.updateCheckin(mem);
							/*String msg = CQUtil.at(qmessage.getUserId())+" 签到成功喵！库存金币："+mem.getCoin()+"(+"+ci+")"+"枚，好感度："+mem.getFavorability()+"(+"+favorabilityIncr+")。";
							String svCompletion = svCmd.formatMyCardCompletion(qmessage.getMessageType(),qmessage.getGroupId(), qmessage.getUserId());
							task.setMessage(svCompletion==null?msg:(msg+"\n"+StringUtil.SPLIT_FOOT+"影之诗图鉴完成度：\n"+svCompletion));*/
							Map<String, String> memEnhance = new HashMap<String, String>();
							memEnhance.put("coin", "+"+ci);
							memEnhance.put("fav", "+"+favorabilityIncr);
							memEnhance.put("checkin", "+"+1);
							if(gained) {
								memEnhance.put("ticket", "+"+1);
							}
							
							String card = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("card");
							String nickName = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("nickname");
							String[][] svCompletions = svCmd.formatMyCardCompletion2(qmessage.getMessageType(),qmessage.getGroupId(), qmessage.getUserId());
							try {
								CheckinRender render = new CheckinRender(new LocalFileRenderImage(randomGroundFile()), mem, svCompletions, new UrlRenderImage(new URL("http://q1.qlogo.cn/g?b=qq&nk="+mem.getUserId()+"&s=640")), StringUtils.isEmpty(card)?nickName:card,memEnhance);
								String templeFileName = qmessage.getMessageType()+qmessage.getGroupId()+"-"+qmessage.getUserId()+".jpg";
								File outFile = new File(groundTempHome+tempDir+templeFileName);
								render.render(outFile);
								task.setMessage(CQUtil.imageUrl("file:///"+outFile.getAbsolutePath()));
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}else {
						//gainCardTicket(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
						boolean gained = gainCardTicket2(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
						
						SysGroupMember mem = mems.get(0);
						int ci = coinIncr;
						if(coinIncr<0) {
							ci = RandomUtil.randomInt(-coinIncr);
						}
						mem.setCoin(mem.getCoin()+ci);
						mem.setCheckinCount(mem.getCheckinCount()+1);
//						int fi = favorabilityIncr;
//						if(favorabilityIncr<0) {
//							fi = RandomUtil.randomInt(6);
//						}
						mem.setFavorability(mem.getFavorability()+favorabilityIncr);
						mem.setLastCheckinTime(new Date());
						sysGroupMember.updateCheckin(mem);
						/*String msg = CQUtil.at(qmessage.getUserId())+" 签到成功喵！库存金币："+mem.getCoin()+"(+"+ci+")"+"枚，好感度："+mem.getFavorability()+"(+"+favorabilityIncr+")。";
						String svCompletion = svCmd.formatMyCardCompletion(qmessage.getMessageType(),qmessage.getGroupId(), qmessage.getUserId());
						task.setMessage(svCompletion==null?msg:(msg+"\n"+StringUtil.SPLIT_FOOT+"影之诗图鉴完成度：\n"+svCompletion));
						*/
						Map<String, String> memEnhance = new HashMap<String, String>();
						memEnhance.put("coin", "+"+ci);
						memEnhance.put("fav", "+"+favorabilityIncr);
						memEnhance.put("checkin", "+"+1);
						if(gained) {
							memEnhance.put("ticket", "+"+1);
						}
						
						String card = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("card");
						String nickName = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("nickname");
						String[][] svCompletions = svCmd.formatMyCardCompletion2(qmessage.getMessageType(),qmessage.getGroupId(), qmessage.getUserId());
						try {
							CheckinRender render = new CheckinRender(new LocalFileRenderImage(randomGroundFile()), mem, svCompletions, new UrlRenderImage(new URL("http://q1.qlogo.cn/g?b=qq&nk="+mem.getUserId()+"&s=640")), StringUtils.isEmpty(card)?nickName:card,memEnhance);
							String templeFileName = qmessage.getMessageType()+qmessage.getGroupId()+"-"+qmessage.getUserId()+".jpg";
							File outFile = new File(groundTempHome+tempDir+templeFileName);
							render.render(outFile);
							task.setMessage(CQUtil.imageUrl("file:///"+outFile.getAbsolutePath()));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}else {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还没绑定个人信息哦，请发送[绑定]进行绑定喵~");
				}
			}else if("个人信息".equals(matcher.group(1))) {
				if(mems!=null&&!mems.isEmpty()) {
					/*String msg = CQUtil.at(qmessage.getUserId())+" 库存金币："+mems.get(0).getCoin()+"枚，好感度："+mems.get(0).getFavorability()+"，签到次数："+mems.get(0).getCheckinCount()+"，复读次数："+mems.get(0).getRepeatCount()+"次，影之诗传说卡券："+mems.get(0).getCardTicket()+"。";
					String svCompletion = svCmd.formatMyCardCompletion(qmessage.getMessageType(),qmessage.getGroupId(), qmessage.getUserId());
					task.setMessage(svCompletion==null?msg:(msg+"\n"+StringUtil.SPLIT_FOOT+"影之诗图鉴完成度：\n"+svCompletion));*/
										
					String card = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("card");
					String nickName = (String) ((Map<String,Object>)qmessage.getEvent().get("sender")).get("nickname");
					String[][] svCompletions = svCmd.formatMyCardCompletion2(qmessage.getMessageType(),qmessage.getGroupId(), qmessage.getUserId());
					try {
						CheckinRender render = new CheckinRender(new LocalFileRenderImage(randomGroundFile()), mems.get(0), svCompletions, new UrlRenderImage(new URL("http://q1.qlogo.cn/g?b=qq&nk="+mems.get(0).getUserId()+"&s=640")), StringUtils.isEmpty(card)?nickName:card,null);
						String templeFileName = qmessage.getMessageType()+qmessage.getGroupId()+"-"+qmessage.getUserId()+".jpg";
						File outFile = new File(groundTempHome+tempDir+templeFileName);
						render.render(outFile);
						task.setMessage(CQUtil.imageUrl("file:///"+outFile.getAbsolutePath()));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还没绑定个人信息哦，请发送[绑定]进行绑定喵~");
				}
			}
			return task;
		}
		if(remarkMatcher.matches()) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			List<SysGroupMember> mems = sysGroupMember.selectByTarget(qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getUserId());
			if(mems==null||mems.isEmpty()) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 您还没绑定个人信息哦，请发送[绑定]进行绑定喵~");
				return task;
			}
			String remark = remarkMatcher.group(1);
			if(remark.length()>15) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 留言不要超过15个字啦，狐狸会坏掉的喵！");
				return task;
			}else {
				SysGroupMember mem = mems.get(0);
				mem.setRemark(remark);
				sysGroupMember.updateCheckin(mem);
				task.setMessage(CQUtil.at(qmessage.getUserId())+" 留言设置成功喵！");
				return task;
			}
		}
		return null;
	}
	
	/**
	 * 获取一名用户的金币数，如果为负数，则表示还未绑定
	 * @param type
	 * @param targetId
	 * @param userId
	 * @return
	 */
	public int getCoin(String type,String targetId,String userId) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			return mems.get(0).getCoin();
		}
	}
	/**
	 * 消耗或补充金币
	 * @param type
	 * @param targetId
	 * @param userId
	 * @param diff 需要变化金币数
	 * @return 剩余金币数 如果喂绑定，则返回-999
	 */
	public int modifyCoin(String type,String targetId,String userId,int diff) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			SysGroupMember mem = mems.get(0);
//			sysGroupMember.updateCoinByTarget(mem.getCoin()+diff, type, targetId, userId);
			mem.setCoin(mem.getCoin()+diff);
			sysGroupMember.updateCheckin(mem);
			return mem.getCoin();
		}
	}

	/**
	 * 消耗× 增加好感
	 * @param type
	 * @param targetId
	 * @param userId
	 * @param diff
	 * @return 执行完后好感度
	 */
	public int modifyFav(String type,String targetId,String userId,int diff) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			SysGroupMember mem = mems.get(0);
			mem.setFavorability(mem.getFavorability()+diff);
			sysGroupMember.updateCheckin(mem);
//			sysGroupMember.updateFavByTarget(mem.getFavorability()+diff, type, targetId, userId);
			return mem.getFavorability();
		}
	}
	/**
	 * 获取一名用户的好感度，如果为负数，则表示还未绑定
	 * @param type
	 * @param targetId
	 * @param userId
	 * @return
	 */
	public int getFav(String type,String targetId,String userId) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			return mems.get(0).getFavorability();
		}
	}
	/**
	 * 消耗或补充复读次数
	 * @param type
	 * @param targetId
	 * @param userId
	 * @param diff 需要变化复读次金币数
	 * @return 剩余复读金币数 如果未绑定，则返回-999
	 */
	public int modifyRepeat(String type,String targetId,String userId,int diff) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			SysGroupMember mem = mems.get(0);
//			sysGroupMember.updateCoinByTarget(mem.getCoin()+diff, type, targetId, userId);
			mem.setRepeatCount(mem.getRepeatCount()+diff);;
			sysGroupMember.updateCheckin(mem);
			return mem.getRepeatCount();
		}
	}
	/**
	 * 消耗或补充卡券
	 * @param type
	 * @param targetId
	 * @param userId
	 * @param diff 需要变化的卡券数
	 * @return 剩余卡券数，如果未绑定，则返回-999
	 */
	public int modifyCardTicket(String type,String targetId,String userId,int diff) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			SysGroupMember mem = mems.get(0);
//			sysGroupMember.updateCoinByTarget(mem.getCoin()+diff, type, targetId, userId);
			mem.setCardTicket(mem.getCardTicket()+diff);;
			sysGroupMember.updateCheckin(mem);
			return mem.getCardTicket();
		}
	}
	/**
	 * 获取一名用户的卡券，如果为负数，则表示还未绑定
	 * @param type
	 * @param targetId
	 * @param userId
	 * @return
	 */
	public int getTicket(String type,String targetId,String userId) {
		List<SysGroupMember> mems = sysGroupMember.selectByTarget(type, targetId, userId);
		if(mems==null||mems.isEmpty()) {
			return -999;
		}else {
			return mems.get(0).getCardTicket();
		}
	}
	/**&
	 * 单独的获取卡券的功能
	 * @param botId
	 * @param type
	 * @param targetId
	 * @param userId
	 */
	@Deprecated
	public void gainCardTicket(String botId,String type,String targetId,String userId) {
		if(RandomUtil.randomPass(ticketProb)) {
			int curTicket = modifyCardTicket(type, targetId, userId, 1);
			if(curTicket>0) {
				taskManager.addGeneralTaskQuick(botId, type, targetId, CQUtil.at(userId)+" 恭喜获得1张传说卡券，现共有"+curTicket+"张券，发送影之诗翻牌+[卡包名]消耗卡券可开出虹卡以上的卡喵~");
			}//未绑定不做处理 
			
		}
	}
	/**
	 * 获取卡券，true则为获取成功
	 * @param botId
	 * @param type
	 * @param targetId
	 * @param userId
	 * @return 
	 */
	public boolean gainCardTicket2(String botId,String type,String targetId,String userId) {
		if(RandomUtil.randomPass(ticketProb)) {
			int curTicket = modifyCardTicket(type, targetId, userId, 1);
			if(curTicket>0) {
				return true;
//				taskManager.addGeneralTaskQuick(botId, type, targetId, CQUtil.at(userId)+" 恭喜获得1张传说卡券，现共有"+curTicket+"张券，发送影之诗翻牌+[卡包名]消耗卡券可开出虹卡以上的卡喵~");
			}//未绑定不做处理 
			
		}
		return false;
	}
	
	/**
	 * 随机从pground中取一张图
	 * @return
	 */
	private File randomGroundFile() {
		File dir = new File(groundTempHome+groundDir);
		File[] all = dir.listFiles();
		return all[RandomUtil.randomInt(all.length)];
	}
}
