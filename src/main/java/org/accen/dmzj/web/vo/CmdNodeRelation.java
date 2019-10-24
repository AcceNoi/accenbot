package org.accen.dmzj.web.vo;

public class CmdNodeRelation {
	private long id;
	private long pNodeId;
	private long cNodeId;
	private String checkNo;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getpNodeId() {
		return pNodeId;
	}
	public void setpNodeId(long pNodeId) {
		this.pNodeId = pNodeId;
	}
	public long getcNodeId() {
		return cNodeId;
	}
	public void setcNodeId(long cNodeId) {
		this.cNodeId = cNodeId;
	}
	public String getCheckNo() {
		return checkNo;
	}
	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}
	
}
