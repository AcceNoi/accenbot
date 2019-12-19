package org.accen.dmzj.core.handler.callbacker;
/**
 * 用于处理程序内部的异步通知
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface AsyncCallback {
	/**
	 * 异步的回调，如果message无法说明，可以使用detail传递，响应地就需要实现了AsyncCallback的类去自己与异步方法沟通（即类型转换）
	 * @param message
	 * @param detail
	 */
	public void callback(String message,Object detail);
}
