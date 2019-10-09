package org.accen.dmzj.web.vo;

import java.util.Date;
/**
 * 对消息快速回复的配置
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class CfgQuickReply {
	private long id;
	private int matchType;//1-Precise精确，2-Fuzzy模糊
	private String pattern;
	private int applyType;//应用类型，1-私信，2-群组，3-讨论组
	private String applyTarget;//应用目标号，例如群号
	private int needAt;//是否需要at，群组和讨论组时有效，1-需要，2-不需要
	private String reply;
	private String creatUserId;
	private Date createTime;
	private int status;//0-无效，1-启用，2-停用
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getMatchType() {
		return matchType;
	}
	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public int getApplyType() {
		return applyType;
	}
	public void setApplyType(int applyType) {
		this.applyType = applyType;
	}
	public String getApplyTarget() {
		return applyTarget;
	}
	public void setApplyTarget(String applyTarget) {
		this.applyTarget = applyTarget;
	}
	public String getCreatUserId() {
		return creatUserId;
	}
	public void setCreatUserId(String creatUserId) {
		this.creatUserId = creatUserId;
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
	public int getNeedAt() {
		return needAt;
	}
	public void setNeedAt(int needAt) {
		this.needAt = needAt;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	
}
