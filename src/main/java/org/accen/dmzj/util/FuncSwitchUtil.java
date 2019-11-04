package org.accen.dmzj.util;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.cmd.CmdAdapter;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用来控制功能是否允许通过
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Component
@Transactional
public class FuncSwitchUtil {
	@Autowired
	private CfgConfigValueMapper configMapper;
	@Autowired
	private BaiduAiUtil baiduAiUtil;
	/**
	 * 功能是否被允许的配置key前缀
	 */
	private static final String NOT_ALLOW_RFEFIX = "not_allow_";
	/**
	 * 风纪委员模式，取值有prohibited/strong/normal/allow/invalid
	 * 对应 -禁止（不给使用这个功能） -strong（审核策略比较严格） -normal（正常审核策略） -allow（不审核）-invalid（无效，也就是审核的api挂了）
	 */
	private static final String JUDGE_MODE_PREFIX = "judge_mode_";
	public boolean isCmdPass(Class<? extends CmdAdapter> cmdClass,String targetType,String targetId) {
		
		if(targetType==null||targetId==null) {
			return true;
		}
		FuncSwitch fs = cmdClass.getAnnotation(FuncSwitch.class);
		if(fs==null) {
			return true;
		}
		CfgConfigValue config = configMapper.selectByTargetAndKey("system", "0", NOT_ALLOW_RFEFIX+fs.value());
		if(config==null||!config.getConfigValue().contains(targetType+"_"+targetId)) {
			return true;
		}
		return false;
		
	}
	/**
	 * 判断此图片是否通过审核
	 * @param url
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public boolean isImgReviewPass(String url,String targetType,String targetId) {
		String mode = judgeMode( targetType, targetId);
		if("prohibited".equals(mode)) {
			return false;
		}else if("strong".equals(mode)||"normal".equals(mode)) {
			//这两者会调用百度ai的图像审核api
			JSONObject json = baiduAiUtil.contentCensor(url);
			if(json==null||json.get("error_code")!=null) {
				return true;
			}else {
				int conclusionType = json.getInt("conclusionType");
				if(conclusionType==1||conclusionType==4) {
					return true;//合规
				}else if(conclusionType==3&&"normal".equals(mode)) {
					return true;//疑是，但模式是正常/宽松策略
				}else {
					return false;
				}
			}
		}else {
			return true;
		}
	}
	/**
	 * 获取当前群的风纪模式
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public String judgeMode(String targetType,String targetId) {
		CfgConfigValue config = configMapper.selectByTargetAndKey(targetType, targetId, JUDGE_MODE_PREFIX+"image");
		String mode = "allow";
		if(config!=null) {
			mode = config.getConfigValue();
		}
		return mode;
	}
	/**
	 * 获取当前群的风纪模式
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public String judgeModeCn(String targetType,String targetId) {
		String mode = judgeMode( targetType, targetId);
		switch (mode) {
		case "prohibited":
			return "禁止";
		case "strong":
			return "强力";
		case "normal":
			return "一般";
		case "allow":
			return "宽松";
		case "invalid":
			return "无效";
		default:
			return "未知";
		}
	}
}
