package org.accen.dmzj.util;

import java.util.Random;

public class RandomUtil {
	private static final Random rd = new Random();
	/**
	 * 生成[0,max)的随机int值
	 * @param max
	 * @return
	 */
	public static int randomInt(int max) {
		return rd.nextInt(max);
	}
}
