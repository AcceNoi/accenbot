package org.accen.dmzj.web.vo;

import java.util.Date;

public class CmdSvPk {
	private long id;
	private String pkName;
	private String pkAlias;//简写
	private String pkJpName;//日语名称
	private String pkEnName;//英语名称
	private int pkSeq;//弹数，一般认为最高的就是最新的卡包
	private Date createTime;
	private String createUserId;
	private Date updateTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPkName() {
		return pkName;
	}
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}
	public String getPkAlias() {
		return pkAlias;
	}
	public void setPkAlias(String pkAlias) {
		this.pkAlias = pkAlias;
	}
	public String getPkJpName() {
		return pkJpName;
	}
	public void setPkJpName(String pkJpName) {
		this.pkJpName = pkJpName;
	}
	public String getPkEnName() {
		return pkEnName;
	}
	public void setPkEnName(String pkEnName) {
		this.pkEnName = pkEnName;
	}
	public int getPkSeq() {
		return pkSeq;
	}
	public void setPkSeq(int pkSeq) {
		this.pkSeq = pkSeq;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
