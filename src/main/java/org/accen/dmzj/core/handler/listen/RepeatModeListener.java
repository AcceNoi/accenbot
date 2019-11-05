package org.accen.dmzj.core.handler.listen;

import java.util.List;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;

public class RepeatModeListener implements ListenAdpter {

	private 
	
	@Override
	public List<GeneralTask> listen(Qmessage qmessage, String selfQnum) {
		// TODO Auto-generated method stub
		return null;
	}

}
class RepeatMsg{
	private String message;
	private String targetType;
	private String targetId;
	public RepeatMsg(String message, String targetType, String targetId) {
		super();
		this.message = message;
		this.targetType = targetType;
		this.targetId = targetId;
	}
	@Override
	public int hashCode() {
		return message.hashCode()+targetType.hashCode()+targetId.hashCode();
	}
}
