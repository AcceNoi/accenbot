package org.accen.dmzj.core.handler.cmd;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.callbacker.AsyncCallback;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.core.timer.Prank;
import org.accen.dmzj.core.timer.RankType;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@FuncSwitch("cmd_prank")
@Component
public class PixivRankCmd implements CmdAdapter,AsyncCallback {

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
	private Prank prank;
	@Autowired
	private TaskManager taskManager;

	private static final Pattern rankPattern = Pattern.compile("^(p|P)站(上上|前|今|本|当|上|昨){0,1}(日|周|月)榜([1-9]){0,1}");
	private static ArrayList<Set<String>> offsetArr = new ArrayList<Set<String>>(3);
	static {
		offsetArr.add(0, Set.of("今","本","当"));
		offsetArr.add(1, Set.of("上","昨"));
		offsetArr.add(2, Set.of("上上","前"));
	}
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = rankPattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			String type = matcher.group(3);
			RankType mode;
			int offsetI = 0;
			String offset=matcher.group(2);
			if(offset!=null) {
				for(int i=0;i<offsetArr.size();i++) {
					if(offsetArr.get(i).contains(offset)) {
						offsetI=i;
						break;
					}
				}
			}
			switch (type) {
			case "日":
				mode = RankType.DAY;
				break;
			case "月":
				mode = RankType.MONTH;
				break;
			case "周":
				mode = RankType.WEEK;
				break;
			default:
				mode = RankType.DAY;
				break;
			}
			int page = matcher.group(4)==null?1:Integer.valueOf(matcher.group(4));
			//TODO 定义callback
			String prankUrl = prank.rank(LocalDate.now(), mode, offsetI, page, this,qmessage,selfQnum);
			if(prankUrl==null) {
				task.setMessage("初次获取榜单需花费较长时间，请稍稍等待喵~");
			}else {
				task.setMessage(CQUtil.image(prankUrl)+"\n发送P站找图+pid可以查看大图喵~");
			}
			return task;
		}
		return null;
	}

	@Override
	public void callback(String message, Object detail,Object... callbackParams) {
		if("failed".equals(message)) {
			//失败了
			taskManager.addGeneralTaskQuick((String)callbackParams[1]
					, ((Qmessage)callbackParams[0]).getMessageType()
					, ((Qmessage)callbackParams[0]).getGroupId()
					, "排行榜获取失败！请稍后再试喵~");
		}else if("success".equals(message)) {
			//成功了
			taskManager.addGeneralTaskQuick((String)callbackParams[1]
					, ((Qmessage)callbackParams[0]).getMessageType()
					, ((Qmessage)callbackParams[0]).getGroupId()
					, CQUtil.image((String)detail)+"\n发送P站找图+pid可以查看大图喵~");
		}
		return ;
	}

}