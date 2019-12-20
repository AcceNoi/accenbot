package org.accen.dmzj.util.render;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class RenderImage implements Scaleable{
	public int x;//左上角x坐标
	public int y;//左上角y坐标
	/**
	 * 允许自由使用graph再去做自定义，例如添加文字、水印等等(可能会被bufferImage覆盖，建议不要使用此方法)
	 * @param graph
	 */
	protected void customBeforeDraw(Graphics2D graph) {};
	/**
	 * 允许自由使用graph再去做自定义，例如添加文字、水印等等
	 * @param graph
	 */
	protected void customAfterDraw(Graphics2D graph) {};
	public abstract BufferedImage getBuffer();

	public abstract int getWidth() ;

	public abstract int getHeight() ;
	
	private double scale;
	@Override
	public double getScale() {
		return scale;
	}
	@Override
	public void setScale(double scale) {
		this.scale = scale;
	}
	
}
