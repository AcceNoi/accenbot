package org.accen.dmzj.core.api.vo;

public class YoudaoTranslateResult {
	private String type;
	private int errorCode;
	private int elapsedTime;
	private Result[][] translateResult;
	public class Result{
		private String src;
		private String tgt;
		public String getSrc() {
			return src;
		}
		public void setSrc(String src) {
			this.src = src;
		}
		public String getTgt() {
			return tgt;
		}
		public void setTgt(String tgt) {
			this.tgt = tgt;
		}
		
	}
	public int getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public Result[][] getTranslateResult() {
		return translateResult;
	}
	public void setTranslateResult(Result[][] translateResult) {
		this.translateResult = translateResult;
	}
	
}
