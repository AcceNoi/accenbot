package org.accen.dmzj.web.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.AccenbotContext;
import org.accen.dmzj.core.AccenbotContext.AccenbotCmdProxy;
import org.accen.dmzj.core.autoconfigure.ContextPostProcessor;
import org.accen.dmzj.core.autoconfigure.ProxyPostProcessor;
import org.accen.dmzj.core.meta.PostType;
import org.accen.dmzj.util.ApproximatelyEqualsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Controller
@RequestMapping("/cmd")
public class CmdController implements ContextPostProcessor,ProxyPostProcessor{
	private Map<String, AccenbotContext> copy = new HashMap<String, AccenbotContext>(4);
	private Map<AccenbotContext,List<AccenbotCmdProxy>> proxyCopies = new HashMap<>(4);
	@Override
	public void afterRegisterContext(PostType postType,AccenbotContext context) {
		copy.put(postType.name(), context);
		proxyCopies.put(context, new LinkedList<>());
	}
	@Override
	public void afterRegisterProxy(AccenbotContext context,AccenbotCmdProxy proxy) {
		proxyCopies.get(context).add(proxy);
	}
	public CmdController(@Autowired @Qualifier("accenbotContext") AccenbotContext accenbotContext) {
		
	}
	
	@GetMapping("/list/{scope}")
	@ResponseBody
	public Map<String,Object> list(@PathVariable("scope")String scope) {
		Map<String,Object> format = new HashMap<String, Object>();
		copy.keySet().parallelStream()
					.forEach(postType->{
						if(scope==null||scope.isBlank()||ApproximatelyEqualsUtil.aequals(scope, postType)) {
							format.put(postType, 
									proxyCopies.get(copy.get(postType))
														.parallelStream()
														.map(proxy->
															Map.of("name", proxy.name()
																	,"anno",proxy.annoClass().getName()
																	,"location",proxy.cmdMethod().toGenericString()
															)
														)
														.collect(Collectors.toList())
							);
						}
					});
		return format;
	}
	@GetMapping("/list")
	@ResponseBody
	public Map<String,Object> list(){
		return list(null);
	}
}
