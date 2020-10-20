package org.accen.dmzj.util.setu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.accen.dmzj.core.task.api.PixivcatApiClient;
import org.accen.dmzj.util.ApplicationContextUtil;
import org.accen.dmzj.util.FilePersistentUtil;
import org.accen.dmzj.util.render.UrlRenderImage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import feign.Response;
/**
 * p站图抓取
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class PixivSetuGreper implements SetuGreper {
	private PixivcatApiClient pixivcatClient = ApplicationContextUtil.getBean(PixivcatApiClient.class);
	private SetuCatcher setuCatcher = ApplicationContextUtil.getBean(SetuCatcher.class);
	
	private List<String> pids;//多图用-隔开
	
	public PixivSetuGreper(String... pids) {
		this.pids = Arrays.asList(pids);
	}
	public PixivSetuGreper(List<String> pids) {
		this.pids = pids;
	}

	@Override
	public int grep() {
		if(pids==null||pids.isEmpty()) {
			return 0;
		}else {
			return (int)(pids.parallelStream().filter(pid->{
				return setuCatcher.catchFromPid(pid, pid+SetuCatcher.SETU_SUFFIX);
			}).count());
		}
	}

}
