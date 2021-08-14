package top;

import java.util.List;

public interface Top<T extends Comparable<T>> {
	public long add(T t);
	public List<Entry<T>> get(int n);
}
