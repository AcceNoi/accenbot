package dmzjbot;

import org.accen.dmzj.core.annotation.CmdRegular;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.stereotype.Component;

@Component
public class Demo {
	@CmdRegular(expression = "^检索(.+)$")
	@GeneralMessage
	public String search(String key) {
		//TODO your code
		return "检索结果...";
	}
	
	@CmdRegular(expression = "^检索(\\d+)$",qmessageParamIndex = 0)
	@GeneralMessage(targetId = "123456")
	public String search(Qmessage qmassage,int pid) {
		//TODO your code
		return "检索结果...";
	}
}
