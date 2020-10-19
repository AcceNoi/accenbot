package org.accen.dmzj.util.setu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.accen.dmzj.core.task.api.PixivcatApiClient;
import org.accen.dmzj.util.ApplicationContextUtil;

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
					Response resp = pixivcatClient.pixivImage(pid);
					if(resp.status()!=200) {
						return false;
					}else {
						try(InputStream is =  resp.body().asInputStream();){
							setuCatcher.catchFromInputStream(pid, is);
						} catch (IOException e) {
							return false;
						}
					}
					return true;
			}).count());
		}
	}

}
