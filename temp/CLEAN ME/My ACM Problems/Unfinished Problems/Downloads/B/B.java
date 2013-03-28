import java.util.*;
import static java.lang.Math.*;

public class B {

	public static void main(String args[]) {
		for(Scanner sc = new Scanner(System.in); sc.hasNext(); ) {
			int d = sc.nextInt(), s = sc.nextInt(), l = sc.nextInt();
			if(d + s + l == 0)
				return;
			 double t = ceil(1.*s/d-l);
			 if(t <= 0)
			 	System.out.println("Rick does not need to wait at all!");
			 else if (t >= 24*60*60)
			 	System.out.println("Rick needs to get a faster internet!");
			 else
			 	System.out.format("Rick needs to wait for: %.3f s\n", t);
		}
	}
}