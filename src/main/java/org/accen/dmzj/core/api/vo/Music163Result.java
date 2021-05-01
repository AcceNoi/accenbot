package org.accen.dmzj.core.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("preview")
@JsonIgnoreProperties(ignoreUnknown = true)
public record Music163Result(Music163Ctt result,String code,String msg) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Music163Ctt(Music163[] songs,int songCount) {}
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Music163(long id,String name,int position,Object alias,int status,int fee,long copyrightId,
			String disc,int no,String mp3Url) {}
}