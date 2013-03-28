import java.util.Iterator;

public class Subsets<T> implements Iterable<T[]> {

	public static <T> Subsets<T> of(T[] set) {
		return new Subsets<T>(set);
	}
  
	public Iterator<T[]> iterator() {
		return new SubsetIterator();
	}

	private final T[] set;

	private Subsets(T[] set) {
		this.set = set;
	}

	private class SubsetIterator implements Iterator<T[]> {

		public boolean hasNext() {
		}

		public T[] next() {
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}