/*
 * http://www.topcoder.com/stat?c=problem_statement&pm=9791
 */

//import java.smart.programmer.*;
import java.awt.*;
import java.math.*;
import java.util.regex.*;
import static java.util.Arrays.*;
import static java.lang.Integer.parseInt;
import static java.util.Collections.*;
import static java.lang.System.*;
import static java.lang.Math.*;
import java.awt.geom.*;
import java.util.*;
import java.text.*;

//beware of bugs in the following code; I have only made it pass the examples, not proved it correct.

public class ArrayMemoizedBackTrackMinMax {

    final double EPS = 1E-9;

    public double gameValue(int px[], int py[]) {
        final int n = px.length;
        double dist[][] = new double[n][n];
        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++) {
                int dx = px[j]-px[i],
                    dy = py[j]-py[i];
                dist[i][j] = sqrt(dx*dx + dy*dy);
        }
       return min_max(dist, new int[n], n, PLAYER1);
    }

    Map<String, Double> cache = new HashMap();

    final int RED = 1, WHITE = 0, BLUE = -1;
    final boolean PLAYER1 = true, PLAYER2 = false;

    double min_max(double dist[][], int color[], int uncolored, boolean turn) {
        final int n = color.length;
        double ans;
        String hash = Arrays.toString(color);
        if(cache.containsKey(hash))
            return cache.get(hash);
        if(uncolored == 0) {
            ans = 0;
            for(int i = 0; i < n; i++)
                for(int j = i+1; j < n; j++)
                    if(color[i] != color[j])
                        ans += dist[i][j];
        }
        else {
            ans = turn == PLAYER1 ? 0 : Double.POSITIVE_INFINITY;
            for(int i = 0; i < n; i++) {
                if(color[i] == WHITE) {
                    color[i] = turn == PLAYER1 ? RED : BLUE;
                    double temp = min_max(dist, color, uncolored-1, !turn);
                    ans = turn == PLAYER1 ? max(ans, temp) : min(ans, temp);
                    color[i] = WHITE;
                }
            }
        }
        cache.put(hash, ans);
        return ans;
    }

    void print(Object ...O) {err.println(deepToString(O));}
}
