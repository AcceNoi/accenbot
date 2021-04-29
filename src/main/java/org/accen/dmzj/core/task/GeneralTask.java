package org.accen.dmzj.core.task;

public class GeneralTask {
	private String type;
	private String targetId;
	private String message;
	private String selfQnum;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSelfQnum() {
		return selfQnum;
	}
	public void setSelfQnum(String selfQnum) {
		this.selfQnum = selfQnum;
	}
	public GeneralTask(String type, String targetId, String message, String selfQnum) {
		super();
		this.type = type;
		this.targetId = targetId;
		this.message = message;
		this.selfQnum = selfQnum;
	}
	public GeneralTask() {
		super();
	}
	
}
