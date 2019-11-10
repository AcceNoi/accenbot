package org.accen.dmzj.core.timer;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class CacheMap<K,V> implements Map<K, V>{
	@SuppressWarnings("rawtypes")
	private static Set<CacheMap> register4TimerClearSet = new HashSet<CacheMap>();
	private static final long DEFAULT_TIMEOUT = 30000;
	static {
		new Thread(()->{
			while(true) {
				long curTime = new Date().getTime();
				register4TimerClearSet.forEach(cacheMap->{
					cacheMap.forEach((k,v)->{
						long[] times = cacheMap.getTime(k);
						if(times!=null&&times[0]+times[1]>=curTime) {
							cacheMap.remove(k);
						}
					});
				});
			}
			
		},"static cache map clear ").start();
	}
	
	private ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
	/**
	 * long[0]put time,long[1] timeout
	 */
	private HashMap<K,long[]> timerMap = new HashMap<K, long[]>();
	
	@Override
	public int size() {
		return map.size();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	@Override
	public V get(Object key) {
		return map.get(key);
	}
	public long[] getTime(Object key) {
		return timerMap.get(key);
	}
	@Override
	public V put(K key, V value) {
		timerMap.put(key, new long[] {new Date().getTime(),DEFAULT_TIMEOUT});
		return map.put(key, value);
	}
	public V put(K key,V value,long timeout) {
		timerMap.put(key, new long[] {new Date().getTime(),timeout});
		return map.put(key, value);
	}
	@Override
	public V remove(Object key) {
		timerMap.remove(key);
		return map.remove(key);
	}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		long[] times = new long[] {new Date().getTime(),DEFAULT_TIMEOUT};
		m.forEach((k,v)->{
			timerMap.put(k, times);
		});
		map.putAll(m);
	}
	@Override
	public void clear() {
		timerMap.clear();
		map.clear();
	}
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}
	@Override
	public Collection<V> values() {
		return map.values();
	}
	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}
	
}
