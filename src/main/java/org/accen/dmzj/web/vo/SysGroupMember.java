package org.accen.dmzj.web.vo;

import java.util.Date;

public class SysGroupMember {
	private long id;
	private String type;//group\discuss\private
	private String targetId;
	private String userId;
	private int coin;//金币
	private int checkinCount;//签到次数
	private int favorability;//好感度
	private Date createTime;
	private Date lastCheckinTime;//上次签到时间
	private int status;//0-无效 1-有效 2禁用
	private int repeatCount;//复读次数
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getCoin() {
		return coin;
	}
	public void setCoin(int coin) {
		this.coin = coin;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCheckinCount() {
		return checkinCount;
	}
	public void setCheckinCount(int checkinCount) {
		this.checkinCount = checkinCount;
	}
	public int getFavorability() {
		return favorability;
	}
	public void setFavorability(int favorability) {
		this.favorability = favorability;
	}
	public Date getLastCheckinTime() {
		return lastCheckinTime;
	}
	public void setLastCheckinTime(Date lastCheckinTime) {
		this.lastCheckinTime = lastCheckinTime;
	}
	public int getRepeatCount() {
		return repeatCount;
	}
	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
	
}
