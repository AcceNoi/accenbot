package org.accen.dmzj.util.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
		graph.setColor(Color.RED);
		graph.setFont(new Font("Microsoft Yahei", Font.BOLD, 15));
		graph.drawString("PID："+pid, x+10, y+20);
		graph.drawString("Title："+title, x+10, y+20+20);
		graph.drawString("Author："+author, x+10, y+20+40);
	}
}
