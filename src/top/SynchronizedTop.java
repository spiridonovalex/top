package top;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SynchronizedTop<T extends Comparable<T>> implements Top<T> {
	private TreeSet<Entry<T>> set = new TreeSet<>();
	//private PriorityQueue<Entry<T>> set = new PriorityQueue<>();
	private Map<T, AtomicLong> map = new ConcurrentHashMap<>();
	
	@Override
	public long add(T value) {
		AtomicLong al = map.computeIfAbsent(value, k -> new AtomicLong());

		Entry<T> key = new Entry<>(al.get(), value);
		synchronized (this) {
			set.remove(key);
			long incremented = al.incrementAndGet();
			key.setCount(incremented);
			set.add(key);
			return incremented;
		}
	}
	
	@Override
	public synchronized List<Entry<T>> get(int n) {
		return set.stream().limit(n).collect(Collectors.toList());
	}
}
