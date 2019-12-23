package org.accen.dmzj.util.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
	private int horizontalSpace = 5;//水平间隔
//	private boolean verticalAlignment = false;//是否横向对齐，为否则会向左填充
	private int verticalSpace = 5;//竖直间隔
	private int singleWidth=0;//单张图片的宽（统一后的）
	
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
	 * 将会创建一个默认3列，向上填充，左右间隔为5的绘制器
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
			initSingleWidth();
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
			if(singleWidth<=0) {
				this.singleWidth = this.imgs.stream().map(img->img.getWidth()).reduce(Integer::min).get();
				imgs.forEach(img->{
					img.setScale((double)this.singleWidth/(double)img.getWidth());
				});
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
			if(index%colCount==0) {
				//是本行的第一列
				grid.add(index/colCount, new ArrayList<RenderImage>());
			}
			//置入
			grid.get(index/colCount).add(imgs.get(index));
			imgs.get(index).x = cursorX;
			imgs.get(index).y = cursorY;
			//置入后，开始移动游标
			if(index%colCount==colCount-1) {
				//如果是每行最后一个，则游标回车
				cursorX = 0;
				if(horizontalAlignment) {
					//如果水平对齐，y游标则需要加上本行最高的高度
					cursorY += grid.get(index/colCount).stream()
									.map(img->(int)(img.getHeight()*img.getScale()))
									.reduce(Integer::max).get()
								+verticalSpace;
				}else {
					int indexNext = index+1;
					//不是水平对齐，y游标是以本列所有的高度加起来
					cursorY = IntStream.range(0, grid.size())
							.mapToObj(i->{
								RenderImage curImg = grid.get(i).get(indexNext%colCount);
								return (int)(curImg.getHeight()*curImg.getScale());
							})
							.reduce(Integer::sum).get()
							+ (indexNext/colCount)*verticalSpace;
					/*cursorY += grid.stream()
									.map(row->row.get(indexNext%colCount).getHeight())
									.reduce(Integer::sum).get()
								+ (indexNext/colCount)*verticalSpace;*/
				}
			}else {
				//不是每行最后一个，则往后移动
				cursorX += singleWidth+horizontalSpace;
				if(horizontalAlignment||index/colCount==0) {
					//对齐，则y不变
				}else {
					//向上填充
					int indexNext = index+1;
					//不是水平对齐，y游标是以本列所有的高度加起来
					
					cursorY = IntStream.range(0, grid.size()-1)
							.mapToObj(i->{
								RenderImage curImg = grid.get(i).get(indexNext%colCount);
								return (int)(curImg.getHeight()*curImg.getScale());
							})
							.reduce(Integer::sum).get()
							+ (indexNext/colCount)*verticalSpace;
					/*cursorY += grid.stream()
							.map(row->row.get(indexNext%colCount).getHeight())
							.reduce(Integer::sum).get()
							+ (indexNext/colCount)*verticalSpace;*/
					
				}
			}
			
		}
		//2.计算高宽
		int wrapperWidth = singleWidth*colCount+horizontalSpace*(colCount-1);
		//最后的游标纵坐标+最后一行的最高高度
		/*int wrapperHeight = cursorY+grid.get(grid.size()-1).stream()
									.map(img->img.getHeight())
									.reduce(Integer::max).get();*/
		/*int wrapperHeight = grid.parallelStream()
								.map(row->{
									return row.stream().map(cell->cell.getHeight()).reduce(Integer::max).get();
									})
								.reduce(Integer::max).get();*/
		int wrapperHeight = 0;
		if(horizontalAlignment) {
			//水平对齐，每一行最大的高度加起来+留白
			wrapperHeight = grid.parallelStream()
					.map(row->{
						return row.parallelStream()
								.map(cell->{
									return (int)(cell.getHeight()*cell.getScale());
								})
								.reduce(Integer::max).get();
					})
					.reduce(Integer::sum).get();
		}else {
			//向上填充，则是每一列的高度*缩放值+留白（最后一行没有图片的空位也会加上去，但是因为留白远小于图片的高度，所以忽略这个误差）
			wrapperHeight = IntStream.range(0, colCount)
					.mapToObj(colIndex->{
						return grid.parallelStream()
								.map(row->{
									return colIndex<row.size()?(int)((double)row.get(colIndex).getHeight()*row.get(colIndex).getScale()):0;
								})
								.reduce(Integer::sum).get()+(grid.size()-1)*verticalSpace;
					}).reduce(Integer::max).get();
		}
		
		//3.绘制
		BufferedImage wrapper = new BufferedImage(wrapperWidth, wrapperHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D wrapperG = wrapper.createGraphics();
		wrapperG.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);
		wrapperG.setColor(Color.BLACK);
		wrapperG.fillRect(0, 0, wrapper.getWidth(), wrapper.getHeight());
		imgs.forEach(img->{
			img.customBeforeDraw(wrapperG);
			AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(img.getScale(), img.getScale()), null);//等比例缩放
			wrapperG.drawImage(img.getBuffer(), ato, img.x, img.y);
			img.customAfterDraw(wrapperG);
		});
		//4.绘制完毕写入out
		try {
			wrapperG.dispose();
			ImageIO.write(wrapper, "jpg", out);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
}
