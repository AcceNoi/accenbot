package org.accen.dmzj.core.api;

import java.util.Collections;
import java.util.List;

import org.accen.dmzj.core.api.vo.Music163Result;
import org.accen.dmzj.core.feign.auth.GloabalFeignConfigration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Param;
import feign.codec.Decoder;
@FeignClient(
		name="music163",
		url="http://music.163.com",
		configuration=Text2JsonFeignConfigration.class)
public interface MusicApiClient {
	/**
	 * 网易云音乐搜索api
	 * @param s 搜索的内容
	 * @param offset 偏移（分页）
	 * @param limit 数量
	 * @param type 1-歌曲，10-专辑，100-歌手，1000-歌单，1002-歌手，1004-mv，1006-歌词，1009电台
	 * @return
	 */
	@GetMapping("/api/search/get/?type=1")
	public Music163Result music163Search(@RequestParam("s")String s,@RequestParam("offset")int offset,@RequestParam("limit")int limit,@Param("type")int type);
}
@Configuration
class Text2JsonFeignConfigration{
	@Bean
	public Decoder textPlainDecoder() {
		return  new SpringDecoder(()->new HttpMessageConverters(new Text2JsonHttpMessageConverter(GloabalFeignConfigration.recordSupportObjectMapper())));
	}
}
class Text2JsonHttpMessageConverter extends MappingJackson2HttpMessageConverter{
	
	public Text2JsonHttpMessageConverter() {
		super();
	}

	public Text2JsonHttpMessageConverter(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
		super.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
	}
}