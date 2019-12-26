package org.accen.dmzj.util.render;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
 * 标识该render是可以自定义backgroud的
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface Backgroudable {
	public void setBackgroud(Graphics2D graph);
}
