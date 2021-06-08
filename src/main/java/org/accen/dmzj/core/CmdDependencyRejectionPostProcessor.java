package org.accen.dmzj.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;
import org.accen.dmzj.core.annotation.DependMode;
import org.accen.dmzj.core.annotation.Dependency;
import org.accen.dmzj.core.annotation.Rejection;
import org.accen.dmzj.core.autoconfigure.EventCmdPostProcessor;
import org.accen.dmzj.core.autoconfigure.EventPostProcessor;
import org.accen.dmzj.core.exception.DependencyRejectionException;
import org.springframework.stereotype.Component;

/**
 * 处理Cmd之间的依赖和排斥关系
 * @author <a href="1339liu@gmail.com">Accen</a>
 * @since 2.2
 */
@Component
public class CmdDependencyRejectionPostProcessor implements EventPostProcessor,EventCmdPostProcessor{
	private static final String CMD_RECORD_KEY = "_CMD_RECORD";
	@Override
	public void beforeEventPost(Map<String, Object> event) {
		event.put(CMD_RECORD_KEY, new HashSet<String>());
	}
	@Override
	public void afterEventPostSuccess(Map<String, Object> event,AccenbotContext context) {
		removeRecordKey(event);
	}
	@Override
	public void afterEventPostFaild(Map<String,Object> event) {
		removeRecordKey(event);
	}
	private void removeRecordKey(Map<String,Object> event) {
		event.remove(CMD_RECORD_KEY);
	}
	
	/**
	 * 在cmd执行前，检查依赖和排斥是否通过
	 */
	@Override
	public boolean beforeEventCmdPost(AccenbotCmdProxy proxy,Map<String, Object> event) {
		if(!event.containsKey(CMD_RECORD_KEY)) {
			throw new DependencyRejectionException("DependencyRejection cmd record未初始化或已删除！");
		}
		@SuppressWarnings("unchecked")
		Set<String> record = (Set<String>) event.get(CMD_RECORD_KEY);
		if(proxy.cmdMethod().getDeclaringClass().isAnnotationPresent(Dependency.class)) {
			Dependency d = proxy.cmdMethod().getDeclaringClass().getDeclaredAnnotation(Dependency.class);
			if(!dependencyPass(d.value(), d.dependMode(), record)) {
				return false;
			}
		}
		if(proxy.cmdMethod().getDeclaringClass().isAnnotationPresent(Rejection.class)) {
			Rejection r = proxy.cmdMethod().getDeclaringClass().getDeclaredAnnotation(Rejection.class);
			if(!rejectionPass(r.value(), r.rejectMode(), record)) {
				return false;
			}
		}
		if(proxy.cmdMethod().isAnnotationPresent(Dependency.class)) {
			Dependency d = proxy.cmdMethod().getDeclaredAnnotation(Dependency.class);
			if(!dependencyPass(d.value(), d.dependMode(), record)) {
				return false;
			}
		}
		if(proxy.cmdMethod().isAnnotationPresent(Rejection.class)) {
			Rejection r = proxy.cmdMethod().getDeclaredAnnotation(Rejection.class);
			if(!rejectionPass(r.value(), r.rejectMode(), record)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 记录改event被cmd处理的情况
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object afterEventCmdPost(AccenbotCmdProxy proxy,Map<String, Object> event,Object invokeResult) {
		if(!event.containsKey(CMD_RECORD_KEY)) {
			throw new DependencyRejectionException("DependencyRejection cmd record未初始化或已删除！");
		}
		((Set<String>) event.get(CMD_RECORD_KEY)).add(proxy.name());
		return invokeResult;
	}
	
	private boolean dependencyPass(String[] value,DependMode mode,Set<String> record) {
		if(value.length==0) {
			return true;
		}
		return switch (mode) {
		case ANY -> Arrays.stream(value).anyMatch(v -> record.contains(v));
		case ALL -> Arrays.stream(value).allMatch(v -> record.contains(v));
		};
	}
	private boolean rejectionPass(String[] value,DependMode mode,Set<String> record) {
		if(value.length==0) {
			return true;
		}
		return switch (mode) {
		case ANY -> Arrays.stream(value).allMatch(v -> !record.contains(v));
		case ALL -> Arrays.stream(value).anyMatch(v -> !record.contains(v));
		};
	}
	
}

