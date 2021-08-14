package top;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AggregatedTop<T extends Comparable<T>> implements Top<T> {
	private final Top<T> [] tops;
	
	public AggregatedTop(List<Top<T>> tops) {
		Objects.requireNonNull(tops, "tops couldn't be null");
		this.tops = tops.toArray(new Top[0]);
	}
	
	private int getBucket(T t) {
		return Math.abs(t.hashCode()) % tops.length;
	}
	
	private Top<T> getTop(T t) {
		return tops[getBucket(t)];
	}
	
	@Override
	public long add(T t) {
		return getTop(t).add(t);
	}

	@Override
	public List<Entry<T>> get(int n) {
		var topsN = Arrays.stream(tops)
				.map(top -> top.get(n))
				.filter(list -> !list.isEmpty())
				.collect(Collectors.toList());
		int size = topsN.size();
		int [] indexes = new int[size];
		List<Entry<T>> result = new ArrayList<>(n);
		for (int i = 0; i < n; ++i) {
			Entry<T> key = null;
			for (int j = 0; j < size; ++j) {
				List<Entry<T>> top = topsN.get(j);
				int index = indexes[j];
				if (top.size() > index) {
					Entry<T> candidate = top.get(index);
					if (key == null || candidate.getCount() > key.getCount()) {
						key = candidate;
						++indexes[j];
					}
				}
			}
			if (key == null) {
				break;
			}
			result.add(key);
		}
		return result;
	}

}
