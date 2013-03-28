class PriorityQueueNode<E, C extends Comparable> implements Comparable<PriorityQueueNode> {
	private C c;
	private E e;

	public PriorityQueueNode(C c, E e) {
		this.c = c;
		this.e = e;
	}

	public int compareTo(PriorityQueueNode p) {
		return p.c.compareTo(c);
	}
}

public class PriorityQueue2 {
	
	public static void main(String args[]) {
		
	}
}
