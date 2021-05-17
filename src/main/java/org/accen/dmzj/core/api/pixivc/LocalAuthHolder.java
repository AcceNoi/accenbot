package org.accen.dmzj.core.api.pixivc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.accen.dmzj.core.exception.PixivcAuthInitialExcpetion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class LocalAuthHolder implements AuthHolder {
	private final static Logger logger = LoggerFactory.getLogger(LocalAuthHolder.class);
	Auth auth;
	File authLocalFile;
	public LocalAuthHolder(PixivcAuthConfigurationProperties prop) {
		try {
			logger.info("pixivc:当前配置的本地认证文件地址为:{}",prop.authLocation());
			authLocalFile = new File(prop.authLocation());
			if(!authLocalFile.exists()){
				authLocalFile.createNewFile();
			}
		} catch (MalformedURLException e) {
			logger.error("{}.auth-location配置错误！");
			throw new PixivcAuthInitialExcpetion(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public synchronized Auth getAuth() {
		if(auth==null) {
			//为空的话，则看本地文件中是否有存货
			try {
				String authStr = Files.readString(authLocalFile.toPath());
				if(StringUtils.hasText(authStr)) {
					auth = new Auth(authStr);
					return auth;
				}else {
					return null;
				}
				
				
			} catch (MalformedURLException  e) {
				logger.error("{}.auth-location配置错误！");
				throw new PixivcAuthInitialExcpetion(e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return auth;
	}

	@Override
	public void updateAuth(Auth auth) {
		this.auth =auth;
		String authStr = auth.auth();
		try {
			Files.writeString(authLocalFile.toPath(), authStr, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
