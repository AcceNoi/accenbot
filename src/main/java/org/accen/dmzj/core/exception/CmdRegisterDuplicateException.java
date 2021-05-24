package org.accen.dmzj.core.exception;

import java.lang.reflect.Method;

public class CmdRegisterDuplicateException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2340303086887140181L;
	public CmdRegisterDuplicateException(String cmdName,Class<?> sourceClass,Method sourceMethod) {
		super("Cmd register duplicate 错误：%s，位置：%s#%s".formatted(cmdName,sourceClass.getName(),sourceMethod.getName()));
	}
}
