package org.accen.dmzj.util.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.IOException;
import java.net.URL;

import render.UrlScalebleRenderImage;

public class PixivUrlRenderImage extends UrlScalebleRenderImage {
	private String pid;
	private String title;
	private String author;
	
	public PixivUrlRenderImage(URL url,String pid,String title,String author) throws IOException {
		super(url);
		this.pid = pid;
		this.title = title;
		this.author = author;
	}
	/**
	 * 写完图片后把元信息也一并写上去
	 */
	@Override
	public void afterRenderMe(Graphics2D graph,int x,int y) {
		Color originColor = graph.getColor();
		Stroke originStroke = graph.getStroke();
		//写字
		TextRenderUtil.renderTextAndOutline("PID："+pid,graph ,x+10,y+30);
		TextRenderUtil.renderTextAndOutline("Title："+title,graph,x+10, y+30+30);
		TextRenderUtil.renderTextAndOutline("Author："+author,graph,x+10, y+30+60);
		
		graph.setColor(originColor);
		graph.setStroke(originStroke);
	}
	
}
