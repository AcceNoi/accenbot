package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.baidu.BaikeApicClientPk;
import org.accen.dmzj.core.task.api.vo.BaikeResult;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CmdWikiMapper;
import org.accen.dmzj.web.vo.CmdWiki;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@FuncSwitch("cmd_baike")
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
	@Autowired
	private CmdWikiMapper cmdWikiMapper;
	
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
			
			String kw = matcher.group(1).trim();
			//1.先在wiki中查
			CmdWiki wiki = cmdWikiMapper.selectByName(kw);
			if(wiki==null) {
				//名字没匹配到，再去找关键字
				List<CmdWiki> wikis = cmdWikiMapper.findByKeyword(kw);
				if(wikis!=null) {
					wiki = wikis.get(RandomUtil.randomInt(wikis.size()));
				}
			}
			if(wiki==null) {
				//2.找不到则去百度百科查
				BaikeResult br = baikeApicClientPk.baike(kw);
				if(br!=null) {
					task.setMessage(CQUtil.imageUrl(br.getImageUrl())+br.getTitle()+"\n"+br.getSummary()+"["+br.getUrl()+"]喵~");
				}else {
					task.setMessage("抱歉，我太弱了，找不到该词条喵~");
				}
			}else {
				
				task.setMessage((wiki.getImage()==null?"":CQUtil.imageUrl(RandomUtil.randomStringSplit(wiki.getImage())))
						+wiki.getWikiName()+"\n"+wiki.getContent());
			}
			
			return task;
		}
		return null;
	}
	
}
