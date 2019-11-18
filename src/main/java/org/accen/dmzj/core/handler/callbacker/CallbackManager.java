package org.accen.dmzj.core.handler.callbacker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CallbackManager {

	@Value("${coolq.bot}")
	private String botId;
	private Map<CallbackListener,List<Qmessage>>  listeners = new ConcurrentHashMap<CallbackListener, List<Qmessage>>();
	/**
	 * 常驻的listener,通常由listener自己保存上下文，且不通过callbackManager管理其生命周期。
	 * 例如找图，只会找一次，是可以由callbackManager保存生命周期的，但诸如收藏是由自己管理的（使用cacheMap管理生命周期）,那么它就应该是常驻listener
	 */
	private Set<CallbackListener> residentListeners = new HashSet<CallbackListener>(4);
	
	public void accept(Qmessage qmessage) {
		//1.常驻
		Iterator<CallbackListener> residentListenerIte = residentListeners.iterator();
		while(residentListenerIte.hasNext()) {
			residentListenerIte.next().listen(null, qmessage, botId);
		}
		//2.一般
		Iterator<Entry<CallbackListener,List<Qmessage>>> listenerEntries = listeners.entrySet().iterator();
		while(listenerEntries.hasNext()) {
			Entry<CallbackListener,List<Qmessage>> listenerEntry = listenerEntries.next();
			if(listenerEntry.getValue()==null||listenerEntry.getValue().isEmpty()) {
				listenerEntries.remove();
			}else {
				Iterator<Qmessage> oqmIte = listenerEntry.getValue().iterator();
				while(oqmIte.hasNext()) {
					if(listenerEntry.getKey().listen(oqmIte.next(), qmessage, botId)) {
						oqmIte.remove();
					}
				}
			}
			
		}
	}
	public void addCallbackListener(CallbackListener listener,Qmessage orginQmessage) {
		if(!listeners.containsKey(listener)) {
			listeners.put(listener, new LinkedList<Qmessage>());
		}
		listeners.get(listener).add(orginQmessage);
	}
	public void addResidentListener(CallbackListener listener) {
		residentListeners.add(listener);
	}
}
