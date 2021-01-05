package org.accen.dmzj.util.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.accen.dmzj.web.vo.SysGroupMember;
import org.springframework.util.StringUtils;

public class CheckinRender implements Render,Backgroudable{
	private RenderImage backgroundImg;
	private RenderImage profileImg;
	private String qqName;
	private SysGroupMember mem;
	private Map<String, String> memEnhance;//对mem做的增强，由于mem只能表示当前的状态，无法显示值的变化，使用这个来实现展示
	private String foot;
	@Deprecated
	private String[][] svCompletions;
	private String hitokoto;
	private String hitokotoFrom;
	private final static int WIDTH = 1080;
	private final static int HEIGHT = 1747;
	public CheckinRender(RenderImage background,SysGroupMember mem,String hitokoto,String hitokotoFrom,RenderImage profileImage,String qqName,Map<String, String> memEnhance,String foot) {
		super();
		this.backgroundImg = background;
		this.mem = mem;
		this.hitokoto = hitokoto;
		this.hitokotoFrom = hitokotoFrom;
		this.profileImg = profileImage;
		this.qqName = qqName;
		this.memEnhance = memEnhance;
		this.foot = foot;
	}
	@Override
	public void render(File outFile) throws Exception {
		BufferedImage wrapper = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D wrapperG = wrapper.createGraphics();
		wrapperG.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);
		wrapperG.setColor(Color.BLACK);
		wrapperG.fillRect(0, 0, wrapper.getWidth(), wrapper.getHeight());
		int faceRadius = 240;
		//1.背景
		setBackgroud(wrapperG);
		//2.头
		renderHeader(wrapperG);
		//3.qqname
		renderName(wrapperG);
		//4.绘制标线
		wrapperG.setStroke(new BasicStroke(9));
		wrapperG.setColor(Color.WHITE);
		wrapperG.drawLine((80*2+15*2)*3, 100*3-faceRadius+60*3, (80*2+15*2)*3, 100*3+faceRadius);
		wrapperG.drawLine(100*3-faceRadius+30*3, 100*3+faceRadius+30*3+50*3, 350*3-50*3, 100*3+faceRadius+30*3+50*3);
		//5.绘制mem信息
		renderMem(wrapperG, 200*3, 158*3-faceRadius);
		//6.绘制留言
		renderRemark(wrapperG, 150*3+faceRadius);
		//7.绘制卡包
		//7.绘制一言
		renderHitokoto(wrapperG,208*3+faceRadius);
//		renderPk(wrapperG, 100*3-faceRadius, 100*3+faceRadius, 208*3+faceRadius);
		//8.绘制foot
		renderFoot(wrapperG,850,1160);
		//8.绘制头像

			
		int border = 1*3;
//		int radius = 80;
		Ellipse2D.Double shape = new Ellipse2D.Double(100*3-faceRadius, 100*3-faceRadius+30*3, faceRadius*2-border, faceRadius*2-border);
		wrapperG.clip(shape);
		wrapperG.drawImage(profileImg.getBuffer(), new AffineTransformOp(AffineTransform.getScaleInstance((float)faceRadius/320, (float)faceRadius/320), null), 100*3-faceRadius, 100*3-faceRadius+30*3);

