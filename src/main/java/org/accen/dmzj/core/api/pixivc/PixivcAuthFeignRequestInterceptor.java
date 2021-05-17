package org.accen.dmzj.core.api.pixivc;

import java.lang.reflect.InvocationTargetException;

import org.accen.dmzj.core.feign.auth.AbstractHeaderAuthCounterFeignRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * pix.ipv4.host使用authorization token实现认证
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */

public class PixivcAuthFeignRequestInterceptor extends AbstractHeaderAuthCounterFeignRequestInterceptor{
	
	
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(PixivcAuthFeignRequestInterceptor.class);
	
	/**
	 * auth header
	 */
	public final static String AUTH_HEADER_KEY = "authorization";
	private PixivcAuthConfigurationProperties prop;
	public PixivcAuthFeignRequestInterceptor(PixivcAuthConfigurationProperties prop) {
		this.prop = prop;
		try {
			holder = this.prop.authHolderClass().getDeclaredConstructor(PixivcAuthConfigurationProperties.class).newInstance(prop);
			fresher = this.prop.authFresherClass().getDeclaredConstructor(PixivcAuthConfigurationProperties.class).newInstance(prop);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * auth持有器
	 */
	private AuthHolder holder;
	/**
	 * auth刷新器
	 */
	private AuthFresher fresher;
	protected void init() {
		Auth auth = holder.getAuth();
		if(auth==null||!fresher.vertify(auth)) {
			//现有的auth holder没有存货，或者过期了，则使用fresher去刷新
			auth = fresher.fresh(null);
		}
		
		if(auth!=null) {
			holder.updateAuth(auth);
		}
	}
	private boolean inited = false;
	@Override
	public void firstTimeDo() {
		/*if(holder.getAuth()==null) {
			synchronized (holder) {
				if(holder.getAuth()==null) {
					init();
				}
			}
		
		}*/
		if(!inited) {
			synchronized (holder) {
				if(!inited) {
					init();
				}
			}
		}
		//添加认证header
		if(holder.getAuth().auth()!=null) {
			super.addHeader(AUTH_HEADER_KEY, holder.getAuth().auth());
		}
	}
	
}
