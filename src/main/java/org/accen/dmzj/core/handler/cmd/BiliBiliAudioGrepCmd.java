package org.accen.dmzj.core.handler.cmd;

import java.util.regex.Pattern;

import org.accen.dmzj.core.task.GeneralTask;
import org.accen.dmzj.web.vo.Qmessage;

public class BiliBiliAudioGrepCmd implements CmdAdapter {

	@Override
	public String describe() {
		return "抽取B站音频";
	}

	@Override
	public String example() {
		return "抽取B站[www.bilibili.com/video/av64689940]从[00:00:00]到[00:01:59]音频，设置名称[小狐狸]，简介[无]";
	}

	private final static Pattern grepPattern = Pattern.compile("^抽取B站(.*)?从(.*)?到(.*)?音频，设置名称(.*)?，简介(.*)$");
	@Override
	public GeneralTask cmdAdapt(Qmessage qmessage, String selfQnum) {
		// TODO Auto-generated method stub
		return null;
	}

}
