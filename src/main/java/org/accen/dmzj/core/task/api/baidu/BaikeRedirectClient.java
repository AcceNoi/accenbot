package org.accen.dmzj.core.task.api.baidu;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;

@Deprecated
public class BaikeRedirectClient extends Client.Default {

	public BaikeRedirectClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
		super(sslContextFactory, hostnameVerifier);
	}

	@Override
	public Response execute(Request request, Options options) throws IOException {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet getReq = new HttpGet(request.url());
			HttpResponse originResp = client.execute(getReq);
//			Response originResp = super.execute(request, options);
			if(originResp.getStatusLine().getStatusCode()>=300&&originResp.getStatusLine().getStatusCode()<400) {
				//3XX则表示需要进一步操作即重定向
				String newUrl = "https:"+(originResp.getHeaders("Location"))[0].toString();
				//重新定义request
				Request newRequest= Request.create(request.httpMethod(), newUrl, request.headers(), request.body(),request.charset());
				return super.execute(newRequest, options);
			}else {
				return super.execute(request, options);
			}
	}

}
