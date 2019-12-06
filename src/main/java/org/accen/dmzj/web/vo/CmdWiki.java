package org.accen.dmzj.web.vo;

public class CmdWiki {
	private long id;
	private String article;//来源作品
	private String wikiName;//词条名
	private String keywords;//检索词。多个使用,隔开
	private String content;//说明
	private String image;//图片。多个使用,隔开
	private int status;//1-保存，2-审核  3-有效    0-无效
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getArticle() {
		return article;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public String getWikiName() {
		return wikiName;
	}
	public void setWikiName(String wikiName) {
		this.wikiName = wikiName;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
