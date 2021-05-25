package org.accen.dmzj.util;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.handler.cmd.CmdAdapter;
import org.accen.dmzj.web.dao.CfgConfigValueMapper;
import org.accen.dmzj.web.vo.CfgConfigValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
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
	@Value("${coolq.judge.maxpornpro:0.3}")
	private double maxPornPro;//最高能容忍的（不含）
	@Value("${coolq.judge.minnormalpro:0.85}")
	private double minNormalPro;//最低能接受的（含），以上两者加起来必须大于1，否则就没意义
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
		FuncSwitch fs = AnnotationUtils.findAnnotation(cmdClass,FuncSwitch.class);
		if(fs==null) {
			return true;
		}
		CfgConfigValue config = configMapper.selectByTargetAndKey("system", "0", NOT_ALLOW_RFEFIX+fs.name());
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
		return true;
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
