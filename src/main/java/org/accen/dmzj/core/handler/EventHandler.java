package org.accen.dmzj.core.handler;

import java.util.Map;

public interface EventHandler {
	void handle(Map<String, Object> event);
}
