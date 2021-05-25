package org.accen.dmzj.web.controller;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.accen.dmzj.core.AccenbotContext;
import org.accen.dmzj.core.security.CqhttpSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/event")
public class EventAcceptController {
	@Autowired
	private CqhttpSecurity security;
//	@Autowired
//	private EventParser parser;
	@Autowired
	@Qualifier("accenbotContext")
	AccenbotContext accentBotContext;
	
	@RequestMapping("/accept")
	public void eventAccept(HttpServletRequest request,HttpServletResponse response) {
		String qnum = request.getHeader("X-Self-ID");
		String sig = request.getHeader("X-Signature");
		String body;
		try {
			body = request.getReader().lines().collect(Collectors.joining());
			if(security.checkCqhttp(qnum, sig, body)) {
				//校验通过，将报文交给event模块处理
//				parser.parse(qnum, body);
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> event = mapper.readValue(body, Map.class);
				accentBotContext.accept(event);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
