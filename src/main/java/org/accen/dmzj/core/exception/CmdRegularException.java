package org.accen.dmzj.core.exception;

public class CmdRegularException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2376131230342453625L;
	public CmdRegularException(String cmdRegularName) {
		super("CmdRegular配置错误！cmdRegularName：{}，当enableAutowiredParam设置为false时，不允许使用AutowiredParam进行匹配！");
	}
}
