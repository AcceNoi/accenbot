package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.StringUtil;
import org.accen.dmzj.web.dao.CfgQuickReplyMapper;
import org.accen.dmzj.web.vo.CfgQuickReply;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
@FuncSwitch("cmd_msg_search")
@Component
@Transactional
public class FuzzyMsgSearch implements CmdAdapter {
	@Autowired
	private CfgQuickReplyMapper cfgQuickReplyMapper;
	@Override
	public String describe() {
		return "查询一条已存在的词条";
	}

	@Override
	public String example() {
		return "查看词条1";
	}
	
	@Value("${coolq.fuzzymsg.list.pageSize:5}")
	private int listSize;
	
	
	private final static Pattern pattern = Pattern.compile("^查看词条(\\d+)$");
	private final static Pattern cttPattern = Pattern.compile("^查询(精确){0,1}词条(.+)$");
	private final static Pattern myPattern = Pattern.compile("^我的词条(\\d*)$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		Matcher cttMatcher = cttPattern.matcher(message);
		Matcher myMatcher = myPattern.matcher(message);
		
		GeneralTask task = new GeneralTask();
		task.setSelfQnum(selfQnum);
		task.setType(qmessage.getMessageType());
		task.setTargetId(qmessage.getGroupId());
		if(matcher.matches()) {
			
			CfgQuickReply reply = cfgQuickReplyMapper.selectById(Long.parseLong(matcher.group(1)));
			if(reply==null||reply.getStatus()!=1) {
				task.setMessage("无法找到此词条，请确认词条编号喵~");
			}else if(!qmessage.getGroupId().equals(reply.getApplyTarget())) {
				task.setMessage("非本群词条喵~");
			}else {
				task.setMessage("词条"+reply.getId()+"为[问"+reply.getPattern()+"答"+reply.getReply().replaceAll("\\[CQ:record,file=.*?\\]", "[语音]")+"]喵~");
			}
			return task;
		}else if(cttMatcher.matches()) {
			
			/*String pageNoStr = cttMatcher.group(4);
			int pageNo = StringUtils.isEmpty(pageNoStr)?1:Integer.parseInt(pageNoStr);
			int offset = (pageNo-1)*listSize;
			*/
			String pt = null;
			if(StringUtils.isEmpty(cttMatcher.group(1))) {
				//模糊
				pt = ".*?"+cttMatcher.group(2)+".*";
			}else {
				//精确
				pt = cttMatcher.group(2);
			}
//			List<CfgQuickReply> replys = cfgQuickReplyMapper.queryByTargetAndPattern( qmessage.getGroupId(), pt,offset,listSize);
			List<CfgQuickReply> replys = cfgQuickReplyMapper.queryByTargetAndPattern( qmessage.getGroupId(), pt);
			if(replys!=null&&!replys.isEmpty()) {
				StringBuffer msgBuf = new StringBuffer("本群");
				msgBuf.append(StringUtils.isEmpty(cttMatcher.group(1))?"":"精确")
						.append("词条[")
						.append(cttMatcher.group(2))
						.append("]为：\n");
				
				listShow(replys, msgBuf);
				
//				int maxPage = (cfgQuickReplyMapper.queryCountByTargetAndPattern(qmessage.getGroupId(), pt)-1)/listSize+1;
//				pageShow(maxPage, msgBuf);
//				msgBuf.append("\n"+StringUtil.SPLIT_FOOT);
//				msgBuf.append("\nTips:发送 查询(精确)词条+[词条问题]+翻页[分页]即可查看其他分页的词条喵~");
				task.setMessage(msgBuf.toString());
				
				return task;
			}else {
				task.setMessage("无法找到该词条喵~");
				return task;
			}
		}else if(myMatcher.matches()) {
			String pageNoStr = myMatcher.group(1);
			int pageNo = StringUtils.isEmpty(pageNoStr)?1:Integer.parseInt(pageNoStr);
			int offset = (pageNo-1)*listSize;
			
			List<CfgQuickReply> replys = cfgQuickReplyMapper.queryByCreator(qmessage.getGroupId(), qmessage.getUserId(), offset, listSize);
			if(replys!=null&&!replys.isEmpty()) {
				StringBuffer msgBuf = new StringBuffer(CQUtil.at(qmessage.getUserId())+" 您在本群的词条["+pageNo+"]为：\n");
				listShow(replys, msgBuf);
				int maxPage = (cfgQuickReplyMapper.queryCountByCreator(qmessage.getGroupId(), qmessage.getUserId())-1)/listSize+1;
				pageShow(maxPage, msgBuf);
				msgBuf.append("\n"+StringUtil.SPLIT_FOOT);
				msgBuf.append("Tips:发送 我的词条+[ 分页]即可查看其他分页的词条喵~");
				task.setMessage(msgBuf.toString());
				
				return task;
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+"无法找到您的词条喵~");
				return task;
			}
			
		}
		return null;
	}

	/**
	 * 分页展示词条
	 * @param replys
	 * @param msgBuf
	 */
	private void listShow(List<CfgQuickReply> replys,StringBuffer msgBuf) {
		if(replys!=null&&!replys.isEmpty()) {
			for(int index = 0;index<replys.size();index++) {
				msgBuf.append(index+1+".[")
						.append(replys.get(index).getId())
						.append("]\n")
						.append(replys.get(index).getApplyType()==2?"问":"精确问")
						.append(replys.get(index).getPattern().replaceAll("\\.\\*\\?", "").replaceAll("\\.\\*", ""))
						.append("答")
						.append(replys.get(index).getNeedAt()==1?"回复":"")
						.append(replys.get(index).getReply().replaceAll("\\[CQ:record,file=.*?\\]", "[语音]"))
						.append("\n"+StringUtil.SPLIT);
				
			}
		}
		
	}
	/**
	 * 显示下面的分页帮助
	 * @param maxPage
	 * @param msgBuf
	 */
	public void pageShow(int maxPage,StringBuffer msgBuf ) {
		if(maxPage>5) {
			//大于5，则中间以省略号展示
			msgBuf.append("[1] [2]···["+(maxPage-1)+"] ["+maxPage+"]");
		}else {
			//小于等于5，就全部展示了
			for(int i=1;i<=maxPage;i++) {
				msgBuf.append("["+i+"]");
				if(i<maxPage) {
					msgBuf.append(" ");
				}
			}
		}
	}
}
