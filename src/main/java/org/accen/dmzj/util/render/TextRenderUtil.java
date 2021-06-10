package org.accen.dmzj.util.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;

public class TextRenderUtil {
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
	public static void renderTextAndOutline(String text,Graphics2D graph,float x,float y) {
		renderTextAndOutline(text, new Font("Microsoft Yahei", Font.BOLD, 25), graph, x, y, Color.RED.darker(), Color.WHITE);
	}
	public static void renderTextAndOutline(String text,Font font,Graphics2D graph,float x,float y) {
		renderTextAndOutline(0.3f,text, font, graph, x, y, Color.WHITE, Color.BLACK.darker());
	}
}
