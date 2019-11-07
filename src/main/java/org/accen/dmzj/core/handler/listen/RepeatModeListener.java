package org.accen.dmzj.core.handler.listen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.accen.dmzj.core.handler.cmd.RepeatModeSwitchCmd;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RepeatModeListener implements ListenAdpter {

	//群复读检测 targetType_targetId
	private Map<String, RepeatMsg> repeatMap = new HashMap<String, RepeatMsg>();
	//复读计数器
	private Map<RepeatMsg,Integer> repeatCounter = new HashMap<RepeatMsg, Integer>();
	
	//触发复读时的次数，也就是说，如果连续发送triggerTime次同样的信息，就会触发复读
	@Value("${coolq.repeatmode.time:2}")
	private int triggerTime;
	@Autowired
	private RepeatModeSwitchCmd repeatModeSwitchCmd;
	@Autowired
	private TaskManager taskManager;
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		if(repeatModeSwitchCmd.modeOpen(qmessage.getGroupId())){
			//开启着在
			//转换成repeatMsg
			RepeatMsg repeatMsg = new RepeatMsg(qmessage);
			if(repeatMap.containsKey(qmessage.getMessageType()+"_"+qmessage.getGroupId())) {
				if(repeatCounter.containsKey(repeatMsg)) {
					//上一条与本条信息是同一条（内容一致）
					int lastCount = repeatCounter.get(repeatMsg);
					if(lastCount==triggerTime-1) {
						//算上这次，就达到了临界值，注意是刚好达到而不是超过
						//复读，由于要求实时性，所以通过taskManager去发送
						repeatCounter.put(repeatMsg, triggerTime);
						taskManager.addGeneralTaskQuick(selfQnum, qmessage.getMessageType(), qmessage.getGroupId(), qmessage.getMessage());
						return null;
					}else {
						//还未到临界值或者超过，则+1，但不发送消息
						repeatCounter.put(repeatMsg, lastCount+1);
						return null;
					}
				}else {
					//消息不同，则清理掉上次的消息
					RepeatMsg lastRepeatMsg = repeatMap.get(qmessage.getMessageType()+"_"+qmessage.getGroupId());
					repeatCounter.remove(lastRepeatMsg);
					//赋上新的
					repeatMap.put(qmessage.getMessageType()+"_"+qmessage.getGroupId(), repeatMsg);
					repeatCounter.put(repeatMsg, 1);
					return null;
				}
			}else {
				//没有此群的复读记录，其实就是初始化
				repeatMap.put(qmessage.getMessageType()+"_"+qmessage.getGroupId(), repeatMsg);
				repeatCounter.put(repeatMsg, 1);
				return null;
			}
		}
		return null;
	}

}
class RepeatMsg{
	private String message;
	private String targetType;
	private String targetId;
	
	public RepeatMsg(Qmessage qmessage) {
		super();
		this.message = qmessage.getMessage();
		this.targetType = qmessage.getMessageType();
		this.targetId = qmessage.getGroupId();
	}
	public RepeatMsg(String message, String targetType, String targetId) {
		super();
		this.message = message;
		this.targetType = targetType;
		this.targetId = targetId;
	}
	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
	    sb.append(message);
	    sb.append(targetType);
	    sb.append(targetId);
	    char[] charArr = sb.toString().toCharArray();
	    int hash = 0;
	    
	    for(char c : charArr) {
	        hash = hash * 131 + c;
	    }
	    return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null) {
			return false;
		}
		if(obj instanceof RepeatMsg) {
			RepeatMsg objr = (RepeatMsg) obj;
			if(this.message==null||this.targetType==null||this.targetType==null) {
				return false;
			}else if(this.message.equals(objr.message)&&this.targetType.equals(objr.targetType)&&this.targetId.equals(objr.targetId)){
				return true;
			}
		}
		return false;
	}
}
