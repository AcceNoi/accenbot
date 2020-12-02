package org.accen.dmzj.core.autoconfigure;

import org.accen.dmzj.core.annotation.FeignApi;

public interface FeignApiInitAfter extends NonBeanEnviromentIniter{
	Object after(Object thisObj,FeignApi feignApi);
}
