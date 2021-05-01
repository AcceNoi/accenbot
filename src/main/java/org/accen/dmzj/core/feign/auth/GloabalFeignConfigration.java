package org.accen.dmzj.core.feign.auth;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import feign.codec.Decoder;

@Configuration
public class GloabalFeignConfigration {
	Decoder feignDecoder() {
		@SuppressWarnings("rawtypes")
		HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(recordSupportObjectMapper());
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
			public String findImplicitPropertyName(com.fasterxml.jackson.databind.introspect.AnnotatedMember m) {
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
}
