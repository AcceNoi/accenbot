package org.accen.dmzj.core.autoconfigure;

import org.accen.dmzj.core.annotation.FeignApi;

public interface FeignApiInitBefore extends NonBeanEnviromentIniter{
	Object before(Class<?> thisClass,FeignApi feignApi);
}
