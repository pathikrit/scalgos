import static java.lang.Math.*;
import java.util.*;

/**
 * @author Wrick
 */
public class Divisibility {

	public static void main(String args[]) {
		for(Scanner sc = new Scanner(System.in); sc.hasNext(); ) {
			int a = sc.nextInt(), b = sc.nextInt(), d = sc.nextInt();
			if(d == 0)
				return;
			if(b < a) {
				int t = a;
				a = b;
				b = t;
			}
			System.out.println(numOfMultiples(a-1,b+1,d));
		}
	}

	public static long numOfMultiples(int a, int b, int d) {
		if(d < 0) d = -d;
		if(a < 0 && b < 0) {
			int t = a;
			a = -b;
			b = -t;
		}
		if(a<0)
			return numOfMultiples(0,-a,d) + numOfMultiples(0,b,d) + 1;
		long l = (long)b-a;
		long q = (long)(l/d), r = (long)(l%d);
		if((b-r)%d > b%d)
			q++;
		if(b%d == 0)
			q--;
		return q;
	}
}