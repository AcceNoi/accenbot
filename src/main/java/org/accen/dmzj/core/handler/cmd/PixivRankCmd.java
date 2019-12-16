package org.accen.dmzj.core.handler.cmd;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.PixivicApiClient;
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
	private static final String tempDir = "prank/";
	
	private static final Pattern rankPattern = Pattern.compile("^(p|P)站(日|周|月)榜(1|2|3){0,1}");

	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = rankPattern.matcher(message);
		if(matcher.matches()) {
			String type = matcher.group(2);
			String mode;
			
			switch (type) {
			case "日":
				mode = "day";
				break;
			case "月":
				mode = "month";
				break;
			case "周":
				mode = "week";
				break;
			default:
				mode = "day";
				break;
			}
			int page = matcher.group(3)==null?1:Integer.parseInt(matcher.group(3));
			//图片命名策略
			String ranFilePath = rankImageTempHome+tempDir+
			Map<String, Object> rs = pixivicApiClient.rank(1, date, mode)
		}
		return null;
	}

}
