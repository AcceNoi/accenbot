package org.accen.dmzj.core.exception;

public class PixivcAuthInitialExcpetion extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1698518380689364957L;
	public PixivcAuthInitialExcpetion(Throwable e) {
		super(e);
	}
	public PixivcAuthInitialExcpetion(String message,Throwable e) {
		super(message, e);
	}
	public PixivcAuthInitialExcpetion() {
	}
}
