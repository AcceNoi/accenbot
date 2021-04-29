package org.accen.dmzj.core.handler.cmd;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.core.api.GoogleTranslateApiClient;
import org.accen.dmzj.core.api.YoudaoApiClient;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.GoogleUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@FuncSwitch("cmd_translate")
@Component
public class TranslateCmd implements CmdAdapter{
	
	public class Lang{
		public final static String LANG_CN2EN="ZH_CN2EN";
		public final static String LANG_CN2JA="ZH_CN2JA";
		public final static String LANG_CN2KR="ZH_CN2KR";
		public final static String LANG_CN2FR="ZH_CN2FR";
		public final static String LANG_CN2RU="ZH_CN2RU";
		public final static String LANG_CN2SP="ZH_CN2SP";
		public final static String LANG_EN2CN="EN2ZH_CN"; //英语　»　中文
		public final static String LANG_JA2CN="JA2ZH_CN"; //日语　»　中文
		public final static String LANG_KR2CN="KR2ZH_CN"; //韩语　»　中文
		public final static String LANG_FR2CN="FR2ZH_CN"; //法语　»　中文
		public final static String LANG_RU2CN="RU2ZH_CN"; //俄语　»　中文
		public final static String LANG_SP2CN="SP2ZH_CN"; //
	}
	
	@Autowired
	private YoudaoApiClient youdaoApiClient;
	@Autowired
	private GoogleTranslateApiClient googleTranslateApiClient;
	@Override
	public String describe() {
		return "多语种翻译";
	}

	@Override
	public String example() {
		return "日语说我回来了";
	}

	private final static Pattern pattern = Pattern.compile("^(日语|英语|法语|韩语|俄语|西班牙语|中文){0,1}说(.+)");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String message = qmessage.getMessage().trim();
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			
			GeneralTask task =  new GeneralTask();
			
			task.setSelfQnum(selfQnum);
			task.setType(qmessage.getMessageType());
			task.setTargetId(qmessage.getGroupId());
			
			String langZ = matcher.group(1);
			
			String sl = "auto";
			
			
			String lang = "JA";
			/*switch(langZ) {
			case "日语翻译":lang = Lang.LANG_CN2JA;break;
			case "英语翻译":lang = Lang.LANG_CN2EN;break;
			case "法语翻译":lang = Lang.LANG_CN2FR;break;
			case "韩语翻译":lang = Lang.LANG_CN2KR;break;
			case "俄语翻译":lang = Lang.LANG_CN2RU;break;
			case "西班牙语翻译":lang = Lang.LANG_CN2SP;break;
			default:break;
			}
			String word = matcher.group(2);
			YoudaoTranslateResult result = youdaoApiClient.translate(word, lang);
			if(result.getErrorCode()==0) {
				task.setMessage(CQUtil.at(qmessage.getUserId())+result.getTranslateResult()[0][0].getTgt() +" (kana");
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+ "抱歉，俺太弱了，翻译不出来喵~");
			}*/
			if(langZ!=null) {
				switch (langZ) {
				case "日语":
					lang = "JA";
					break;
				case "英语":
					lang = "EN";
					break;
				case "法语":
					lang = "FR";
					break;
				case "俄语":
					lang = "RU";
					break;
				case "中文":
					lang = "zh_CN";
					break;
				default:
					
					break;
				}
			}
			
			String word = matcher.group(2).trim();
			Map<String, Object> result = googleTranslateApiClient.translate(lang, word,sl);
			
			if(StringUtils.isEmpty(langZ)) {
				//如果是空的，则认为是语音
				//先拿到语种
				String src = (String) result.get("src");
				try {
					String wordU = URLEncoder.encode(word,"UTF-8");
					String token = GoogleUtil.calculate_token(word);
					task.setMessage(CQUtil.recordUrl("http://translate.google.cn/translate_tts?ie=UTF-8&q="+wordU+"&tl="+src+"&total=1&idx=0&textlen=4&tk="+token+"&client=webapp&prev=input"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
//				String token = GoogleUtil.calculate_token(word);
//				task.setMessage(CQUtil.recordUrl("http://translate.google.cn/translate_tts?ie=UTF-8&q="+word+"&tl="+src+"&total=1&idx=0&textlen=4&tk="+token+"&client=webapp&prev=input"));
			}else {
				task.setMessage(CQUtil.at(qmessage.getUserId())+((List<Map<String,Object>>)result.get("sentences")).get(0).get("trans")+" (kana");
			}
			
			return task;
		}
		return null;
	}
	
}
