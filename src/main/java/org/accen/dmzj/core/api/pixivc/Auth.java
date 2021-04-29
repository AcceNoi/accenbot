package org.accen.dmzj.core.api.pixivc;
/**
 * 验证信息，可以继承这个类完善更多信息，比如验证信息的有效期等，我这里只是个简单实现
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public record Auth(String auth) {
}
