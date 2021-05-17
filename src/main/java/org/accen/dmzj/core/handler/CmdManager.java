package org.accen.dmzj.core.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.CmdRegular;
import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.annotation.GeneralMessage;
import org.accen.dmzj.core.handler.cmd.CmdAdapter;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.TaskManager;
import org.accen.dmzj.util.FuncSwitchUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CmdManager{
	
	
	/**
	 * 处理事件的功能
	 */
	@Autowired
	private List<CmdAdapter> allCmds;
	@Autowired
	private FuncSwitchUtil funcSwitchUtil;
	@Autowired
	private TaskManager taskManager;
	/**
	 * 受{@link FuncSwitch}控制的功能
	 */
	Map<String, CmdAdapter> allShowableCmds = new HashMap<>(16);
	@SuppressWarnings("preview")
	public void accept(Qmessage qmessage) {
		allCmds.forEach(cmd->{
			if(funcSwitchUtil.isCmdPass(cmd.getClass(), qmessage.getMessageType(), qmessage.getGroupId())) {
				taskManager.addGeneralTask(cmd.cmdAdapt(qmessage, qmessage.getEvent().get("selfQnum").toString()));
			}
		});
		cmdRegularManager.cmdMethodMap.forEach((name,cmdMethod)->{
			Matcher mt = ((Pattern) cmdMethod[0]).matcher(qmessage.getMessage());
			if(mt.matches()) {
				CmdRegular cr = (CmdRegular) cmdMethod[1];
				Method mtd = (Method) cmdMethod[2];
				Class<?>[] paramTypes = mtd.getParameterTypes();
				Object obj = cmdMethod[3];
				int groupCount = mt.groupCount();
				Object[] params = new Object[groupCount+(cr.qmessageParamIndex()>=0?1:0)+(cr.selfNumParamIndex()>=0?1:0)];
				//先放这俩参数
				if(cr.qmessageParamIndex()>=0) {params[cr.qmessageParamIndex()]=qmessage;}
				if(cr.selfNumParamIndex()>=0) {params[cr.selfNumParamIndex()]=qmessage.getEvent().get("selfQnum").toString();}
				//再顺序放正则式的其他参数
				int paramIndex = 0;
				int groupIndex = 0;
				for(;paramIndex<params.length;paramIndex++) {
					if(params[paramIndex]==null) {
						String groupValue = mt.group(groupIndex);
						//简单格式转换
						if(paramTypes[paramIndex]==byte.class||paramTypes[paramIndex]==Byte.class) {
							params[paramIndex] = Byte.valueOf(groupValue);
						}else if(paramTypes[paramIndex]==int.class||paramTypes[paramIndex]==Integer.class) {
							params[paramIndex] = Integer.valueOf(groupValue);
						}else if(paramTypes[paramIndex]==long.class||paramTypes[paramIndex]==Long.class) {
							params[paramIndex] = Long.valueOf(groupValue);
						}else if(paramTypes[paramIndex]==float.class||paramTypes[paramIndex]==Float.class) {
							params[paramIndex] = Float.valueOf(groupValue);
						}else if(paramTypes[paramIndex]==double.class||paramTypes[paramIndex]==Double.class) {
							params[paramIndex] = Double.valueOf(groupValue);
						}else if(paramTypes[paramIndex]==char.class||paramTypes[paramIndex]==Character.class) {
							params[paramIndex] = groupValue.charAt(0);
						}else {
							params[paramIndex] = groupValue;
						}
					}
				}
				try {
					Object rt = mtd.invoke(obj, params);
					if(rt instanceof GeneralTask task) {
						taskManager.addGeneralTask(task);
					}else if(mtd.isAnnotationPresent(GeneralMessage.class)&&rt instanceof String message) {
						taskManager.addGeneralTaskQuick(
								qmessage.getEvent().get("selfQnum").toString()
								, qmessage.getMessageType()
								, qmessage.getGroupId()
								, message);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}
	@Autowired
	private CmdRegularManager cmdRegularManager;
	
}
