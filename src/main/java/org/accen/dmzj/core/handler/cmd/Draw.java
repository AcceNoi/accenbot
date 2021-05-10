package org.accen.dmzj.core.handler.cmd;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.autoconfigure.Workdirer;
import org.accen.dmzj.core.handler.group.Default;
import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.accen.dmzj.web.vo.Qmessage;
import org.springframework.stereotype.Component;

import com.madgag.gif.fmsware.GifDecoder;
@FuncSwitch(groupClass = Default.class, title = "抽签")
@Component
public class Draw implements CmdAdapter,Workdirer{
	@Override
	public String workdir() {
		return "drawSource/";
	}

	private final static Pattern DRAW_PATTERN = Pattern.compile("^(.+)?抽签$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		String msg = qmessage.getMessage();
		Matcher drawMt = DRAW_PATTERN.matcher(msg);
		if(drawMt.matches()) {
			String sign = drawMt.group(1);
			File gif = new File(workdir()+sign+".gif");
			if(gif.exists()) {
				
				GifDecoder gifd =  new GifDecoder();
				try(InputStream is = new FileInputStream(gif);){
					gifd.read(is);
					int count = gifd.getFrameCount();
					int rdm = RandomUtil.randomInt(count);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					BufferedImage rmdBfdImg = gifd.getFrame(rdm);
					ImageIO.write(rmdBfdImg, "png", os);
					os.flush();
					String rmdBs64 = Base64.getEncoder().encodeToString(os.toByteArray());
					GeneralTask task = new GeneralTask(qmessage.getMessageType(), qmessage.getGroupId()
							, CQUtil.at(qmessage.getUserId())+" "+CQUtil.imageBs64(rmdBs64), selfQnum);
					return task;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return null;
	}


}
