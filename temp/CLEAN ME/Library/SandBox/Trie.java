import java.util.*;

public class Trie<T> {
	
	protected class Node {
		private HashMap<T, Node> edges = new HashMap<T, Node>(1);	
		
		public Node addNext(T t) {
			if(!edges.containsKey(t))
				edges.put(t, new Node());
			return edges.get(t);
		}
		
		public Node getNext(T t) {
			return edges.get(t);
		}
		
		public boolean isEnd() {
			return edges.containsKey(END);
		}
		
		public void markEnd() {
			edges.put(END, null);
		}
		
		public boolean unmarkEnd() {
			if(!isEnd())
				return false;
			edges.remove(END);
			return true;
		}
		
		/**
		 * Two nodes are equal IFF their children are equals
		 * 
		 */
		public boolean equals(Object obj) {			
			Node node = (Node)obj;
			for(T t : edges.keySet()) {
				Node n1 = getNext(t), n2 = node.getNext(t);
				if(n1 == null && n2 == null)
					continue;	
				else if((n1 == null ^ n2 == null) || !n1.equals(n2))
					return false;
			}				
			return true;		
		}
		
		public int hashCode() {
			return edges.keySet().hashCode();
		}
		
	}
	
	private final Node ROOT = new Node();
	private T END;
	
	public Trie(T endMarker) {
		END = endMarker;
	}
	
	public void add(ArrayDeque<T> tList) {
		Node last = ROOT;
		for(T t : tList)
			last = last.addNext(t);
		last.markEnd();
	}
	
	public boolean contains(ArrayDeque<T> tList) {
		Node last = traverseToEnd(ROOT, tList);
		return last != null && last.isEnd();
	}
	
	// Leaves dangling chains
	public boolean delete(ArrayDeque<T> tList) {
		Node last = traverseToEnd(ROOT, tList);
		return last != null && last.unmarkEnd();
	}
	
	private Node traverseToEnd(Node start, ArrayDeque<T> tList) {
		if(start == null || tList.isEmpty())
			return start;		
		return traverseToEnd(start.getNext(tList.pop()), tList);
	}
	
	public void compress() {
		Collection<Node> uniqueNodes = uniqueNodes(ROOT);
	}
	
	public int uniqueNodeCount() {
		return uniqueNodes(ROOT).size();
	}
	
	private HashSet<Node> uniqueNodes(Node node) {
		if(node == null)
			return new HashSet<Node>();
		Collection<Node> children = node.edges.values();
		HashSet<Node> uniqueChildren = new HashSet<Node>();
		uniqueChildren.add(node);
		for(Node child : children)
			uniqueChildren.addAll(uniqueNodes(child));
		return uniqueChildren;
	}
	
	public int internalNodeCount() {
		return internalNodeCount(ROOT);		
	}	
	
	private int internalNodeCount(Node node) {
		if(node == null)
			return 0;
		Collection<Node> children = node.edges.values();
		int count = children.size();
		for(Node child : children)
			count += internalNodeCount(child);
		return count;
	}
}
