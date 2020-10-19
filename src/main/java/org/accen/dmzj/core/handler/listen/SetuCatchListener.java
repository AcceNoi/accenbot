package org.accen.dmzj.core.handler.listen;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.setu.CqSetuGreper;
import org.accen.dmzj.util.setu.PixivSetuGreper;
import org.accen.dmzj.util.setu.SetuGreper;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 本地消息抓取涩图的监听器，现在判断是否是涩图的逻辑比较简单，有待扩展
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
public class SetuCatchListener implements ListenAdpter{
	private Set<String> allowedSenders = Set.of("2735919291");//yome喵
	private long minImageSize = 100*1024;//单位为Byte
	private long maxImageSize = 10*1024*1024;
	@Autowired
	private TaskManager taskManager;
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		SetuGreper greper = isSetu(qmessage);
		if(greper!=null) {
			int successCont = greper.grep();
			//通知  
			if(successCont>0) {
				taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), "涩图充能+"+successCont+"！");
			}
			
		}
		return null;
	}
	/**
	 * 判断是否是涩图的方法，可实现此方法来扩展
	 * @param qmessage
	 * @return
	 */
	protected SetuGreper isSetu(Qmessage qmessage) {
		/*if(!"857083789".equals(qmessage.getGroupId())){
			return false;
		}*/
		if(!CQUtil.hasImg(qmessage.getMessage())) {
			return null;
		}
		if(!allowedSenders.contains(qmessage.getUserId())&&!Pattern.matches("^(涩|色|瑟|se)图充能.*", qmessage.getMessage())) {
			return null;
		}
		Matcher mt = pixivPattern.matcher(qmessage.getMessage());
		if(mt.matches()) {
			String pidFmting = mt.group(2);
			return new PixivSetuGreper(pidFmting.split(",|，|、"));
		}
		List<String> cqs = CQUtil.grepImageCq(qmessage.getMessage());
		if(!cqs.isEmpty()) {
			return new CqSetuGreper(minImageSize, maxImageSize, qmessage, cqs);
		}
		return null;
	}
	
	private static final Pattern pixivPattern = Pattern.compile("^(涩|色|瑟|se)图充能(\\d+?((，|,|、)\\d+?)*)");
}
