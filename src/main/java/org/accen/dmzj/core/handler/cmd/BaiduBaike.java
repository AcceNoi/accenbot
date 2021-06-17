package org.accen.dmzj.core.handler.cmd;

import java.util.List;

import org.accen.dmzj.core.annotation.AutowiredRegular;
import org.accen.dmzj.core.annotation.CmdMessage;
import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.annotation.MessageRegular;
import org.accen.dmzj.core.api.baidu.BaikeApicClientPk;
import org.accen.dmzj.core.api.vo.BaikeResult;
import org.accen.dmzj.core.handler.group.Pedia;
import org.accen.dmzj.core.meta.MessageType;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.dao.CmdWikiMapper;
import org.accen.dmzj.web.vo.CmdWiki;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@FuncSwitch(name = "cmd_baike",
			title = "百科",
			showMenu = true,
			format = "了解+[想了解的内容]", 
			order = 1,
			groupClass = Pedia.class)
@Component
public class BaiduBaike {

	
	@Autowired
	private BaikeApicClientPk baikeApicClientPk;
	@Autowired
	private CmdWikiMapper cmdWikiMapper;
	
	@CmdMessage(messageType = MessageType.GROUP)
	@MessageRegular(expression = "^了解(.+)")
	@GeneralMessage
	public String cmdAdapt(@AutowiredRegular(1) String kw) {
		
		//1.先在wiki中查
		CmdWiki wiki = cmdWikiMapper.selectByName(kw);
		if(wiki==null) {
			//名字没匹配到，再去找关键字
			List<CmdWiki> wikis = cmdWikiMapper.findByKeyword(kw);
			if(wikis!=null&&!wikis.isEmpty()) {
				wiki = wikis.get(RandomUtil.randomInt(wikis.size()));
			}
		}
		if(wiki==null) {
			//2.找不到则去百度百科查
			BaikeResult br = baikeApicClientPk.baike(kw);
			if(br!=null) {
				return CQUtil.imageUrl(br.getImageUrl())+br.getTitle()+"\n"+br.getSummary()+"["+br.getUrl()+"]喵~";
			}else {
				return "抱歉，我太弱了，找不到该词条喵~";
			}
		}else {
			
			return (wiki.getImage()==null?"":CQUtil.imageUrl(RandomUtil.randomStringSplit(wiki.getImage())))
					+wiki.getWikiName()+"\n"+wiki.getContent();
		}
	}
	
}
