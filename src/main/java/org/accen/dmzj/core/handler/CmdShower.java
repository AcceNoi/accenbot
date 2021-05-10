package org.accen.dmzj.core.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.accen.dmzj.core.annotation.FuncSwitch;
import org.accen.dmzj.core.annotation.FuncSwitchGroup;
import org.accen.dmzj.core.handler.group.Default;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

@ConfigurationProperties("accen.cmd")
@ConstructorBinding
public class CmdShower {
	private String[] cmdGroupPaths;
	private String[] cmdPaths;
	private boolean cmdGroupPathsExcludeDefault;
	private boolean cmdPathsExcludeDefault;
	public CmdShower(@DefaultValue("org.accen.dmzj.core.handler.group")String[] cmdGroupPaths,
			@DefaultValue("org.accen.dmzj.core.handler.cmd")String[] cmdPaths,
			@DefaultValue("false")boolean cmdGroupPathsExcludeDefault,
			@DefaultValue("false")boolean cmdPathsExcludeDefault) {
//		this.cmdGroupPaths = cmdGroupPaths;
//		this.cmdPaths = cmdPaths;
		this.cmdGroupPathsExcludeDefault = cmdGroupPathsExcludeDefault;
		if(this.cmdGroupPathsExcludeDefault) {
			this.cmdGroupPaths = cmdGroupPaths;
		}else {
			if(!Arrays.stream(cmdGroupPaths)
					.anyMatch(path->"org.accen.dmzj.core.handler.group".equals(path)||"org.accen.dmzj.core.handler.group".startsWith(path+"."))) {
				//如果当前配置没有包含默认，且设置了不排除默认，则自动增加默认
				this.cmdGroupPaths = Arrays.copyOf(cmdGroupPaths, cmdGroupPaths.length+1);
				this.cmdGroupPaths[this.cmdGroupPaths.length-1] = "org.accen.dmzj.core.handler.group";
			}else {
				this.cmdGroupPaths = cmdGroupPaths;
			}
		}
		this.cmdPathsExcludeDefault = cmdPathsExcludeDefault;
		if(this.cmdPathsExcludeDefault) {
			this.cmdPaths = cmdPaths;
		}else {
			if(!Arrays.stream(cmdPaths)
					.anyMatch(path->"org.accen.dmzj.core.handler.cmd".equals(path)||"org.accen.dmzj.core.handler.cmd".startsWith(path+"."))) {
				//如果当前配置没有包含默认，且设置了不排除默认，则自动增加默认
				this.cmdPaths = Arrays.copyOf(cmdPaths, cmdPaths.length+1);
				this.cmdPaths[this.cmdPaths.length-1] = "org.accen.dmzj.core.handler.cmd";
			}else {
				this.cmdPaths = cmdPaths;
			}
		}
		doScan();
	}
	/**
	 * 功能分组定义
	 */
	List<FuncSwitchGroup> cmdGroups;
	
	/**
	 * 索引
	 */
	Map<String, FuncSwitchGroup> cmdGroupQuickIndex;
	/**
	 * 功能定义
	 */
	Map<FuncSwitchGroup, List<FuncSwitch>> cmds;
	
	protected void doScan() {
		if(this.cmdGroupPaths!=null&&this.cmdGroupPaths.length>0) {
			cmdGroupQuickIndex = new HashMap<String, FuncSwitchGroup>(16);
			ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(cmdGroupPaths).scan();
			cmdGroups = scanResult.getAllClasses()
						.stream()
						.map(classInfo -> {
							FuncSwitchGroup ann = classInfo.loadClass().getAnnotation(FuncSwitchGroup.class);
							if(ann!=null&&ann.showMenu()) {
								cmdGroupQuickIndex.put(StringUtils.hasText(ann.name())?ann.name().trim():"cmd".concat(classInfo.getName())
										, ann);
								return ann;
							}else {
								return null;
							}
							
						})
						.filter(ann->ann!=null)
						.sorted((ann1,ann2)->ann1.order()-ann2.order())
						.collect(Collectors.toList());
		}
		
		if(this.cmdPaths!=null&&this.cmdPaths.length>0) {
			cmds = new HashMap<>(8);
			ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages(cmdPaths).scan();
			scanResult.getAllClasses()
					.parallelStream()
					.forEach(classInfo->{
									FuncSwitch ann = classInfo.loadClass().getAnnotation(FuncSwitch.class);
									if(ann!=null&&ann.showMenu()&&ann.groupClass()!=Default.class) {
										FuncSwitchGroup groupAnn = ann.groupClass().getAnnotation(FuncSwitchGroup.class);
										if(groupAnn!=null&&cmdGroups.contains(groupAnn)) {
											if(!cmds.containsKey(groupAnn)) {
												cmds.put(groupAnn, new ArrayList<>());
											}
											cmds.get(groupAnn).add(ann);
										}
									}
								});
			//重新排序
			cmds.keySet().stream().forEach(group->{
				cmds.get(group).sort((ann1,ann2)->ann1.order()-ann2.order());
			});
		}
	}

	public List<FuncSwitchGroup> getCmdGroups() {
		return cmdGroups;
	}

	public Map<FuncSwitchGroup, List<FuncSwitch>> getCmds() {
		return cmds;
	}
	
}