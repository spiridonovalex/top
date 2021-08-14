package top;

public class Entry<T extends Comparable<T>> implements Comparable<Entry<T>> {
	private volatile long count;
	private final T value;
	
	public Entry(long count, T value) {
		this.count = count;
		this.value = value;
	}
	
	@Override
	public int compareTo(Entry<T> o) {
		int result = -Long.compare(count, o.count);
		return result == 0 ? value.compareTo(o.value) : result;
	}
	
	public void setCount(long count) {
		this.count = count;
	}
	
	public long getCount() {
		return count;
	}
	
	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (count ^ (count >>> 32));
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entry other = (Entry) obj;
		if (count != other.count)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Key [count=" + count + ", value=" + value + "]";
	}
}