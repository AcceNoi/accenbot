package org.accen.dmzj.core.handler.callbacker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CallbackManager {

	@Value("${coolq.bot}")
	private String botId;
	private Map<CallbackListener,List<Qmessage>>  listeners = new ConcurrentHashMap<CallbackListener, List<Qmessage>>();
	
	public void accept(Qmessage qmessage) {
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
}
