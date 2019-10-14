package org.accen.dmzj.core.task.api.baidu;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;

public class BaikeRedirectClient extends Client.Default {

	public BaikeRedirectClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
		super(sslContextFactory, hostnameVerifier);
	}

	@Override
	public Response execute(Request request, Options options) throws IOException {
			Response originResp = super.execute(request, options);
			if(originResp.status()>=300&&originResp.status()<400) {
				//3XX则表示需要进一步操作即重定向
				String newUrl = "https:"+(originResp.headers().get("Location").toArray())[0].toString();
				//重新定义request
				Request newRequest= Request.create(request.httpMethod(), newUrl, request.headers(), request.body(),request.charset());
				return super.execute(newRequest, options);
			}else {
				return originResp;
			}
	}

}
