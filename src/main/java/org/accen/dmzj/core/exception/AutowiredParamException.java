package org.accen.dmzj.core.exception;

public class AutowiredParamException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AutowiredParamException() {
		
	}
	public AutowiredParamException(String regularName ,String autowiredParamName) {
		super("AutowiredParam配置失败！regularName：%s，paramName：%s".formatted(regularName,autowiredParamName));
	}
}
