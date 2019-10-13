package org.accen.dmzj.web.vo;

import java.util.Date;

public class CmdBuSub {
	private long id;
	private String type;//group\discuss\private
	private String targetId;
	private String subscriber;
	private String subTarget;//订阅源的地址，展示只支持bilibili.com
	private String subType;//订阅类型，暂时支持up，番剧
	private String subObj;//订阅目标的唯一标识 例如up主id
	private String subObjMark;//唯一标识的补充说明 例如up主名字
	private Date subTime;//订阅时间
	private String status;//1-有效 2-无效
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
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	public String getSubTarget() {
		return subTarget;
	}
	public void setSubTarget(String subTarget) {
		this.subTarget = subTarget;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getSubObj() {
		return subObj;
	}
	public void setSubObj(String subObj) {
		this.subObj = subObj;
	}
	public Date getSubTime() {
		return subTime;
	}
	public void setSubTime(Date subTime) {
		this.subTime = subTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSubObjMark() {
		return subObjMark;
	}
	public void setSubObjMark(String subObjMark) {
		this.subObjMark = subObjMark;
	}
	
}
