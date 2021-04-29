package org.accen.dmzj.core.timer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.exception.DataNeverInitedException;
import org.accen.dmzj.core.handler.callbacker.AsyncCallback;
import org.accen.dmzj.core.api.pixivc.PixivicApiClient;
import org.accen.dmzj.util.render.PixivUrlRenderImage;
import org.accen.dmzj.util.render.SimpleImageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Prank {
	@Value("${sys.static.html.upload}")
	private String rankImageTempHome ;

	private static final String tempDir = "prank/";
	@Autowired
	private PixivicApiClient pixivicApiClient ;
	/**
	 * 获取p站排行榜
	 * @param curFactDate 当前实际的日期
	 * @param rankType 榜单的类型
	 * @param offset 偏移，例如昨日就偏移1，前日就偏移2
	 * @param page 分页，支持1~9
	 * @param callback 由于获取榜单比较耗时，使用callback异步通知处理情况 {@link AsyncCallback}
	 * @return
	 */
	public String rank(LocalDate curFactDate,RankType rankType,int offset,int page,AsyncCallback callback,Object... callbackParams) {
		String dateFmt = DateTimeFormatter.ISO_LOCAL_DATE.format(rankType.virtualDate(curFactDate, offset));
		//图片命名策略，形如../prank/day/2019-12-17_1.jpg
		String reltivePath = tempDir+rankType.getMode()+"/"+dateFmt+"_"+page+".jpg";
		File rankFile = new File(rankImageTempHome+reltivePath);
		if(rankFile.exists()) {
			//缓存有了
			return "file:///"+rankImageTempHome+reltivePath;
		}else {
			if(!rankFile.getParentFile().exists()) {
				rankFile.getParentFile().mkdirs();
			}
			//缓存无，需去网络上取
			new Thread(()-> {
				Map<String, Object> rs = pixivicApiClient.rank((page-1)%3+1, dateFmt, rankType.getMode());
				List<Map<String,Object>> dataList = (List<Map<String, Object>>) rs.get("data");
				if(dataList==null||dataList.isEmpty()) {
					//失败，通过asyncCallback通知
					if(callback!=null) {
						callback.callback("failed",null,callbackParams);
					}
//					task.setMessage("搜索失败喵~，请稍后尝试");
				}else {
					//初始化renderImages
					final boolean[] initSuccess = new boolean[] {true};
					List<PixivUrlRenderImage> waitingRenderImages = dataList.stream()
							 .skip(page/3*9)
							 .limit(9)
							 .parallel()
							 .map(singleData->{
								List<Map<String,Object>> imageUrls = (List<Map<String,Object>>)singleData.get("imageUrls");
								Map<String,Object> artistPreView = (Map<String, Object>) singleData.get("artistPreView");
								try {
//									return new PixivUrlRenderImage(new URL(((String)imageUrls.get(0).get("large")).replace("i.pximg.net", "i.pixiv.cat"))
									return new PixivUrlRenderImage(new URL(((String)imageUrls.get(0).get("large")).replace("_webp", "").replace("i.pximg.net", "i.pixiv.cat"))
//									return new PixivUrlRenderImage(new URL("https://bigimg.cheerfun.dev/get/"+((String)imageUrls.get(0).get("large")))
											, ""+(int)singleData.get("id")+(imageUrls.size()>1?("[1-"+imageUrls.size()+"]"):"")
											, (String)singleData.get("title")
											, (String)artistPreView.get("name"));
								} catch (MalformedURLException e) {
									initSuccess[0] = false;
									
									e.printStackTrace();
								} catch (IOException e) {
									initSuccess[0] = false;
									
									e.printStackTrace();
								}
								return null ;
							 })
							 .collect(Collectors.toList());
					if(callback!=null&&initSuccess[0]==false) {
						callback.callback("failed",null,callbackParams);
					}
					//使用render去绘制
					SimpleImageRender rankRender = new SimpleImageRender();
					rankRender.setImgs(waitingRenderImages);
					try {
						rankRender.render(rankFile);
						if(callback!=null) {
							callback.callback("success","file:///"+rankImageTempHome+reltivePath,callbackParams);
						}
					} catch (DataNeverInitedException e) {
						if(callback!=null) {
							callback.callback("failed",null,callbackParams);
						}
						e.printStackTrace();
					}
				}
			}).start();
			return null;
		}
	}
}