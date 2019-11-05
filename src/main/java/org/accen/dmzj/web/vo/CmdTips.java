package org.accen.dmzj.web.vo;
/**
 * tips实体
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
public class CmdTips {
	private long id;
	private String moduleName;
	private String moduleCode;
	private String cmdFunc;
	private String content;
	private short status;//1-启用，2-停用
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	public String getCmdFunc() {
		return cmdFunc;
	}
	public void setCmdFunc(String cmdFunc) {
		this.cmdFunc = cmdFunc;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public short getStatus() {
		return status;
	}
	public void setStatus(short status) {
		this.status = status;
	}
	
}
