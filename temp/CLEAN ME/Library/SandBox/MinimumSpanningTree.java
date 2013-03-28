import java.awt.*;
import java.math.*;
import java.util.regex.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.lang.System.*;
import static java.lang.Math.*;
import java.awt.geom.*;
import java.util.*;
import java.text.*;

public class MinimumSpanningTree {

	final static int INF = (int)1E9;

	public static void main(String args[]) {
		int mat[][] = {{0,    7,  INF,  5,  INF, INF, INF},
					   {7,	 0,	 8,   9,   7,  INF, INF},
					   {INF,   8,   0,  INF,  5,  INF, INF},
					   {5,    9,  INF,  0,  15,  6,   INF},
					   {INF,   7,   5,  15,   0,  8,    9},
					   {INF,  INF, INF,  6,   8,  0,   11},
					   {INF,  INF, INF, INF,  9, 11,    0}};

		for(int e[] : primsMST(mat))
			System.out.println(Arrays.toString(e) + " " + mat[e[0]][e[1]]);
	}

	/*
	 * Prim's Minimum Spanning Tree algorithm
	 * O(V*V)
	 * if u, v disconnected mat[u][v] = INF
	 * mat[i][i] = 0
	 * returns an arraylist of edges {u,v} forming the mst
	 */
	public static ArrayList<int[]> primsMST(int mat[][]) {
		final int V = mat.length;
		HashSet<Integer> visited = new HashSet(), unvisited = new HashSet();
		ArrayList<int[]> spanningEdges = new ArrayList();
		visited.add(0);
		for(int i = 1; i < V; i++)
			unvisited.add(i);

		while(visited.size() < V) {
			int u = -1, v = -1, w = INF;
			for(int i : visited)
				for(int j : unvisited)
					if(mat[i][j] <= w) {
						u = i;
						v = j;
						w = mat[i][j];
					}
			assert u > -1 && v > -1;
			spanningEdges.add(new int[]{u, v});
			unvisited.remove(v);
			visited.add(v);
		}
		return spanningEdges;
	}
}

