package org.accen.dmzj.core.timer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public enum RankType {
	DAY("day"),WEEK("week"),MONTH("month");
	private String mode;
	private RankType(String mode) {
		this.mode = mode;
	}
	public String getMode() {
		return this.mode;
	}
	public LocalDate virtualDate(LocalDate factDate,int offset) {
		if(factDate==null) {
			factDate = LocalDate.now();
		}
		LocalDate curVirtualDate = factDate.minusDays(2);//先延迟
		
		switch(this) {
		case DAY:
			curVirtualDate = curVirtualDate.minusDays(offset);//再往前推offset天
			break;
		case WEEK:
			curVirtualDate = curVirtualDate
				.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
				.minusWeeks(offset);//变成周一后再往前推offset周
			break;
		case MONTH:
			curVirtualDate = curVirtualDate
				.with(TemporalAdjusters.firstDayOfMonth())
				.minusMonths(offset);//变成1号后再往前推offset月
			break;
		default:
			curVirtualDate = curVirtualDate.minusDays(offset);//再往前推offset天
			break;
		}
		return curVirtualDate;
	}
}
