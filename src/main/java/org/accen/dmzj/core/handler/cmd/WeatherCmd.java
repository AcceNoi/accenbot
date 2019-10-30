package org.accen.dmzj.core.handler.cmd;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.task.api.JiRenGuApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
	
	private static final Pattern pattern = Pattern.compile("^(.+)?(今天|今日|明天|明日)?天气$");
	
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		String aft = CQUtil.subAtAfter(message, selfQnum);
		if(!StringUtils.isEmpty(aft)) {
			Matcher mt = pattern.matcher(aft.trim());
			if(mt.matches()) {
				String city = mt.group(1);
				Map<String, Object> rs = jiRenGuApiClient.weather(city);
				if((Double)rs.get("error")==0) {
					
					GeneralTask task = new GeneralTask();
					task.setSelfQnum(selfQnum);
					task.setType(qmessage.getMessageType());
					task.setTargetId(qmessage.getGroupId());
					
					Map<String, Object> result = ((Map<String, Object>[])rs.get("results"))[0];
					Map<String, Object>[] weathers  = (Map<String, Object>[]) result.get("weather_data");
					Map<String, Object>[] indexes = (Map<String, Object>[]) result.get("index");
					StringBuffer msgBuff = new StringBuffer(result.get("currentCity").toString());
					if("明日".equals(mt.group(2))||"明天".equals(mt.group(2))) {
						//明日
						msgBuff.append("明天")
								.append(weathers[1].get("date"))
								.append("天气")
								.append(weathers[1].get("weather"))
								.append("温度")
								.append(weathers[1].get("temperature"))
								.append("，")
								.append(weathers[1].get("wind"));
					}else {
						//今日
						msgBuff.append("今天")
								.append(weathers[0].get("date"))
								.append("天气")
								.append(weathers[0].get("weather"))
								.append("温度")
								.append(weathers[0].get("temperature"))
								.append("，")
								.append(weathers[0].get("wind"))
								.append("。")
								.append(indexes[3].get("desc"));
					}
					task.setMessage(msgBuff.toString());
					return task;
				}
			}
		}
		return null;
	}

}
