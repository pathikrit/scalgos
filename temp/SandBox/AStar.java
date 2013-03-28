import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public abstract class AStar<Node> {

  /**
   * Run A* starting from start node
   * If not found, return null and empty path
   * Else return goal and path contains trace back to start from goal
   */
  public final Node search(Node start, HashMap<Node, Node> path) {
    HashSet<Node> visited = new HashSet<Node>();
    final HashMap<Node, Double> score = new HashMap<Node, Double>();
    TreeSet<Node> queue = new TreeSet<Node>(new Comparator<Node>() {
      @Override
      public int compare(Node a, Node b) {
        return Double.compare(score.get(a) + heuristic(a), score.get(b) + heuristic(b));
      }
    });

    score.put(start, 0d);
    for(queue.add(start); !queue.isEmpty(); ) {
      Node c = queue.pollLast();
      if(isGoal(c)) {
        return c;
      }
      visited.add(c);
      for(Node n : neighbours(c)) {
        if (!visited.contains(n) && (!score.containsKey(n) || score.get(c) + distance(c, n) <= score.get(n))) {
          score.put(n, score.get(c) + distance(c, n));
          path.put(n, c);
          if (queue.contains(n)) {
            queue.remove(n);
          }
          queue.add(n);
        }
      }
    }

    path.clear();
    return null;
  }

  /**
   * @return all neighbours of node n
   */
  protected abstract Iterable<Node> neighbours(Node n);

  /**
   * Admissible heuristic distance from goal of node n
   * Must never over-estimate i.e. heuristic(x) <= dist(x,y) + heuristic(y) for all x,y
   * When h(x) = 0 then its a simple BFS
   */
  protected double heuristic(Node n) {
    return 0;
  }

  /**
   * returns true iff n is goal (by default we assume heuristic(n) is 0 iff n is goal)
   */
  protected boolean isGoal(Node n) {
    return heuristic(n) == 0;
  }

  /**
   * distance between two neighbour nodes
   * Note: This function will be called for all children
   */
  protected double distance(Node a, Node b) {
    return 1;
  }
}
