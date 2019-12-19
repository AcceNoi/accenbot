package org.accen.dmzj.core.handler.cmd;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.PixivicApiClient;
import org.accen.dmzj.core.timer.Prank;
import org.accen.dmzj.core.timer.RankType;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class PixivRankCmd implements CmdAdapter {

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
	private PixivicApiClient pixivicApiClient ;
	@Value("${sys.static.html.upload}")
	private String rankImageTempHome ;
	@Value("${sys.static.url.upload}")
	private String localUrl;
	
	@Autowired
	private Prank prank;
	private static final String tempDir = "prank/";
	
	private static final Pattern rankPattern = Pattern.compile("^(p|P)站(上上|前|今|本|当|上|昨){0,1}(日|周|月)榜(1-9){0,1}");
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
			prank.rank(LocalDate.now(), mode, offsetI, page, null);
		}
		return null;
	}

}