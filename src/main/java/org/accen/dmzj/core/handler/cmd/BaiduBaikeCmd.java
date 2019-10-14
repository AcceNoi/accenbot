package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.baidu.BaikeApicClientPk;
import org.accen.dmzj.core.task.api.vo.BaikeResult;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaiduBaikeCmd implements CmdAdapter{

	@Override
	public String describe() {
		return "获取一个词条的摘要";
	}

	@Override
	public String example() {
		return "了解克洛诺斯";
	}
	
	@Autowired
	private BaikeApicClientPk baikeApicClientPk;

	private final static Pattern pattern = Pattern.compile("^了解(.+)");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			BaikeResult br = baikeApicClientPk.baike(matcher.group(1));
			if(br!=null) {
				task.setMessage(br.getTitle()+"\n"+br.getSummary()+"["+br.getUrl()+"]喵~");
			}else {
				task.setMessage("抱歉，我太弱了，找不到该词条喵~");
			}
			return task;
		}
		return null;
	}
	
}
