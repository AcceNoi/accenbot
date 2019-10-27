package org.accen.dmzj.core.handler.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.accen.dmzj.core.handler.callbacker.CallbackListener;
import org.accen.dmzj.core.handler.callbacker.CallbackManager;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.dao.CmdGameMapper;
import org.accen.dmzj.web.vo.CmdGame;
import org.accen.dmzj.web.vo.CmdGameNode;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GamerManager implements CallbackListener{
	@Autowired
	private CmdGameMapper cmdGameMapper;
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private CallbackManager callbackManager;
	/**
	 * 当前进行着得的游戏信息 groupId_userId->last game node
	 */
	private Map<String, CmdGameNode> gameInfoMap = new HashMap<String, CmdGameNode>();
	private static final Pattern listPattern = Pattern.compile("^游戏列表$");
	private static final Pattern beginPattern = Pattern.compile("^(.+)?开始游戏$");
	private static final Pattern processPattern = Pattern.compile("^游戏进程$");
//	private static final Pattern endPattern = Pattern.compile("^查看结果$");
	public void game(Qmessage qmessage,String selfQnum) {
		String message = qmessage.getMessage().trim();
		String key = qmessage.getGroupId()+"_"+qmessage.getUserId();
		
		//0.游戏列表
		Matcher listMatcher = listPattern.matcher(message);
		if(listMatcher.matches()) {
			List<CmdGame> games = cmdGameMapper.findAllGame();
			if(games!=null&&!games.isEmpty()) {
				StringBuffer desc = new StringBuffer();
				String gameList = IntStream.range(0, games.size())
						.mapToObj(i->i+". "+games.get(i).getGameName())
						.collect(Collectors.joining("\n"));
				desc.append(gameList)
					.append("\n")
					.append("输入[游戏名]+开始游戏 可进行游戏喵~");
				taskManager.addGeneralTaskQuick(selfQnum
						, qmessage.getMessageType()
						, qmessage.getGroupId()
						, desc.toString());
				return ;
			}else {
				return ;
			}
		}
			
		//1.是否是启动
		Matcher beginMatcher = beginPattern.matcher(message);
		if(beginMatcher.matches()) {
			String gameName = beginMatcher.group(1);
			//1.1 先检查是否含有这样一个游戏
			CmdGame game = cmdGameMapper.selectGameByName(gameName);
			if(game!=null) {
				//1.2 有这个游戏
				if(gameInfoMap.containsKey(key)) {
					//1.2.1 已有进行中的游戏，则警告
					taskManager.addGeneralTaskQuick(selfQnum
							, qmessage.getMessageType()
							, qmessage.getGroupId()
							, CQUtil.at(qmessage.getUserId())+" 当前还有进行中的游戏，请先完成当前游戏喵~回复[游戏进程]可查看当前进行中的游戏");
					return ;
				}else {
					//1.2.2 没有，则开始进行游戏
					chooseGame(qmessage,game,selfQnum);
					return ;
				}
			}else {
				//1.3 没有这个游戏
				if(gameInfoMap.containsKey(key)) {
					//1.3.1 已有进行中的游戏
					return ;
				}else {
					//1.3.2 没有进行中的游戏
					taskManager.addGeneralTaskQuick(selfQnum
							, qmessage.getMessageType()
							, qmessage.getGroupId()
							, CQUtil.at(qmessage.getUserId())+" 未找到该游戏喵~回复[游戏列表]可以查看所有游戏");
					return ;
				}
			}
		}
		
		//2. 游戏进程
		Matcher processMatcher = processPattern.matcher(message);
		if(processMatcher.matches()) {
			if(gameInfoMap.containsKey(key)) {
				CmdGameNode node = gameInfoMap.get(key);
				CmdGame game = cmdGameMapper.selectGameById(node.getGameId());
				taskManager.addGeneralTaskQuick(selfQnum
						, qmessage.getMessageType()
						, qmessage.getGroupId()
						, CQUtil.at(qmessage.getUserId())+"\n"
								+"【当前游戏】"+game.getGameName()+"\n"
								+"【游戏进度】"+node.getNodeDesc());
				return ;
			}else {
				taskManager.addGeneralTaskQuick(selfQnum
						, qmessage.getMessageType()
						, qmessage.getGroupId()
						, CQUtil.at(qmessage.getUserId())+" 您尚未开始游戏喵~回复[游戏列表]可以查看所有游戏");
				return ;
			}
		}
		
	}
	public void chooseGame(Qmessage qmessage,CmdGame game,String selfQnum) {
		CmdGameNode firstNode = cmdGameMapper.findFirstNodeByGame(game.getId());
		taskManager.addGeneralTaskQuick(selfQnum
				, qmessage.getMessageType()
				, qmessage.getGroupId()
				, CQUtil.at(qmessage.getUserId())+" "+firstNode.getNodeDesc()+"\n[回复时请@Bot]");
		gameInfoMap.put(qmessage.getGroupId()+"_"+qmessage.getUserId(), firstNode);
		callbackManager.addCallbackListener(this, qmessage);
	}
	
	private static final Pattern replyPattern = Pattern.compile("^\\[CQ:at,qq=(\\d+)?\\](.+)$");
	@Override
	public boolean listen(Qmessage originQmessage, Qmessage qmessage, String selfQnum) {
		if(originQmessage!=qmessage&&originQmessage.getGroupId().equals(qmessage.getGroupId())&&originQmessage.getUserId().equals(qmessage.getUserId())) {
			Matcher replyMatcher = replyPattern.matcher(qmessage.getMessage().trim());
			if(replyMatcher.matches()&&gameInfoMap.containsKey(qmessage.getGroupId()+"_"+qmessage.getUserId())) {
				String atQQ = replyMatcher.group(1);
				String ctt = replyMatcher.group(2).trim();
				if(atQQ.equals(selfQnum)) {
					//at了bot
					CmdGameNode lastNode = gameInfoMap.get(qmessage.getGroupId()+"_"+qmessage.getUserId());
					//1.当前是最终节点，且回复的是“查看接口”
					if("Last".equals(lastNode.getNodeType())&&"查看结果".equals(ctt)) {
						List<CmdGameNode> resultNodes = cmdGameMapper.findNextNode(lastNode.getId());
						taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(),
								CQUtil.at(qmessage.getUserId())+" "+resultNodes.get(0).getNodeDesc());
						gameInfoMap.remove(qmessage.getGroupId()+"_"+qmessage.getUserId());
						return true;
					}else {
						CmdGameNode nextNode = cmdGameMapper.selectNextNodeByNo(lastNode.getId(), ctt);
						if(nextNode!=null) {
							taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(),
									CQUtil.at(qmessage.getUserId())+" "+nextNode.getNodeDesc());
							gameInfoMap.put(qmessage.getGroupId()+"_"+qmessage.getUserId(), nextNode);
							callbackManager.addCallbackListener(this, qmessage);
							return false;//由于上述添加的key都是this，所以这里返回false
						}
					}
				}
			}
		}
		return false;
	}
}
