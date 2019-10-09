package org.accen.dmzj.core.task.api;

import java.io.File;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormEncoder;

@FeignApi(host = "http://saucenao.com/",encoder = FormEncoder.class)
public interface SaucenaoApiClient {
	/**
	 * 调用<a href="http://saucenao.com/">saucenao.com</a>进行图片检索
	 * @param image
	 * @return
	 */
	@Headers("Content-Type: multipart/form-data")
	@RequestLine("POST /search.php")
	public String search(@Param("file") File image);
}
