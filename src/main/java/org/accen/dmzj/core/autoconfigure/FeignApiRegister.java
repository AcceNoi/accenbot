package org.accen.dmzj.core.autoconfigure;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.accen.dmzj.core.annotation.FeignApi;
import org.accen.dmzj.core.task.api.PixivApiClientInitBefore;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import feign.Client;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

@Component
public class FeignApiRegister implements BeanFactoryPostProcessor { // 扫描的接口路径
	private String scanPath = "org.accen.dmzj.core.task.api";

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		ClassInfoList feignClassInfo = scan(scanPath);
		if(feignClassInfo==null) {
			return;
		}
		feignClassInfo.forEach(feignClass->{
			String url = feignClass.loadClass().getAnnotation(FeignApi.class).host();
			if(!(url.startsWith("http://")||url.startsWith("https://"))) {
				url = "http://"+url;
			}
			String auth = null;
			Class<? extends FeignApiInitBefore> clazz = feignClass.loadClass().getAnnotation(FeignApi.class).before();
			try {
				if(clazz.getConstructors()!=null&&clazz.getConstructors().length>0) {
					FeignApiInitBefore before = (FeignApiInitBefore) clazz.getConstructors()[0].newInstance(null);
					Object result = before.before(feignClass.loadClass(), feignClass.loadClass().getAnnotation(FeignApi.class));
					if(before instanceof PixivApiClientInitBefore) {
						auth = (String) result;
					}
				}
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			
			Feign.Builder builder = getFeignBuilder(feignClass.loadClass().getAnnotation(FeignApi.class).encoder()
					,feignClass.loadClass().getAnnotation(FeignApi.class).decoder()
					,feignClass.loadClass().getAnnotation(FeignApi.class).client()
					,feignClass.loadClass().getAnnotation(FeignApi.class).maxPeriod()
					,feignClass.loadClass().getAnnotation(FeignApi.class).maxAttempts());
			if(auth!=null) {
				final String auth2 = auth;
				builder.requestInterceptor(template->template.header("authorization",auth2));
			}
			beanFactory.registerSingleton(feignClass.getName(), builder.target(feignClass.loadClass(), url));
		});
	}

	public Feign.Builder getFeignBuilder(){
		return getFeignBuilder(GsonEncoder.class,GsonDecoder.class,Client.Default.class,5000,3);
	}
	public Feign.Builder getFeignBuilder(Class<? extends Encoder> encoderClass
			,Class<? extends Decoder> decoderClass
			,Class<? extends Client> clientClass
			,long maxPeriod
			,int maxAttempts) {
		Feign.Builder builder = null;
		try {
			builder = Feign.builder()
					.encoder(encoderClass.getDeclaredConstructor().newInstance())
					.client(clientClass.getDeclaredConstructor(SSLSocketFactory.class,HostnameVerifier.class).newInstance(null,null))
					.decoder(decoderClass.getDeclaredConstructor().newInstance())
					.options(new Request.Options(1000, 3500)).retryer(new Retryer.Default(5000, maxPeriod, maxAttempts));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return builder;
	}

	/**
	 * @see <a gref="https://github.com/classgraph/classgraph/wiki/Code-examples">https://github.com/classgraph/classgraph/wiki/Code-examples</a>
	 * @param path
	 * @return
	 */
	public ClassInfoList scan(String path) {
		try(ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(path).scan()){
			ClassInfoList checkedClasses = scanResult.getAllClasses()
		            .filter(classInfo -> classInfo.hasAnnotation("org.accen.dmzj.core.annotation.FeignApi"));
			return checkedClasses;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
