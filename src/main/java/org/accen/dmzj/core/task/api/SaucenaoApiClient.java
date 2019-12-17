package org.accen.dmzj.core.task.api;

import java.io.File;

import org.accen.dmzj.core.annotation.FeignApi;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.codec.StringDecoder;
import feign.form.FormEncoder;

@FeignApi(host = "https://saucenao.com/",encoder = FormEncoder.class,decoder = StringDecoder.class)
public interface SaucenaoApiClient {
	/**
	 * 调用<a href="https://saucenao.com/">saucenao.com</a>进行图片检索
	 * @param image
	 * @return
	 */
	@Headers("Content-Type: multipart/form-data")
	@RequestLine("POST /search.php")
	public String search(@Param("file") File image);
	
	/**
	 * 调用<a href="https://saucenao.com/">saucenao.com</a>进行图片检索
	 * @param image
	 * @return
	 */
	@Headers("Content-Type: multipart/form-data")
	@RequestLine("POST /search.php")
	public String search(@Param("url") String url);
}
