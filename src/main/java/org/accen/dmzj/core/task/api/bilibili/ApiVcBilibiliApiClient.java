package org.accen.dmzj.core.task.api.bilibili;

import java.util.Map;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Param;
import feign.RequestLine;
/**
 * 用来获取B站动态的API，up主不管发文章还是视频，都会已动 态的形式呈现
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignApi(host = "https://api.vc.bilibili.com")
public interface ApiVcBilibiliApiClient { 
	/**
	 * 获取up动态
	 * @param visitorUid 参观者uid（貌似没啥用，b站不知道有没有私密动态的说法）
	 * @param hostUid up uid
	 * @param offsetDynamicId 起始id（不含）
	 * @return 主要解析card字段的json，和desc-timestamp 注意这个timestamp需要*1000才是UNIX时间戳<br>
	 * type 8:视频 <br> 2：普通动态<br> 64：专栏<br>  1：转发动态<br> 4：普通动态（但是解析同转发动态，不知道有什么区别）<br>256： 音乐<br>512：番剧<br>16：小视频（估计还有16,32，128暂时没看出来，）
	 */
	@RequestLine("GET /dynamic_svr/v1/dynamic_svr/space_history?visitor_uid={visitorUid}&host_uid={hostUid}&offset_dynamic_id={offsetDynamicId}")
	public Map<String, Object> dynamic(@Param("visitorUid")String visitorUid,@Param("hostUid")String hostUid,@Param("offsetDynamicId")long offsetDynamicId);
}
