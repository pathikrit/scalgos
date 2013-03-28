import java.util.*;
import java.io.*;

public class MinCostMaxFlow
{
    // MAX FLOW - GLOBALS.
    // This will be problem specific, but don't leave required stuff
    // uninitialized: source, sink and size in particular.
    // Also, keep an eye on the value of INF, in case the problem
    // constraints need it to be set higher.
    int[][] cap;        // Capacities of original graph - adjacency matrix.
    int[][] adj;        // Adjacency list for faster access.
    int[][] resid;      // Residual network.
    int[][] flow;       // The flow graph.
    int[][] cost;       // For min-cost flow.
    int[] deg;          // For the adjacency list.
    int n;              // No of vertices in original graph.
    int e;              // No of edges in original graph.
    int size;           // Size of residual network.
                        // In case we're splitting vertices or something.
    int source, sink;   // Kinda obvious what these are.
    boolean[] visited;
    int[] parent;
    final int INF = 987654321;

    public MinCostMaxFlow(int adjMat[][], int int s, int t, )

    // Max Flow. Give it the initial network (resid), source and sink,
    // and it will do the rest.
    public int maxFlow()
    {
        cap = new int[size][size];
        adj = new int[size][size];
        deg = new int[size];

        // Construct the adjacency matrix.
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
            {
                cap[i][j] = resid[i][j];
                if (resid[i][j] > 0 || resid[j][i] > 0)
                    adj[i][deg[i]++] = j;
            }

        int ret = 0;
        visited = new boolean[size];
        parent = new int[size];

        while (BFSPath(source))
        {
            int dec = getCap();
            augment(dec);
            ret += dec;
            visited = new boolean[size];
            parent = new int[size];
        }
        return ret;
    }

    // Pathfinding using DFS.
    boolean DFSPath(int curr)
    {
        if (visited[curr])
            return false;

        visited[curr] = true;
        if (curr == sink)
            return true;

        for (int i = 0,  v = adj[curr][i]; i < deg[curr]; i++, v = adj[curr][i])
            if (resid[curr][v] > 0 && DFSPath(v))
            {
                parent[v] = curr;
                return true;
            }

        return false;
    }

    // Pathfinding using BFS.
    boolean BFSPath(int source)
    {
        Queue<Integer> queue = new LinkedList<Integer>();

        queue.offer(source);
        visited[source] = true;
        while (!queue.isEmpty())
        {
            int curr = queue.poll();
            if (curr == sink)
                return true;

            for (int i = 0,  v = adj[curr][i]; i < deg[curr]; i++, v = adj[curr][i])
                if (resid[curr][v] > 0 && !visited[v])
                {
                    parent[v] = curr;
                    visited[v] = true;
                    queue.offer(v);
                }
        }

        return false;
    }

    // Compute path capacity.
    int getCap()
    {
        int ret = INF;
        int curr = sink;
        while (curr != source)
        {
            int prev = parent[curr];
            ret = Math.min(ret, resid[prev][curr]);
            curr = prev;
        }
        return ret;
    }

    // Augment the residual network with a found path.
    void augment(int dec)
    {
        int curr = sink;
        while (curr != source)
        {
            int prev = parent[curr];
            resid[prev][curr] -= dec;
            resid[curr][prev] += dec;
            curr = prev;
        }
    }

    // Builds the flow graph, taking into account the possibility of
    // backward edges in the original graph.
    void makeFlowGraph()
    {
        flow = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (cap[i][j] > 0)
                    flow[i][j] = cap[i][j] - resid[i][j];
    }

    // Call this right after the final call to the path finding
    // algorithm, without resetting any arrays. This gives the
    // edges of the min-cut as a vector whose elements are of the form (u, v).
    Vector<int[]> minCut()
    {
        Vector<int[]> ret = new Vector<int[]>();
        for (int i = 0; i < size; i++)
            if (visited[i])
                for (int j = 0; j < deg[i]; j++)
                {
                    int v = adj[i][j];
                    if (!visited[v] && cap[i][v] > 0 && cap[i][v] < INF)
                        ret.add(new int[]{i, v});
                }
        return ret;
    }

