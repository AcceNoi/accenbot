package org.accen.dmzj.core.api.steam;

public class SteamNewsFormatter {
	private SteamVariableConfiguration prop;
	public SteamNewsFormatter(SteamVariableConfiguration prop) {
		this.prop = prop;
	}
	private final String imgPattern1 = "\\[img\\]\\{STEAM_CLAN_IMAGE\\}";
	private final String imgPattern2 = "\\[/img\\]";
	private final String hPattern1 = "\\[h\\d\\]";
	private final String hPattern2 = "\\[/h\\d\\]";
	public String formatContents(String content) {
		//图片转cq格式
		String result = content.replaceAll(imgPattern1, "[CQ:image,file=".concat(prop.STEAM_CLAN_IMAGE()))
				.replaceAll(imgPattern2, "]");
		//header清除
		return result.replaceAll(hPattern1, "").replaceAll(hPattern2, "");
	}
}
