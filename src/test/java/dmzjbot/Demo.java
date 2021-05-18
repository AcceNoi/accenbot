package dmzjbot;

import java.sql.Date;

import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.AutowiredRegular;
import org.accen.dmzj.core.annotation.CmdRegular;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.stereotype.Component;

@Component
public class Demo {
	@CmdRegular(expression = "^检索(.+)$",enableAutowiredParam = false)
	@GeneralMessage
	public String search(String key) {
		//TODO your code
		return "检索结果...";
	}
	
	@CmdRegular(expression = "^用(.+)引擎检索(\\d+)$")
	@GeneralMessage(targetId = "123456")
	public String search(Qmessage qmassage
						,@AutowiredParam("message") String msg
						,@AutowiredParam Date sendTime
						,int pid
						,@AutowiredRegular(1) String engine) {
		//TODO your code
		return "检索结果...";
	}
}
