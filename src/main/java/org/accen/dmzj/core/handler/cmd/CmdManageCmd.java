package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class CmdManageCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "展示所有的功能";
	}

	@Override
	public String example() {
		return "";
	}

	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		//直接做个简单的
//		initCmds();
		
		String message = qmessage.getMessage().trim();
		if("功能".equals(message)) {
			GeneralTask task = new GeneralTask();
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			StringBuffer func = new StringBuffer();
			func.append("1. 搜图 发送【老婆找图+[图片]】 \n")
				.append("2. 翻译 发送【日语|英语|俄语|法语|西班牙语说+[要翻译的内容]】\n")
				.append("3. 百科 发送【了解+[要百科的内容]】\n")
				.append("4. 随机图片 发送【随机瑟图】\n")
				.append("5. 点歌 发送【网易点歌+[歌名]】\n")
				.append("6. 定时提醒 发送【老婆xx小时xx分钟xx秒后提醒我+[需要提醒的事]】\n")
				.append("7. 抽卡 发送【影之诗抽卡+[对应的卡包]】\n")
				.append("8. 复读 发送【老婆说+[想复读的内容]】\n")
				.append("9. 语音复读 发送【说+[想复读的内容]】\n")
				.append("10. 词条 发送【添加[精确]问xx答[回复]xx】");
			task.setMessage(
//					cmds.stream()
//						.map(cmd->cmds.indexOf(cmd)+1+"."+cmd.describe()+"示例："+cmd.example())
//						.collect(Collectors.joining("\n"))
					func.toString()
						);
			return task;
		}
		return null;
		
	}
	private List<CmdAdapter> cmds = null;
	/**
	 * 初始化功能列表，但要排除自身
	 */
	private void initCmds() {
		if(cmds!=null) {
			synchronized (cmds) {
				if(cmds!=null) {
					Map<String, CmdAdapter> cmdMap = ApplicationContextUtil.getBeans(CmdAdapter.class);
					cmds = cmdMap.values().stream()
						.filter(cmd -> cmd.getClass()!=CmdManageCmd.class)
						.collect(Collectors.toList());
				}
			}
		}
	}
}
