package org.accen.dmzj.core.handler;

import java.util.HashSet;
import java.util.Set;

/**
 * 模式切换型的功能
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public abstract class ModeCmd {
	private Set<String> allowGroup = new HashSet<String>(4);
	public void addGroup(String groupId) {
		allowGroup.add(groupId);
	}
	public void removeGroup(String groupId) {
		allowGroup.remove(groupId);
	}
	public abstract boolean modeOpen(String groupId);
	protected boolean hasGroup(String groupId) {
		return allowGroup.contains(groupId);
	}
}
