package org.accen.dmzj.util.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UrlRenderImage extends RenderImage {
	private URL imageUrl;
	private BufferedImage buffImage;
	private static SSLContext sc ;
	static {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{
				    new X509TrustManager() {
				        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				            return null;
				        }
				        public void checkClientTrusted(
				            java.security.cert.X509Certificate[] certs, String authType) {
				        }
				        public void checkServerTrusted(
				            java.security.cert.X509Certificate[] certs, String authType) {
				        }
				    }
				};
			sc = SSLContext.getInstance("TLSv1.2");
			try {
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public UrlRenderImage(URL url) throws IOException {
		this.imageUrl = url;
//		InputStream is = imageUrl.openStream();
		URLConnection conn = null;
		if("HTTPS".equalsIgnoreCase(this.imageUrl.getProtocol())) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			conn = (HttpsURLConnection) imageUrl.openConnection();
		}else if("HTTP".equalsIgnoreCase(this.imageUrl.getProtocol())){
			conn = (HttpURLConnection) imageUrl.openConnection();
		}
		
		/*conn.setConnectTimeout(20*1000);
		conn.setReadTimeout(20*1000);*/
		buffImage = ImageIO.read(conn.getInputStream());
	}
	@Override
	public BufferedImage getBuffer() {
		return buffImage;
	}

	@Override
	public int getWidth() {
		return buffImage.getWidth();
	}

	@Override
	public int getHeight() {
		return buffImage.getHeight();
	}

}
