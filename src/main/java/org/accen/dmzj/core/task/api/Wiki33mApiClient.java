package org.accen.dmzj.core.task.api;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.RequestLine;
import feign.Response;
import feign.codec.Decoder;

/**
 * 由<a href="https://wiki.33m.me/%E6%8E%A5%E5%8F%A3/qq">孤狼Wiki-QQ</a>提供的获取qq用户头像的api
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@FeignApi(host = "https://api.33m.me",decoder = Decoder.Default.class)
public interface Wiki33mApiClient {
	/**
	 * 获取群或用户的头像
	 * @param type qtx-群头像  tx2-用户头像
	 * @param num
	 * @return
	 */
	@RequestLine("GET /qq?lx={type}&qq={num}")
	public Response qqImage(String type,String num);
}
