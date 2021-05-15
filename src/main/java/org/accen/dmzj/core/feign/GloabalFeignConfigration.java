package org.accen.dmzj.core.feign;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import feign.codec.Decoder;

public class GloabalFeignConfigration {
	@Bean
	Decoder feignDecoder() {
		@SuppressWarnings("rawtypes")
		HttpMessageConverter jacksonConverter = new Text2JsonHttpMessageConverter(recordSupportObjectMapper());
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
	}
	/**
	 * 支持record
	 * @return
	 */
	public static ObjectMapper recordSupportObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("serial")
		JacksonAnnotationIntrospector jai = new JacksonAnnotationIntrospector() {
			/**
			 * 重写findImplicitPropertyName以支持record
			 */
			@SuppressWarnings("preview")
			@Override
			public String findImplicitPropertyName(AnnotatedMember m) {
				if(m.getDeclaringClass().isRecord()) {
					if(m instanceof AnnotatedParameter p) {
						return m.getDeclaringClass().getRecordComponents()[p.getIndex()].getName();
					}
				}
				return super.findImplicitPropertyName(m);
			};
		};
		objectMapper.setAnnotationIntrospector(jai);
		
		return objectMapper;
		
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
}
