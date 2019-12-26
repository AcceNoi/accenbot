package org.accen.dmzj.util.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.io.IOException;
import java.net.URL;

public class PixivUrlRenderImage extends UrlRenderImage {
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
	public void customAfterDraw(Graphics2D graph) {
		Color originColor = graph.getColor();
		Stroke originStroke = graph.getStroke();
		//写字
		/*graph.setColor(Color.RED.darker());
		graph.setFont(new Font("Microsoft Yahei", Font.BOLD, 25));
		graph.drawString("PID："+pid, x+10, y+30);
		graph.drawString("Title："+title, x+10, y+30+30);
		graph.drawString("Author："+author, x+10, y+30+60);*/
		renderTextAndOutline("PID："+pid,graph ,x+10,y+30);
		renderTextAndOutline("Title："+title,graph,x+10, y+30+30);
		renderTextAndOutline("Author："+author,graph,x+10, y+30+60);
		
		graph.setColor(originColor);
		graph.setStroke(originStroke);
	}
	public static void renderTextAndOutline(String text,Font font,Graphics2D graph,float x,float y,Color textColor,Color outlineColor) {
		TextLayout tl = new TextLayout(text, font, graph.getFontRenderContext());
		graph.setColor(textColor);
		tl.draw(graph, x, y);
		Shape s = tl.getOutline(null);
		graph.setStroke(new BasicStroke());
		graph.translate(x, y);
		graph.setColor(outlineColor);
		graph.draw(s);
		graph.translate(-x, -y);
	}
	public static void renderTextAndOutline(float outlineSize,String text,Font font,Graphics2D graph,float x,float y,Color textColor,Color outlineColor) {
		TextLayout tl = new TextLayout(text, font, graph.getFontRenderContext());
		graph.setColor(textColor);
		tl.draw(graph, x, y);
		Shape s = tl.getOutline(null);
		graph.setStroke(new BasicStroke(outlineSize));
		graph.translate(x, y);
		graph.setColor(outlineColor);
		graph.draw(s);
		graph.translate(-x, -y);
	}
	private static void renderTextAndOutline(String text,Graphics2D graph,float x,float y) {
		renderTextAndOutline(text, new Font("Microsoft Yahei", Font.BOLD, 25), graph, x, y, Color.RED.darker(), Color.WHITE);
	}
	public static void renderTextAndOutline(String text,Font font,Graphics2D graph,float x,float y) {
		renderTextAndOutline(0.3f,text, font, graph, x, y, Color.WHITE, Color.BLACK.darker());
	}
}
