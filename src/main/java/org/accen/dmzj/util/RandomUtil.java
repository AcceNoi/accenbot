package org.accen.dmzj.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
	 * 百分比几率随机是否通过。
	 * @param prob 通过率，大于等于1则返回true，小于等于0则返回false，其他按几率返回
	 * @return
	 */
	public static boolean randomPass(double prob) {
		if(prob>=1) {
			return true;
		}else if(prob<=0) {
			return false;
		}else {
			return rd.nextDouble()<prob;
		}
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
	
	private static final String[] ZH_NUM = new String[] {"〇","一","二","三","四",
														"五","六","七","八","九"};
	/**
	 * 随机生成简体汉字小写数字
	 * @param length
	 * @return
	 */
	public static String randZhNum(int length) {
		if(length>0) {
			StringBuffer rsBuff = new StringBuffer();
			for(int index=0;index<length;index++) {
				int rdm = randomInt(10);
				rsBuff.append(ZH_NUM[rdm]);
			}
			return rsBuff.toString();
		}
		return null;
		
	}
	/**
	 * 随机生成简体汉字小写数字（不会随机到重复的）
	 * @param length
	 * @param count
	 * @return
	 */
	public static String[] randZhNumEx(int length,int count) {
		
		if(length>0&&count>0) {
			Set<String> rs = new HashSet<String>(count);
			while(rs.size()<count) {
				rs.add(randZhNum(length));
			}
			return rs.toArray(new String[count]);
		}
		return null;
		
	}
	/**
	 * 随机生成简体汉字小写数字（可排除）
	 * @param length
	 * @param excludes
	 * @return
	 */
	public static String randZhNumExclude(int length,Collection<String> excludes) {
		String rd = randZhNum(length);
		if(excludes!=null&&excludes.contains(rd)) {
			return randZhNumExclude(length, excludes);
		}else {
			return rd;
		}
	}
	/**
	 * 随机从可切分的字符串中取子串
	 * @param origin
	 * @param split
	 * @return
	 */
	public static String randomStringSplit(String origin,String split) {
		String[] origins = origin.split(split);
		return origins[randomInt(origins.length)];
	}/**
	 * 随机从可切分的字符串中取子串，默认西文逗号,
	 * @param origin
	 * @return
	 */
	public static String randomStringSplit(String origin) {
		return randomStringSplit(origin, ",");
	}
}
