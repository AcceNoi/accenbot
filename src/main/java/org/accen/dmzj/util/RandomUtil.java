package org.accen.dmzj.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
	/**
	 * 暂时只支持有放回的随机抽取算法
	 * @param <T>
	 * @param objs
	 * @param count
	 * @return 如果随机到重复的，则是同一个对象
	 */
	public static <T> List<T> randomObjWeight(List<RandomMeta<T>> objs,int count) {
		int all  = objs.stream().map(obj->obj.getWeight()).reduce(Integer::sum).get();
		int[] tgts = new int[count];
		List<T> tgtts = new ArrayList<T>(count);
		for(int i=0;i<count;i++) {
			tgts[i] = randomInt(all);
		}
		for (int i = 0; i < tgts.length; i++) {
			int sum = 0;
			for(RandomMeta<T> obj:objs) {
				sum += obj.getWeight();
				if(tgts[i]<sum) {
					tgtts.add(i,  obj.getObj());
					break;
				}
			}
		}
		return tgtts;
	}
	
}
