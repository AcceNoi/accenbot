package org.accen.dmzj.core.meta;

public enum NoticeType {
	_ALL,
	/**
	 * 群文件上传
	 */
	GROUP_UPLOAD,
	/**
	 * 群管理变动
	 */
	GROUP_ADMIN,
	/**
	 * 群成员减少
	 */
	GROUP_DEGREASE,
	/**
	 * 群成员增加
	 */
	GROUP_INCREASE,
	/**
	 * 群禁言
	 */
	GROUP_BAN,
	/**
	 * 好友添加
	 */
	FRIEND_ADD,
	/**
	 * 群消息撤回
	 */
	GROUP_RECALL,
	/**
	 * 好友消息撤回
	 */
	FRIEND_RECALL,
	/**
	 * 戳一戳
	 */
	NOTIFY
}
