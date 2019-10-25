package dmzjbot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.springframework.util.StringUtils;

public class ImageTest {
	public static void main(String[] args) {
		
	}
	public static String addWaterMark(String src,String word) {
		try {
			Image img = ImageIO.read(new File(src));
			int imgWight = img.getWidth(null);
			int imgHeight = img.getHeight(null);
			BufferedImage bufImg = new BufferedImage(imgWight, imgHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufImg.createGraphics();
			g.drawImage(img, 0, 0, imgWight, imgHeight, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @see <a href="https://my.oschina.net/liangxiao/blog/1539484">参考</a>
	 * @param sourcePath
	 * @param destinyPath
	 * @param radius
	 * @return
	 */
	public static boolean getCircleImage(String sourcePath,String destinyPath ,int radius){
        if(StringUtils.isEmpty(sourcePath) || StringUtils.isEmpty(destinyPath)){
            return false;
        }
        
        long start = System.currentTimeMillis();
        // 获取原图片
        BufferedImage waterImg = null;
        try{
            
          
                InputStream sis = null;
                try{
                    URL sourceUrl = new URL(sourcePath); 
                    sis = sourceUrl.openConnection().getInputStream();
                    waterImg = ImageIO.read(sis);  
                }catch(Exception e){
                    
                }finally{
                    if(sis != null){
                        sis.close();
                    }
                }
            
        }catch(Exception e){
            return false;
        }
        
        //半径有设置时，以设置的半径为主
        int width = waterImg.getWidth();
        int height = waterImg.getHeight();
        int getDiameter = (width < height)?width:height;
        if( radius > 0){
            getDiameter = radius;
        }
        
        //按照要求缩放图片
        BufferedImage tag = new BufferedImage(getDiameter, getDiameter, BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(waterImg, 0, 0,getDiameter, getDiameter, null);
        waterImg = tag;
        
        //生成最终的图片
        boolean rs = false;
        Graphics2D g2 = null;
        try {
            tag = new BufferedImage(getDiameter, getDiameter, BufferedImage.TYPE_INT_ARGB);
            g2 = tag.createGraphics();
            g2.setComposite(AlphaComposite.Src);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getDiameter, getDiameter, getDiameter,getDiameter));
            g2.setComposite(AlphaComposite.SrcAtop);
            g2.drawImage(waterImg, 0, 0, null);  
            int temp = destinyPath.lastIndexOf(".") + 1;
            rs = ImageIO.write(tag,destinyPath.substring(temp), new File(destinyPath));
        } catch (IOException e) {
            
            return false;
        }finally{
            if(g2 != null){
                g2.dispose();
            }
        }
        
        long end = System.currentTimeMillis();
        
        
        return rs;
        
        
    }
}
