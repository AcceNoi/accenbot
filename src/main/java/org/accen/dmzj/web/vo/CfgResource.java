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
	private String title;
	private String content;
	private String image;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
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
