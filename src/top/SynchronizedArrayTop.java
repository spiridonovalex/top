package top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynchronizedArrayTop<T extends Comparable<T>> implements Top<T> {
	private Map<T, Integer> pointers = new HashMap<>();
	private List<Entry<T>> entries = new ArrayList<>();
	
	@Override
	public synchronized long add(T value) {
		Integer index = pointers.computeIfAbsent(value, k -> {
			entries.add(new Entry<>(0, value));
			return entries.size() - 1;
		});
		Entry<T> entry = entries.get(index);
		entry.setCount(entry.getCount() + 1);
		for (int i = index - 1; i >= 0; --i) {
			Entry<T> prevEntry = entries.get(i);
			if (prevEntry.getCount() >= entry.getCount()) {
				int newIndex = i + 1;
				swap(index, newIndex);
				return entry.getCount();
			}
		}
		swap(index, 0);
		return entry.getCount();
		
	}
	
	private void swap(int index, int newIndex) {
		if (index == newIndex) {
			return;
		}
		Entry<T> entry = entries.get(index);
		//swap
		Entry<T> tmp = entries.get(newIndex);
		entries.set(newIndex, entry);
		entries.set(index, tmp);
		
//		System.out.println(entries.stream().map(e -> e.getCount()).collect(Collectors.toList()));

		pointers.put(entry.getValue(), newIndex);
		pointers.put(tmp.getValue(), index);
	}

	@Override
	public synchronized List<Entry<T>> get(int n) {
		return new ArrayList<>(entries.subList(0, n));
	}
}
