package org.accen.dmzj.util.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class UrlRenderImage extends RenderImage {
	private URL imageUrl;
	private BufferedImage buffImage;
	public UrlRenderImage(URL url) throws IOException {
		this.imageUrl = url;
		InputStream is = imageUrl.openStream();
		buffImage = ImageIO.read(is);
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
