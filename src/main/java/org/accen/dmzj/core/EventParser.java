package org.accen.dmzj.core;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.accen.dmzj.core.annotation.HandlerChain;
import org.accen.dmzj.core.handler.EventHandler;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

@Service
public class EventParser {
	/*上报事件类型*/
	public final static String POST_TYPE_MESSAGE="message";//消息
	public final static String POST_TYPE_NOTICE="notice";//通知
	public final static String POST_TYPE_REQUEST="request";//请求
	public final static String POST_TYPE_META_EVENT="meta_event";//元事件
	/*当上报事件为“消息”时的消息类型*/
	public final static String MESSAGE_TYPE_PRIVATE="private";//私信
	public final static String MESSAGE_TYPE_GROUP="group";//群消息
	public final static String MESSAGE_TYPE_DISCUSS="discuss";//讨论组
	/*当上报事件为“通知”时的通知类型*/
	public final static String NOTICE_TYPE_GROUP_UPLOAD="group_upload";//群文件上传
	public final static String NOTICE_TYPE_GROUP_ADMIN="group_admin";//群管理变动
	public final static String NOTICE_TYPE_GROUP_DECREASE="group_decrease";//群成员减少
	public final static String NOTICE_TYPE_GROUP_INCREASE="group_increase";//群成员新增
	public final static String NOTICE_TYPE_FRIEND_ADD="friend_add";//好友添加
	/*当上报事件为“请求”时的请求类型*/
	public final static String REQUEST_FRIEND="friend";//加好友
	public final static String REQUEST_GROUP="group";//加群请求/邀请
	/*当上报事件为“元事件”时的事件类型*/
	public final static String META_EVENT_LIFECYCLE="lifecycle";//声明周期
	public final static String META_EVENT_HEARTBEAT="heartbeat";//心跳
	
	/*子事件类型*/
	public final static String SUB_TYPE_FRIEND="friend";
	public final static String SUB_TYPE_GROUP="group";
	public final static String SUB_TYPE_DISCUSS="discuss";
	public final static String SUB_TYPE_OTHER="other";
	
	public final static String SUB_TYPE_NORMAL="normal";
	public final static String SUB_TYPE_ANONYMOUS="anonymous";
	public final static String SUB_TYPE_NOTICE="notice";
	
	public final static String SUB_TYPE_SET="set";
	public final static String SUB_TYPE_UNSET="unset";
	
	public final static String SUB_TYPE_LEAVE="leave";
	public final static String SUB_TYPE_KICK="kick";
	public final static String SUB_TYPE_KICK_ME="kick_me";
	
	public final static String SUB_TYPE_APPROVE="approve";
	public final static String SUB_TYPE_INVITE="invite";
	
	public final static String SUB_TYPE_ADD="add";
	
	public final static String SUB_TYPE_ENABLE="enable";
	public final static String SUB_TYPE_DISABLE="disable";
	
	private Map<String, List<EventHandler>> handlerChains = new HashMap<String, List<EventHandler>>();
	private Map<EventHandler,HandlerChain> handlerCfg = new HashMap<EventHandler, HandlerChain>();
	//延迟加载handlerChain
	private void initHandlerChain() {
//		synchronized (handlerChains) {
			if(handlerChains.isEmpty()) {
				synchronized (handlerChains) {
					Map<String, EventHandler> handlerBeans = ApplicationContextUtil.getBeans(EventHandler.class);
					if(handlerChains.isEmpty()) {
//						handlerChains = new HashMap<String, List<EventHandler>>();
//						handlerCfg = new HashMap<EventHandler, HandlerChain>();
						for(String handlerBeanName:handlerBeans.keySet()) {
							EventHandler handlerBean = handlerBeans.get(handlerBeanName);
							Class<? extends EventHandler> beanClazz = handlerBean.getClass();
							HandlerChain anno = beanClazz.getAnnotation(HandlerChain.class);
							
							String postType = anno.postType();
							if(handlerChains.get(postType)==null) {
								handlerChains.put(postType, new LinkedList<EventHandler>());
							}
							handlerChains.get(postType).add(handlerBean);
							handlerCfg.put(handlerBean, anno);
						}
						
						//排序，降序
						for(String postType:handlerChains.keySet()) {
							List<EventHandler> handlerList = handlerChains.get(postType);
							Collections.sort(handlerList, (h1,h2)->handlerCfg.get(h2).order()-handlerCfg.get(h1).order());
						}
					}
				}
			}
//		}
	}
	/**
	 * 解析器允许
	 * @param qnum
	 * @param body
	 * @return
	 */
	public void parse(String qnum,String body) {
		
		initHandlerChain();
		
		GsonBuilder gb = new GsonBuilder()
				.setLongSerializationPolicy(LongSerializationPolicy.STRING);
				
		Gson gson = gb.create();
		
		Map<String, Object> bodyMap = gson.fromJson(body, Map.class);
		bodyMap.put("selfQnum", qnum);
		List<EventHandler> handlerChain = handlerChains.get(bodyMap.get("post_type"));
		//处理链
		if(handlerChain!=null) {
			for(EventHandler handler:handlerChain) {
				handler.handle(bodyMap);
				if(handlerCfg.get(handler).isBlock()) {
					break;
				}
			}
		}
	}
}
