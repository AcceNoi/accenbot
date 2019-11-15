package org.accen.dmzj.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class QRCodeUtil {
	public static String decode(String filePath) {
		try {
			BufferedImage img = ImageIO.read(new File(filePath));
			LuminanceSource source = new BufferedImageLuminanceSource(img);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	        HashMap<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
	        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
	        Result result = new MultiFormatReader().decode(bitmap, hints);
	        return result.getText();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
