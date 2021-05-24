package org.accen.dmzj.core.meta;

public enum MessageSubType {
	_ALL,
	/**
	 * 私聊-好友消息
	 */
	FRIEND,
	/**
	 * 私聊-群临时会话
	 */
	GROUP,
	/**
	 * 私聊-其他
	 */
	OTHER,
	/**
	 * 群聊-正常
	 */
	NORMAL,
	/**
	 * 群聊-匿名
	 */
	ANONYMOUS,
	/**
	 * 群聊-系统提示
	 */
	NOTICE;
}
