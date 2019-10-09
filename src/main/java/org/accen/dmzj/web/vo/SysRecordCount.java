package org.accen.dmzj.web.vo;

import java.util.Date;
/**
 * 对业务产生的特殊情况进行记录
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class SysRecordCount {
	private long id;
	private String recordType;//记录类型，如REPEAT
	private String recordTarget;//暂时只用一个字段，但是通常难以用一个字段来确定
	private String recordValue;
	private String attr1;
	private String attr2;
	private Date createTime;
	private String createUserId;
	private Date updateTime;
	private String updateUserId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getRecordTarget() {
		return recordTarget;
	}
	public void setRecordTarget(String recordTarget) {
		this.recordTarget = recordTarget;
	}
	public String getRecordValue() {
		return recordValue;
	}
	public void setRecordValue(String recordValue) {
		this.recordValue = recordValue;
	}
	public String getAttr1() {
		return attr1;
	}
	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}
	public String getAttr2() {
		return attr2;
	}
	public void setAttr2(String attr2) {
		this.attr2 = attr2;
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
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	
}
