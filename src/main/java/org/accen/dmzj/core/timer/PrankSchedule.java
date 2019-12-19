package org.accen.dmzj.core.timer;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrankSchedule {
	private static final Logger logger = LoggerFactory.getLogger(PrankSchedule.class);
	@Autowired
	private Prank prank;
	/**
	 * 每天的凌晨1点刷一次日榜
	 */
	@Scheduled(cron = "0 0 1 * * *")
	public void todayRank() {
		for(int page=1;page<=9;page++) {
			prank.rank(LocalDate.now(), RankType.DAY, 0, page, null);
		}
		
	}
	/**
	 * 每周三的凌晨2点刷一次周榜
	 */
	@Scheduled(cron = "0 0 2 * * WED")
	public void weekRank() {
		for(int page=1;page<=9;page++) {
			prank.rank(LocalDate.now(), RankType.WEEK, 0, page, null);
		}
	}
	/**
	 * 每月三号3点刷一次月榜
	 */
	@Scheduled(cron = "0 0 3 3 * *")
	public void monthRank() {
		for(int page=1;page<=9;page++) {
			prank.rank(LocalDate.now(), RankType.MONTH, 0, page, null);
		}
	}
}