    // Transforms the residual network so that the min cut found
    // has the minimum number of edges. Make sure that T is bigger
    // than the max number of edges in the min cut. Usually T = |E| + 1.
    // This transformation can also be applied to adj before building resid, in which
    // case this shouldn't be done.
    void minCardinalityTransformation(int T)
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (resid[i][j] > 0)
                    resid[i][j] = T * resid[i][j] + 1;
    }

    // MAXIMUM BIPARTITE MATCHING:
    int left, right;        // Sizes of the two sets being matched.
    int[] leftMatch;        // Matching from left set to right set.
    int[] rightMatch;       // Matching from right set to left set.

    // This does bipartite matching using adjacency lists, and taking
    // advantage of the special features of flow networks used for
    // bipartite matching.
    int maxMatching()
    {
        visited = new boolean[right];
        leftMatch = new int[left];
        rightMatch = new int[right];
        Arrays.fill(leftMatch, -1);
        Arrays.fill(rightMatch, -1);

        int ret = 0;
        for (int i = 0; i < left; i++)
        {
            if (DFSMatch(i))
                ret++;
            visited = new boolean[right];
        }

        return ret;
    }

    // Match using BFS.
    boolean BFSMatch(int leftElem)
    {
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.offer(leftElem);

        parent = new int[left];
        Arrays.fill(parent, -1);
        parent[leftElem] = leftElem;

        boolean found = false;
        int psink = -1, match = -1;

        while (!queue.isEmpty())
        {
            int curr = queue.poll();
            for (int i = 0,  rightElem = adj[curr][i]; i < deg[curr]; i++, rightElem = adj[curr][i])
            {
                int next = rightMatch[rightElem];

                if (next == curr)
                    continue;

                if (next == -1)
                {
                    found = true;
                    psink = curr;
                    match = rightElem;
                    break;
                }

                if (parent[next] == -1)
                {
                    queue.offer(next);
                    parent[next] = curr;
                }
            }
        }

        if (!found)
            return false;

        int curr = psink;
        while (curr != parent[curr])
        {
            int temp = leftMatch[curr];
            leftMatch[curr] = match;
            rightMatch[match] = curr;
            match = temp;
            curr = parent[curr];
        }

        leftMatch[curr] = match;
        rightMatch[match] = curr;

        return true;
    }

    // Match using DFS.
    boolean DFSMatch(int leftElem)
    {
        if (leftElem == -1)
            return true;

        for (int i = 0,  rightElem = adj[leftElem][i]; i < deg[leftElem]; i++, rightElem = adj[leftElem][i])
            if (!visited[rightElem])
            {
                visited[rightElem] = true;
                int next = rightMatch[rightElem];
                if (DFSMatch(next))
                {
                    leftMatch[leftElem] = rightElem;
                    rightMatch[rightElem] = leftElem;
                    return true;
                }
            }
        return false;
    }

    // Vertex cover for unweighted bipartite graphs.
    // This uses the bipartite matching code above, but for
    // weighted vertex cover, the same technique can be applied
    // to extract the cover from standard max flow.
    boolean[][] vertexCover()
    {
        maxMatching();

        boolean[] leftMatched = new boolean[left];
        boolean[] rightMatched = new boolean[right];
        Queue<Integer> queue = new LinkedList<Integer>();
        visited = new boolean[right];

        for (int i = 0; i < left; i++)
        {
            if (leftMatch[i] >= 0)
                leftMatched[i] = true;
            else
                for (int j = 0,  rightElem = adj[i][j]; j < deg[i]; j++, rightElem = adj[i][j])
                {
                    visited[rightElem] = rightMatched[rightElem] = true;
                    queue.offer(rightElem);
                }
        }

        while (!queue.isEmpty())
        {
            int curr = queue.poll();
            int match = rightMatch[curr];
            leftMatched[match] = false;

            for (int i = 0,  rightElem = adj[match][i]; i < deg[match]; i++, rightElem = adj[match][i])
                if (!visited[rightElem])
                {
                    visited[rightElem] = rightMatched[rightElem] = true;
                    queue.offer(rightElem);
                }
        }

        return new boolean[][]{leftMatched, rightMatched};
    }


    int[] pi;                   // Potentials for mincost.
    boolean[] forward;          // Forward or backward edge.

    // Most general min cost flow algorithm. Handles edges going both ways, and
    // its adjacency list approach is about as fast as can be reasonably
    // expected, short of using a heap for Dijkstra.
    // Note: Make sure that all costs are non-negative! Negative costs from
    // backward edges in the residual network will be handled properly.
    // For max cost max flow, just negate the capacities.
    int minCostFlow()
    {
        cap = new int[size][size];
        adj = new int[size][size];
        deg = new int[size];
        pi = new int[size];

        // Construct the adjacency matrix.
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
            {
                cap[i][j] = resid[i][j];
                if (resid[i][j] > 0 || resid[j][i] > 0)
                    adj[i][deg[i]++] = j;
            }

        int flowVal = 0, flowCost = 0;
        while (DijkstraPath(source))
        {
            int dec = INF;
            int curr = sink;
            while (curr != source)
            {
                int prev = parent[curr];
                if (forward[curr])
                    dec = Math.min(dec, resid[prev][curr]);
                else
                    dec = Math.min(dec, cap[curr][prev] - resid[curr][prev]);
                curr = prev;
            }

            curr = sink;
            while (curr != source)
            {
                int prev = parent[curr];
                if (forward[curr])
                {
                    resid[prev][curr] -= dec;
                    flowCost += cost[prev][curr] * dec;
                }
                else
                {
                    resid[curr][prev] += dec;
                    flowCost -= cost[curr][prev] * dec;
                }
                curr = prev;
            }
            flowVal += dec;
        }

        return flowCost;
    }


    // This is for the successive shortest paths augmenting algorithm used
    // in min cost max flow.
    int[] dist;
    boolean DijkstraPath(int source)
    {
        dist = new int[size];
        parent = new int[size];
        boolean[] perm = new boolean[size];
        forward = new boolean[size];

        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        for (int k = 0; k < size; k++)
        {
            int best = -1, bestDist = INF;
            for (int i = 0; i < size; i++)
            {
                if (!perm[i] && dist[i] < bestDist)
                {
                    best = i;
                    bestDist = dist[i];
                }
            }
            if (bestDist == INF)
                break;

            perm[best] = true;
            for (int i = 0; i < deg[best]; i++)
            {
                int v = adj[best][i];
                if (resid[best][v] > 0 && dist[v] > bestDist + cost[best][v] + pi[best] - pi[v])
                {
                    dist[v] = bestDist + cost[best][v] + pi[best] - pi[v];
                    parent[v] = best;
                    forward[v] = true;
                }

                if (resid[v][best] < cap[v][best] && dist[v] > bestDist - cost[v][best] + pi[best] - pi[v])
                {
                    dist[v] = bestDist - cost[v][best] + pi[best] - pi[v];
                    parent[v] = best;
                    forward[v] = false;
                }
            }
        }

        for (int i = 0; i < size; i++)
            if (pi[i] < INF)
                pi[i] += dist[i];

        return dist[sink] < INF;
    }
}
