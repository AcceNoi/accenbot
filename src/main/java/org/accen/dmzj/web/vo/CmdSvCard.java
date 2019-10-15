package org.accen.dmzj.web.vo;

import java.util.Date;

public class CmdSvCard {
	private long id;
	private long pkId;
	private String cardName;
	private String cardNameJp;
	private String career;//职业
	private int cardRarity;//稀有度 1-铜 2-银 3-金 4-虹 5-异画
	private double probability;//抽取概率
	private Date createTime;
	private String createUserId;
	private int status;//1-有效 2-无效
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPkId() {
		return pkId;
	}
	public void setPkId(long pkId) {
		this.pkId = pkId;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardNameJp() {
		return cardNameJp;
	}
	public void setCardNameJp(String cardNameJp) {
		this.cardNameJp = cardNameJp;
	}
	public int getCardRarity() {
		return cardRarity;
	}
	public void setCardRarity(int cardRarity) {
		this.cardRarity = cardRarity;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCareer() {
		return career;
	}
	public void setCareer(String career) {
		this.career = career;
	}
	
}