		try {
			wrapperG.dispose();
			ImageIO.write(wrapper, "jpg", outFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setBackgroud(Graphics2D graph) {
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance((float)WIDTH/backgroundImg.getWidth(), (float)WIDTH/backgroundImg.getWidth()), null);//等比例缩放
		graph.drawImage(backgroundImg.getBuffer(), ato, 0, 0);
		
	}
	@Deprecated
	protected void renderProfile(Graphics2D wrapperG) {
		//先把头像裁成160*160
		
		int border = 1;
		int radius = 80;
		Ellipse2D.Double shape = new Ellipse2D.Double(100-radius, 100-radius, radius*2-border, radius*2-border);
		wrapperG.clip(shape);
		wrapperG.drawImage(profileImg.getBuffer(), null, 100-radius, 100-radius);
	}
	protected void renderHeader(Graphics2D wrapperG) {
		int headerSize = 25*3;
		Font font = new Font("微软雅黑", Font.BOLD, headerSize);
		String headerText = "个人档案 No."+mem.getId();
		float x = caculateCenterTextX(headerText,font,0,WIDTH,wrapperG);
		float y = headerSize+5*3;
		renderTextOutline(wrapperG, x, y, font, headerText);
	}
	protected void renderName(Graphics2D wrapperG) {
		Font ft16 = new Font("微软雅黑", Font.BOLD, 16*3);
		String nameText = "【"+qqName+"】";
		float x = caculateCenterTextX(nameText, ft16, 0, WIDTH, wrapperG);
		float y = 50*3;
		renderTextOutline(wrapperG, x, y, ft16, nameText);
	}
	protected void renderTextOutline(Graphics2D wrapperG,float x,float y,Font font,String text) {
		PixivUrlRenderImage.renderTextAndOutline(1f,text,font,wrapperG,x,y,Color.WHITE, Color.BLACK);
	}
	protected void renderMem(Graphics2D wrapperG,float x,float y) {
		Font ft18 = new Font("微软雅黑", Font.BOLD, 18*3);
		FontMetrics fm = wrapperG.getFontMetrics(ft18);
		int textLength = fm.stringWidth("签到次数");
		renderTextOutline(wrapperG, x, y, ft18, "金币");
		renderTextOutline(wrapperG, x+textLength+5*3, y, ft18, ""+mem.getCoin()+((memEnhance==null||memEnhance.get("coin")==null)?"":"("+memEnhance.get("coin")+")"));
		renderTextOutline(wrapperG, x, y+28*3, ft18, "好感度");
		renderTextOutline(wrapperG, x+textLength+5*3, y+28*3, ft18, ""+mem.getFavorability()+((memEnhance==null||memEnhance.get("fav")==null)?"":"("+memEnhance.get("fav")+")"));
		renderTextOutline(wrapperG, x, y+28*3+28*3, ft18, "签到次数");
		renderTextOutline(wrapperG, x+textLength+5*3, y+(28+28)*3, ft18, ""+mem.getCheckinCount()+((memEnhance==null||memEnhance.get("checkin")==null)?"":"("+memEnhance.get("checkin")+")"));
		renderTextOutline(wrapperG, x, y+(28+28+28)*3, ft18, "复读次数");
		renderTextOutline(wrapperG, x+textLength+5*3, y+(28+28+28)*3, ft18, ""+mem.getRepeatCount()+((memEnhance==null||memEnhance.get("repeat")==null)?"":"("+memEnhance.get("repeat")+")"));
		renderTextOutline(wrapperG, x, y+(28+28+28+28)*3, ft18, "卡券");
		renderTextOutline(wrapperG, x+textLength+5*3, y+(28+28+28+28)*3, ft18, ""+mem.getCardTicket()+((memEnhance==null||memEnhance.get("ticket")==null)?"":"("+memEnhance.get("ticket")+")"));
	}
	protected void renderRemark(Graphics2D wrapperG,float y) {
		Font ft16 = new Font("微软雅黑", Font.BOLD, 16*3);
		String remark = StringUtils.isEmpty(mem.getRemark())?"↑这个人很懒，还没有设置留言":mem.getRemark();
		float x = caculateCenterTextX(remark, ft16, 0, WIDTH, wrapperG);
		renderTextOutline(wrapperG, x, y, ft16, remark);
	}
	private final int hitokotoLineWordCount = 15;
	protected void renderHitokoto(Graphics2D wrapperG,float y) {
		Font ft18 = new Font("微软雅黑", Font.BOLD, 18*3);
		int oneLineLength = ft18.getSize()*hitokotoLineWordCount;
		int x = (WIDTH-oneLineLength)/2;
		for(int line = 0;line<=this.hitokoto.length()/hitokotoLineWordCount;line++) {
			String ph =  hitokotoLineWordCount*(line+1)>(this.hitokoto.length()+1)?this.hitokoto.substring(hitokotoLineWordCount*line):this.hitokoto.substring(hitokotoLineWordCount*line,hitokotoLineWordCount*(line+1));
			renderTextOutline(wrapperG, x, y+ft18.getSize()*line, ft18, ph);
		}
		Font ft12 = new Font("微软雅黑", Font.BOLD, 12*3);
		String from = "——"+this.hitokotoFrom+"  ";
		FontMetrics fm = wrapperG.getFontMetrics(ft12);
		renderTextOutline(wrapperG, WIDTH-fm.stringWidth(from), y+ft18.getSize()*(this.hitokoto.length()/hitokotoLineWordCount+1), ft12, from);
	}
	@Deprecated
	protected void renderPk(Graphics2D wrapperG,float x1,float x2,float y) {
		Font ft16 = new Font("微软雅黑", Font.BOLD, 16*3);
		FontMetrics fm = wrapperG.getFontMetrics(ft16);
		int textLength = fm.stringWidth("钢铁的反叛者");
		for(int i=0;i<svCompletions.length;i++) {
			renderTextOutline(wrapperG, i%2==0?x1:x2, y+28*(i/2)*3, ft16, svCompletions[i][0]);
			renderTextOutline(wrapperG, (i%2==0?x1:x2)+textLength+10*3, y+28*(i/2)*3, ft16, svCompletions[i][1]);
		}
	}
	protected void renderFoot(Graphics2D wrapperG,float x,float y) {
		Font ft8 = new Font("微软雅黑", Font.BOLD, 8*3);
		renderTextOutline(wrapperG, x, y, ft8, foot);
	}
	/**
	 * 计算文字水平居中时的x坐标
	 * @param text
	 * @param font
	 * @param limitLeft
	 * @param limitRight
	 * @param wrapperG
	 * @return
	 */
	private static float caculateCenterTextX(String text,Font font,float limitLeft,float limitRight,Graphics2D wrapperG) {
		FontMetrics fm = wrapperG.getFontMetrics(font);
		int textLength = fm.stringWidth(text);
		return (limitRight-limitLeft-textLength)/2+limitLeft;
	}
	protected void renderLine(Graphics2D wrapperG,int x1,int y1,int x2,int y2) {
		wrapperG.setStroke(new BasicStroke(3));
		wrapperG.setColor(Color.WHITE);
		wrapperG.drawLine(x1, y1, x2, y2);
	}
}
