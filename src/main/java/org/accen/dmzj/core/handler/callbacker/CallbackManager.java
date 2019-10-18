package org.accen.dmzj.core.handler.callbacker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CallbackManager {

	@Value("${coolq.bot}")
	private String botId;
	private Map<CallbackListener,Qmessage>  listeners = new HashMap<CallbackListener, Qmessage>();
	
	public void accept(Qmessage qmessage) {
		Iterator<CallbackListener> listenerKeys = listeners.keySet().iterator();
		while(listenerKeys.hasNext()) {
			CallbackListener listener = listenerKeys.next();
			if(listener.listen(listeners.get(listener),qmessage,botId)) {
				listenerKeys.remove();
			}
		}
	}
	public void addCallbackListener(CallbackListener listener,Qmessage orginQmessage) {
		listeners.put(listener,orginQmessage);
	}
}
