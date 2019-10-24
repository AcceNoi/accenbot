package org.accen.dmzj.web.vo;

public class CmdGame {
	private long id;
	private String gameName;
	private int coinConsum;
	private int favLimit;
	private int status;//0-无效 1-启用 2-关闭
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public int getCoinConsum() {
		return coinConsum;
	}
	public void setCoinConsum(int coinConsum) {
		this.coinConsum = coinConsum;
	}
	public int getFavLimit() {
		return favLimit;
	}
	public void setFavLimit(int favLimit) {
		this.favLimit = favLimit;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
