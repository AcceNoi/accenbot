package org.accen.dmzj.core.task.api.vo;

public class ImageResult {
	private String similarity;//相似度
	private String title;
	private String content;//具体来源如Pixiv ID: 76742665及画师
	private boolean isSuccess = false;//是否检索成功
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getSimilarity() {
		return similarity;
	}
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
