package org.accen.dmzj.web.vo;

import java.util.Date;
/**
 * qq对象的功能状态
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class CfgListenStatus {
	private long id;
	private int applyType;//1-私信，2-群组，3-讨论组
	private int applyTarget;
	private String listenerCode;
	private String listenerStatus;//0-无效，1-开启，2-关闭
	private String listenerStatus2;//与功能相关的数值，在无法仅通过listenerStatus表达时，使用这个字段增强
	private String createUserId;
	private Date createTime;
	private String updateUserId;
	private Date updateTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getApplyType() {
		return applyType;
	}
	public void setApplyType(int applyType) {
		this.applyType = applyType;
	}
	public int getApplyTarget() {
		return applyTarget;
	}
	public void setApplyTarget(int applyTarget) {
		this.applyTarget = applyTarget;
	}
	public String getListenerCode() {
		return listenerCode;
	}
	public void setListenerCode(String listenerCode) {
		this.listenerCode = listenerCode;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getListenerStatus() {
		return listenerStatus;
	}
	public void setListenerStatus(String listenerStatus) {
		this.listenerStatus = listenerStatus;
	}
	public String getListenerStatus2() {
		return listenerStatus2;
	}
	public void setListenerStatus2(String listenerStatus2) {
		this.listenerStatus2 = listenerStatus2;
	}
	
}
