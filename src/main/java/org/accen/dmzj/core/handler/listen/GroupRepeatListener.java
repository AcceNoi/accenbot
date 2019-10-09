package org.accen.dmzj.core.handler.listen;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.CqhttpClient;
import org.accen.dmzj.web.dao.CfgListenStatusMapper;
import org.accen.dmzj.web.dao.SysRecordCountMapper;
import org.accen.dmzj.web.vo.CfgListenStatus;
import org.accen.dmzj.web.vo.Qmessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupRepeatListener implements ListenAdpter, ListenStatus {
	@Autowired
	private CfgListenStatusMapper cfgListenStatusMapper;
	@Autowired
	private SysRecordCountMapper sysRecordCountMapper;
	@Autowired
	private CqhttpClient cqhttpClient;
	private final static Logger logger = LoggerFactory.getLogger(GroupRepeatListener.class);
	@Override
	public String name() {
		return "口球模式";
	}

	@Override
	public String nameEn() {
		return "repeation listen mode";
	}

	/**
	 * 复读队列，最多只会保留近若干次消息
	 */
	Map<String,BlockingDeque<Qmessage>> repeatDeques = new HashMap<String, BlockingDeque<Qmessage>>();
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		if(selfQnum.equals(qmessage.getUserId())) {
			return null;//不会禁言自己
		}
		//1.先判断是否开启了口球模式
//		String groupId
		CfgListenStatus status = cfgListenStatusMapper.selectByApplyAndCode("group", qmessage.getGroupId(),code());
		if(status!=null&&"1".equals(status.getListenerStatus())) {
			//开启着
			//2.队列检查
			BlockingDeque<Qmessage> repeatDeque;
			int cap = 2;//队列容量，如果已经慢了，则下次入队时将被认为是将被禁言的复读。可以理解为复读的最大容忍次数
			if(repeatDeques.containsKey(qmessage.getGroupId())) {
				repeatDeque = repeatDeques.get(qmessage.getGroupId());
			}else {
				try {
					cap = Integer.parseInt(status.getListenerStatus2())-1;
					if(cap<=1) {
						throw new NumberFormatException();//懒得区分了
					}
				}catch (NumberFormatException e) {
					logger.warn("Date:{0,date,yyyy-MM-dd HH:mm:ss},Group:{1},Code:REPEAT,未或错误配置复读超时次数，使用默认次数3", new Date(),qmessage.getGroupId());
				}
				
				repeatDeque = new LinkedBlockingDeque<Qmessage>(cap);
				repeatDeques.put(qmessage.getGroupId(), repeatDeque);
			}
			//3.复读检测
			try {
				if(repeatDeque.isEmpty()) {
					repeatDeque.put(qmessage);
					
				}else if(repeatDeque.getLast().getMessage().equals(qmessage.getMessage())){
					//复读了
					if(repeatDeque.size()==cap) {
						//队列满了，禁言
						Map<String, Object> body = new HashMap<String, Object>();
						body.put("group_id", qmessage.getGroupId());
						body.put("user_id", qmessage.getUserId());
						body.put("duration", 3*60);
						cqhttpClient.setGroupBan(body);
					}
				}
			} catch (InterruptedException e) {
			}
		}else {
			//即使没有配置，这里仍旧清理一下复读队列，以免因为缓存出问题
			repeatDeques.remove(qmessage.getGroupId());
		}
		return null;
	}

	@Override
	public String code() {
		return "REPEAT";
	}

}
