package org.accen.dmzj.core.exception;

import java.lang.reflect.Field;

public class DataNeverInitedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataNeverInitedException() {
		super("Data never be inited!");
	}
	public DataNeverInitedException(Object obj,Field field) {
		super("Data never be inited! >> "+obj.toString()+" : "+field.getName());
	}
	
}
