import static java.lang.Math.*;
import java.util.*;

public class SCC {

	final static int INF = (int)1E9;

	public static void main(String args[]) {

		int mat[][] = { {INF, 1, INF, INF, INF},
						{INF, INF, 1, INF, 1},
						{INF, INF, INF, 1, INF},
						{INF, 1, 1, INF, INF},
						{INF, INF, INF, 1, INF}};
		init();
		tarjanSCC(matrix2list(mat), 0);
	}



	/*
	 * takes in the adjList representation of a directed graph
	 * returns an array of hashsets
	 * hashset[i] is the ith scc
	 * union of hashsets[] = V
	 * any pair-wise intersection is disjoint
	 * i.e. if hashset[i] contains a,b,c then a,b,c form an scc
	 */

	/*public HashSet[] scc(LinkedList adjList[]) {
		final int V = adjList.size();

		for(int i = 0; i < V; i++) {

		}
		return null;

	}*/

	final static int UNDEF = -1, N = 10;
	static int index = 0;
	static Stack<Integer> s = new Stack();
	static int vIndex[] = new int[N],
	    vLowlink[] = new int[N];

	public static void init() {
		Arrays.fill(vIndex, UNDEF);
	}

	/*
	 * returns the maximal scc set which has v0
	 */
	public static void tarjanSCC(LinkedList<Pair> adjList[], int v) {
		vIndex[v] = index;
  		vLowlink[v] = index;
  		index++;
  		s.push(v);                     // Push v on the stack

  		HashSet<Integer> scc = new HashSet();

  		int vprime;

  		for(Pair<Integer, Integer> p : adjList[v]) {
  			vprime = p.getFirst();
  			if(vIndex[vprime] == UNDEF) {
  				tarjanSCC(adjList, vprime);
  				vLowlink[v] = min(vLowlink[v], vLowlink[vprime]);
  			} else if (s.contains(vprime)) { //THIS IS SLOW
  				vLowlink[v] = min(vLowlink[v], vIndex[vprime]);
  			}
  		}

  		if(vLowlink[v] == vIndex[v]) {
  			System.out.println("\nSCC:");
  			do {
  				vprime = s.pop();
  				System.out.print(vprime + ", ");
  				scc.add(vprime);
  			} while(v != vprime);
  		}
	}

	/*
	 * converts an adjacency matrix to a adjacency list
	 * O(V^2)
	 * list = adjacency list
	 * each node of List[i] is of the form <v, w> which means the weight of i-v edge is w
	 */
	public static LinkedList[] matrix2list(int mat[][]) {
		int n = mat.length;

		LinkedList list[] = new LinkedList[n];

		for(int i = 0; i < n; i++)
		{
			list[i] = new LinkedList<Pair>();
			for(int j = 0; j < n; j++)
				if(mat[i][j] < INF)
					list[i].add(new Pair(j, mat[i][j]));
		}
		return list;
	}

/*
	 * pair of objects
	 * supports primitves and arrays too
	 * the printing of arrays of primitves is garbled
	 * can change type of F,S when doing set() !!!
	 */
	public static class Pair<F,S>
	{
		private F first;
		private S second;
		public Pair(F f, S s) {first = f;	second = s;}
		public F getFirst() {return first;}
		public S getSecond() {return second;}
		public void setFirst(F f) {first = f;}
		public void setSecond(S s) {second = s;}
		public String toString() {return "<" + first + ", " + second + ">";}
	}


}