package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.NoticeEventHandler;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.timer.ReportTimeSchedule;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@FuncSwitch("cmd_manage")
@Lazy
@Component
public class CmdManageCmd implements CmdAdapter {

	@Autowired
	private ReportTimeSchedule rtc;
	@Autowired
	private TriggerProSwitchCmd tpsc;
	@Autowired
	private RepeatModeSwitchCmd rmsc;
	
	@Override
	public String describe() {
		return "展示所有的功能";
	}

	@Autowired
	private FuncSwitchUtil funcSwitchUtil;
	
	@Autowired
	private CfgConfigValueMapper configMapper;
	
	@Value("${coolq.welcom.maxlength:15}")
	private int welcomLength;
	
	@Override
	public String example() {
		return "";
	}
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		//直接做个简单的
//		initCmds();
		
		String message = qmessage.getMessage().trim();
		GeneralTask task = new GeneralTask();
		task.setSelfQnum(selfQnum);
		task.setType(qmessage.getMessageType());
		task.setTargetId(qmessage.getGroupId());
		StringBuffer func = new StringBuffer();
		switch (message) {
		case "/功能":
			func.append("#功能#\n")
				.append(StringUtil.SPLIT)
				.append("1. /搜图\n")
				.append(StringUtil.SPLIT)
				.append("2. /词条\n")
				.append(StringUtil.SPLIT)
				.append("3. /音乐\n")
				.append(StringUtil.SPLIT)
				.append("4. /抽卡\n")
				.append(StringUtil.SPLIT)
				.append("5. /提醒与复读\n")
				.append(StringUtil.SPLIT)
				.append("6. /百科\n")
				.append(StringUtil.SPLIT)
				.append("7. /订阅\n")
				.append(StringUtil.SPLIT)
				.append("8. /系统");
			break;
		case "/搜图":
			func.append("#搜图#\n")
				.append(StringUtil.SPLIT)
				.append("1. 查找图片>>发送【老婆找图+[图片]】 \n")
				.append(StringUtil.SPLIT)
				.append("2. 随机图片>>发送【随机瑟图】");
			break;
		case "/词条":
			func.append("#词条#\n")
				.append(StringUtil.SPLIT)
				.append("1. 新增词条>>发送【添加(精确)问[问题]答(回复)[回复的内容]】\n")
				.append(StringUtil.SPLIT)
				.append("2. 删除词条>>发送【删除词条+[要删除的词条编号]】\n")
				.append(StringUtil.SPLIT)
				.append("3. 查看词条>>发送【查看词条+[想查看的词条编号]】\n")
				.append(StringUtil.SPLIT)
				.append("4. 查询词条>>发送【查询(精确)词条+[词条问题]】\n")
				.append(StringUtil.SPLIT)
				.append("5. 我的词条>>发送【我的词条】");
			break;
		case "/音乐":
			func.append("#音乐#\n")
				.append(StringUtil.SPLIT)
				.append("1. 点歌>>发送【网易点歌+[歌曲名称]】\n")
				.append(StringUtil.SPLIT)
				.append("2. B站点歌>>发送【B站点歌+[歌曲名称]】\n")
				.append(StringUtil.SPLIT)
				.append("3. B站歌曲列表>>发送【B站歌曲列表】\n")
				.append(StringUtil.SPLIT)
				.append("4. B站歌曲投稿（暂不稳定）>>发送【抽取B站https://www.bilibili.com/video/av+{avid}从dd:dd:dd到dd:dd:dd音乐，设置名称xxx】\n")
				.append(StringUtil.SPLIT)
				.append("5. B站语音投稿>>发送【抽取B站https://www.bilibili.com/video/av+{avid}从dd:dd:dd到dd:dd:dd语音，设置名称xxx】");
			break;
		case "/抽卡":
			func.append("#抽卡#\n")
				.append(StringUtil.SPLIT)
				.append("1. 影之诗抽卡>>发送【影之诗(十连)抽卡】\n")
				.append(StringUtil.SPLIT)
				.append("2. 东方Project抽卡>>>发送【东方(单抽|十连|翻牌)】\n")
				.append(StringUtil.SPLIT)
				.append("3. 东方Project图鉴>>发送【我的图鉴】\n")
				.append(StringUtil.SPLIT)
				.append("4. 现支持的影之诗卡包有[森罗咆哮]、[荣耀再临]、[钢铁的反叛者]、[扭曲次元]。其他卡包 敬请期待...");
			break;
		case "/提醒与复读":
			func.append("#提醒&复读#\n")
				.append(StringUtil.SPLIT)
				.append("1. 定时提醒>>发送【老婆\\d小时\\d分钟\\d秒后提醒我+[要提醒的事]】\n")
				.append(StringUtil.SPLIT)
				.append("2. 定时发送>>发送【老婆\\d小时\\d分钟\\d秒后说+[要发送的消息]】\n")
				.append(StringUtil.SPLIT)
				.append("3. 复读消息>>发送【老婆说+[要复读的消息]】\n")
				.append(StringUtil.SPLIT)
				.append("4. 翻译>>发送【(日语|英语|俄语|法语|西班牙语)说+[待翻译的内容]】\n")
				.append(StringUtil.SPLIT)
				.append("5. 语音复读>>发送【说+[要复读的消息]】");
			break;
		case "/百科":
			func.append("#百科#\n")
				.append(StringUtil.SPLIT)
				.append("1. 百科>>发送【了解+[想了解的内容]】");
			break;
		case "/订阅":
			func.append("#订阅#\n")
				.append(StringUtil.SPLIT)
				.append("1. 订阅B站UP主>>发送【订阅B站UP+[要订阅的Up]】\n")
				.append(StringUtil.SPLIT)
				.append("2. 查看我的订阅>>发送【我的订阅】\n")
				.append(StringUtil.SPLIT)
				.append("3. 取消订阅>>发送【取消订阅B站UP+[Up主名字或前面的id]】\n")
				.append(StringUtil.SPLIT)
				.append("4. 订阅成功后会推送up的动态、视频以及专栏，其他功能敬请期待...");
			break;
		case "/系统":
			String clock = rtc.getClock(qmessage.getGroupId());
			CfgConfigValue increaseNotice = configMapper.selectByTargetAndKey(qmessage.getMessageType(), qmessage.getGroupId(), NoticeEventHandler.REPLY_GROUP_INCREASE);
			func.append("#系统#\n")
				.append(StringUtil.SPLIT)
				.append("1. 开启报时>>发送【开启(晓|响|吹雪)报时】\n")
				.append(StringUtil.SPLIT)
				.append("2. 关闭报时>>发送【关闭报时】\n")
				.append(StringUtil.SPLIT)
				.append("3. 设置词条触发几率>>发送【设置几率d%】\n")
				.append(StringUtil.SPLIT)
				.append("4. 复读模式>>发送【(开启|关闭)复读模式】\n")
				.append(StringUtil.SPLIT)
				.append("5. 新人加群欢迎>>发送【设置欢迎词+[欢迎词]】（不要超过"+welcomLength+"个汉字哦）\n")
				.append("\n")
				.append("当前群状态:\n")
				.append("报时：           "+(clock==null?"未开启":clock)+"\n")
				.append("词条触发率：  "+tpsc.triggerPro(qmessage.getGroupId())+"%\n")
				.append("风纪模式:       "+funcSwitchUtil.judgeModeCn(qmessage.getMessageType(), qmessage.getGroupId())+"\n")
				.append("复读模式:       "+(rmsc.modeOpen(qmessage.getGroupId())?"开启\n":"关闭\n"))
				.append("加群欢迎： "+(increaseNotice==null?"未设置":increaseNotice.getConfigValue())+"\n")
				.append(StringUtil.SPLIT_FOOT)
				.append("Copyright クロノス/Accen\n")
				.append(StringUtil.SPLIT_FOOT);
			break;	
		default:
			break;
		}
		
		/*if("/功能".equals(message)) {
			
			/*func.append("1. 搜图 发送【老婆找图+[图片]】 \n")
				.append("2. 翻译 发送【日语|英语|俄语|法语|西班牙语说+[要翻译的内容]】\n")
				.append("3. 百科 发送【了解+[要百科的内容]】\n")
				.append("4. 随机图片 发送【随机瑟图】\n")
				.append("5. 点歌 发送【网易点歌+[歌名]】\n")
				.append("6. 定时提醒 发送【老婆xx小时xx分钟xx秒后提醒我+[需要提醒的事]】\n")
				.append("7. 抽卡 发送【影之诗抽卡+[对应的卡包]】\n")
				.append("8. 复读 发送【老婆说+[想复读的内容]】\n")
				.append("9. 语音复读 发送【说+[想复读的内容]】\n")
				.append("10. 词条 发送【添加[精确]问xx答[回复]xx】");*/
			
		if("".equals(func.toString().trim())) {
			return null;
			
		}else {
			task.setMessage(func.toString().trim());
			return task;
		}
		
		
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
