package org.accen.dmzj.util.render;

import java.io.File;
import java.io.OutputStream;
/**
 * 绘制器
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public interface Render {
	public void render(File outFile) throws Exception;
}
