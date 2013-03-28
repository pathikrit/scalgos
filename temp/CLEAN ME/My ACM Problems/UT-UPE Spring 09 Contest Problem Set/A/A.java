import static java.lang.Math.*;
import java.util.*;

public class A {

	public static void main(String args[]) {
		for(Scanner sc = new Scanner(System.in); sc.hasNext(); ) {
			long a = sc.nextLong(), b = sc.nextLong(), d = sc.nextLong();
			if(d == 0)
				return;
			if(b < a) {
				long t = a;
				a = b;
				b = t;
			}
			System.out.println(numOfMultiples(a-1,b+1,d));
		}
	}

	public static long numOfMultiples(long a, long b, long d) {
		if(d < 0) d = -d;
		if(a < 0 && b < 0) {
			long t = a;
			a = -b;
			b = -t;
		}
		if(a < 0)
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