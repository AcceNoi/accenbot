package org.accen.dmzj.core.handler.cmd;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.JiRenGuApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@FuncSwitch("cmd_weather")
@Component
public class WeatherCmd implements CmdAdapter {

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String example() {
		// TODO Auto-generated method stub
		return null;
	}

	@Autowired
	private JiRenGuApiClient jiRenGuApiClient;
	
	private static final Pattern pattern = Pattern.compile("^(.+)?(今天|今日|明天|明日)天气$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		String aft = CQUtil.subAtAfter(message, selfQnum);
		if(!StringUtils.isEmpty(aft)) {
			Matcher mt = pattern.matcher(aft.trim());
			if(mt.matches()) {
				String city = mt.group(1);
				Map<String, Object> rs = jiRenGuApiClient.weather(city);
				if((Integer)rs.get("error")==0) {
					
					GeneralTask task = new GeneralTask();
					task.setSelfQnum(selfQnum);
					task.setType(qmessage.getMessageType());
					task.setTargetId(qmessage.getGroupId());
					
					Map<String, Object> result = ((List<Map<String, Object>>)rs.get("results")).get(0);
					List<Map<String, Object>> weathers  = (List<Map<String, Object>>) result.get("weather_data");
					List<Map<String, Object>> indexes = (List<Map<String, Object>>) result.get("index");
					StringBuffer msgBuff = new StringBuffer(result.get("currentCity").toString());
					if("明日".equals(mt.group(2))||"明天".equals(mt.group(2))) {
						//明日
						msgBuff.append("明天")
								.append(weathers.get(1).get("date"))
								.append("天气")
								.append(weathers.get(1).get("weather"))
								.append("温度")
								.append(weathers.get(1).get("temperature"))
								.append("，")
								.append(weathers.get(1).get("wind"));
					}else {
						//今日
						msgBuff.append("今天")
								.append(weathers.get(0).get("date"))
								.append("天气")
								.append(weathers.get(0).get("weather"))
								.append("温度")
								.append(weathers.get(0).get("temperature"))
								.append("，")
								.append(weathers.get(0).get("wind"))
								.append("。")
								.append(indexes.get(3).get("des"));
					}
					task.setMessage(msgBuff.toString());
					return task;
				}
			}
		}
		return null;
	}

}
