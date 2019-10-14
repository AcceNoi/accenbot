package org.accen.dmzj.core.task.api.vo;

public class BilibliVideoInfo {
	private long aId;
	private String title;
	private long postTime;
	public long getaId() {
		return aId;
	}
	public void setaId(long aId) {
		this.aId = aId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getPostTime() {
		return postTime;
	}
	public void setPostTime(long postTime) {
		this.postTime = postTime;
	}
	public BilibliVideoInfo(long aId, String title, long postTime) {
		super();
		this.aId = aId;
		this.title = title;
		this.postTime = postTime;
	}
	public BilibliVideoInfo() {
		super();
	}
	
}
