package org.accen.dmzj.util;

/**
 * 用于辅助随机抽取
 */
public class RandomMeta<T>{
	private T obj;//单个抽取对象
	private int weight ;//权值，
	public T getObj() {
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public RandomMeta(T obj, int weight) {
		super();
		this.obj = obj;
		this.weight = weight;
	}
	
}