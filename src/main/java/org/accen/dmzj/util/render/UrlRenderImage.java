package org.accen.dmzj.util.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class UrlRenderImage extends RenderImage {
	private URL imageUrl;
	private BufferedImage buffImage;
	public UrlRenderImage(URL url) throws IOException {
		this.imageUrl = url;
//		InputStream is = imageUrl.openStream();
		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
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
