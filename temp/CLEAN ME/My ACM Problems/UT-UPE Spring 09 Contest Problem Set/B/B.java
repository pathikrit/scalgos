import java.util.*;
import static java.lang.Math.*;

public class B {

	final static int INF = (int)2E9, N = 3;

	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);
		for(int CASES = s.nextInt(); CASES > 0; CASES--) {
			int houses = s.nextInt();
			int cost[][] = new int[houses][N];
			for(int i = 0; i < houses; i++)
				for(int j = 0; j < N; j++)
					cost[i][j] = s.nextInt();
			int ans = INF;
			for(int color = 0; color < N; color++)
				ans = min(ans, search(0, color, 0, cost));
			System.out.println(ans);
		}
	}

	public static int search(int house, int color, int cost, int cost_matrix[][]) {
		if(house == cost_matrix.length)
			return cost;

		cost += cost_matrix[house][color %= N];
		house++;

		int cost1 = search(house, ++color, cost, cost_matrix),
		    cost2 = search(house, ++color, cost, cost_matrix);
		return min(cost1, cost2);
	}
}