package org.accen.dmzj.util;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {
	/**
	 * 将图片变成灰度
	 * @param originImage
	 * @param newFile
	 */
	public static void toGray(File originFile,File newFile) {
		BufferedImage originImage;
		try {
			originImage = ImageIO.read(originFile);
			BufferedImage newPic = new BufferedImage(originImage.getWidth(), originImage.getHeight(),  
	                BufferedImage.TYPE_3BYTE_BGR);  
	  
	        ColorConvertOp cco = new ColorConvertOp(ColorSpace  
	                .getInstance(ColorSpace.CS_GRAY), null);  
	        cco.filter(originImage, newPic);  
	        try {
				ImageIO.write(newPic, "jpg", newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
