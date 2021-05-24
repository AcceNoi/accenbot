package org.accen.dmzj.core.meta;

public enum NoticeSubType {
	_ALL,
	/**
	 * 设置管理员
	 */
	SET,
	/**
	 * 取消管理员
	 */
	UNSET,
	/**
	 * 群成员减少-主动退群
	 */
	LEAVE,
	/**
	 * 群成员减少-成员被踢
	 */
	KICK,
	/**
	 * 群成员减少-登录号被踢
	 */
	KICK_ME,
	/**
	 * 群成员增加-管理员已同意入群
	 */
	APPROVE,
	/**
	 * 群成员增加-管理员邀请入群
	 */
	INVITE,
	/**
	 * 群禁言-禁言
	 */
	BAN,
	/**
	 * 群禁言-结除禁言
	 */
	LIFT_BAN,
	/**
	 * 戳一戳
	 */
	POKE,
	/**
	 * 群红包运气王
	 */
	LUCKY_KING,
	/**
	 * 群成员荣誉变更
	 */
	HONOR
}
