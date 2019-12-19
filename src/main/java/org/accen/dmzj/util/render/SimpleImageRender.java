package org.accen.dmzj.util.render;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.accen.dmzj.core.exception.DataNeverInitedException;
/**
 * 一个简单的图片绘制器实现
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class SimpleImageRender implements Render{
	private List<? extends RenderImage> imgs;
	private int colCount = 3;
	private boolean horizontalAlignment = false;//是否水平对齐，为否则会向上填充
	private int horizontalSpace = 0;//水平间隔
//	private boolean verticalAlignment = false;//是否横向对齐，为否则会向左填充
	private int verticalSpace = 0;//竖直间隔
	private int singleWidth;//单张图片的宽（统一后的）
	
	public SimpleImageRender(int colCount,boolean horizontalAlignment,int horizontalSpace
							,int verticalSpace) {
		super();
		this.colCount = colCount;
		this.horizontalAlignment = horizontalAlignment;
		this.horizontalSpace = horizontalSpace;
//		this.verticalAlignment = verticalAlignment;
		this.verticalSpace = verticalSpace;
	}
	public SimpleImageRender(int colCount,boolean horizontalAlignment,int horizontalSpace
			,int verticalSpace,int singleWidth) {
		super();
		this.colCount = colCount;
		this.horizontalAlignment = horizontalAlignment;
		this.horizontalSpace = horizontalSpace;
//		this.verticalAlignment = verticalAlignment;
		this.verticalSpace = verticalSpace;
		this.singleWidth = singleWidth;
}
	
	
	/**
	 * 将会创建一个默认3列，向上填充，无间隔的绘制器
	 */
	public SimpleImageRender() {
		super();
	}
	
	
	@Override
	public void render(File outFile) throws  DataNeverInitedException {
		if(imgs==null) {
			try {
				throw new DataNeverInitedException(this, SimpleImageRender.class.getDeclaredField("imgs"));
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			} 
		}else if (imgs.isEmpty()) {
			return;
		}else {
			renderImg(outFile);
			
		}
		
	}
	public List<? extends RenderImage> getImgs() {
		return imgs;
	}
	public void setImgs(List<? extends RenderImage> imgs) {
		this.imgs = imgs;
		
	}
	/**
	 * 初始化singleWidth，这里提供的是使用最小的width，这意味着，其他的的图片会被剪切或缩小
	 * @throws DataNeverInitedException
	 */
	protected void initSingleWidth() throws DataNeverInitedException {
		if(imgs==null) {
			try {
				throw new DataNeverInitedException(this, SimpleImageRender.class.getDeclaredField("imgs")); 
			}catch(NoSuchFieldException e) {
				e.printStackTrace();
			}catch(SecurityException e) {
				e.printStackTrace();
			}
			
		}else {
			if(singleWidth<0) {
				this.singleWidth = this.imgs.stream().map(img->img.getWidth()).reduce(Integer::min).get();
			}
			
		}
	}
	/**
	 * 对list中的img按照从左到右从上到下排列
	 * @param out
	 */
	protected void renderImg(File out) {
		//1.格式化成二维数组结构，
		ArrayList<ArrayList<RenderImage>> grid = new ArrayList<ArrayList<RenderImage>>();
		//游标，用于协助初始化renderImage的左上角坐标
		int cursorX = 0;
		int cursorY = 0;
		for(int index=0;index<imgs.size();index++) {
			if(grid.get(index/colCount)==null) {
				grid.add(index/colCount, new ArrayList<RenderImage>());
			}
			//置入
			grid.get(index/colCount).add(index%colCount, imgs.get(index));
			imgs.get(index).x = cursorX;
			imgs.get(index).y = cursorY;
			//置入后，开始移动游标
			if(index%colCount==colCount-1) {
				//如果是每行最后一个，则游标回车
				cursorX = 0;
				if(horizontalAlignment) {
					//如果水平对齐，y游标则需要加上本行最高的高度
					cursorY += grid.get(index/colCount).stream()
									.map(img->img.getHeight())
									.reduce(Integer::max).get()
								+verticalSpace;
				}else {
					int indexNext = index+1;
					//不是水平对齐，y游标是以本列所有的高度加起来
					cursorY += grid.stream()
									.map(row->row.get(indexNext%colCount).getHeight())
									.reduce(Integer::sum).get()
								+ (indexNext/colCount)*verticalSpace;
				}
			}else {
				//不是最后一个，则往后移动
				cursorX += singleWidth+horizontalSpace;
				if(horizontalAlignment) {
					//对齐，则y不变
				}else {
					//向上填充
					int indexNext = index+1;
					//不是水平对齐，y游标是以本列所有的高度加起来
					cursorY += grid.stream()
									.map(row->row.get(indexNext%colCount).getHeight())
									.reduce(Integer::sum).get()
								+ (indexNext/colCount)*verticalSpace;
				}
			}
			
		}
		//2.计算高宽
		int wrapperWidth = singleWidth*colCount+horizontalSpace*(colCount-1);
		//最后的游标纵坐标+最后一行的最高高度
		int wrapperHeight = cursorY+grid.get(grid.size()-1).stream()
									.map(img->img.getHeight())
									.reduce(Integer::max).get();
		//3.绘制
		BufferedImage wrapper = new BufferedImage(wrapperWidth, wrapperHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D wrapperG = wrapper.createGraphics();
		imgs.forEach(img->{
			img.customBeforeDraw(wrapperG);
			wrapperG.drawImage(img.getBuffer(), null, img.x, img.y);
			img.customAfterDraw(wrapperG);
		});
		//4.绘制完毕写入out
		try {
			ImageIO.write(wrapper, "jpg", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		wrapperG.dispose();
	}
}
