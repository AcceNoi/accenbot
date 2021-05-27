package org.accen.dmzj.core.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
		name="saucenao",
		url="https://saucenao.com")
public interface SaucenaoApiClient {
	/**
	 * 调用<a href="https://saucenao.com/">saucenao.com</a>进行图片检索
	 * @param image
	 * @return
	 */
	@PostMapping(value="/search.php",headers= {"Content-Type: multipart/form-data"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String search(@RequestPart("file") MultipartFile image);
	
	/**
	 * 调用<a href="https://saucenao.com/">saucenao.com</a>进行图片检索
	 * @param image
	 * @return
	 */
	@PostMapping(value="/search.php",headers = {"Content-Type: multipart/form-data"})
	public String search(@RequestParam("url") String url);
}
