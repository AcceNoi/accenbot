package org.accen.dmzj.web.vo;
/**
 * 资源，通过cfgResource定位，支持网络，文件，只要包含协议就行
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class CfgResource {
	private long id;
	private String cfgKey;
	private String cfgResource;
	private String resourceType;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCfgKey() {
		return cfgKey;
	}
	public void setCfgKey(String cfgKey) {
		this.cfgKey = cfgKey;
	}
	public String getCfgResource() {
		return cfgResource;
	}
	public void setCfgResource(String cfgResource) {
		this.cfgResource = cfgResource;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
}
