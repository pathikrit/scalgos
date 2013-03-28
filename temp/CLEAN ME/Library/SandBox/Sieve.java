import java.awt.*;
import java.math.*;
import java.util.regex.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.lang.Integer.parseInt;
import static java.lang.System.*;
import static java.lang.Math.*;
import java.awt.geom.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class Sieve {

	public static void main(String args[]) {
		int N = 500;

		long t1 = 0, t2 = 0;

        t1 = System.nanoTime();
        BitSet a3 = sieveOfAtkin(N);
        t2 = System.nanoTime();
        System.out.println("SA uses: " + (t2-t1));

		t1 = System.nanoTime();
        BitSet a2 = sieveOfSundaram(N);
		t2 = System.nanoTime();
		System.out.println("SW uses: " + (t2-t1));

		t1 = System.nanoTime();
		BitSet a1 = sieveOfEratosthenes(N);
		t2 = System.nanoTime();
		System.out.println("SE uses: " + (t2-t1));

        a1.xor(a2);
        a2.xor(a3);

		System.out.println(a1 + "\n" + a2);

	}

	static BitSet sieveOfSundaram(int n) {
		BitSet s = new BitSet(n++), p = new BitSet(n);
        for (int i = 3, l = (int)sqrt(n); i < l; i += 2)
			for(int j = 3*i; j < n; j += 2*i)
				s.set(j/2); // do p.clear??
		for (int i = 0; 2*(i = s.nextClearBit(i+1)) < n; p.set(2*i+1));
        p.set(2);
	    return p;
	}

    static BitSet sieveOfAtkin(int n) {
        BitSet p = new BitSet(++n);
        int l = (int)sqrt(n);

        for(int x = 1; x <= l; x++)
            for(int y = 1; y <= l; y++) {
                int k = x*x, a = 3*k, b = y*y,
                   n2 = a+b,
                   n3 = a-b,
                   n1 = n2+k,
                   m2 = n2%12,
                   m3 = n3%12,
                   m1 = n1%12;

                if(n1 < n && (m1 == 1 || m1 == 5))
                    p.set(n1);
                if(n2 < n && m2 == 7)
                    p.set(n2);
                if(n3 < n && x > y && m3 == 11)
                    p.set(n3);
            }

        p.set(2);
        p.set(3);

       /*for(int i = 3; i < n; i = p.nextSetBit(i+1)) {
           for(int j = 1, x; (x = j*i*i) < n; j++)
               p.clear(x);
       }*/

        return p;
    }

	static BitSet sieveOfEratosthenes(int n) {
		BitSet p = new BitSet(++n);
		for(int i = 3; i < n; i += 2)
			p.set(i, i%3 > 0 || i%5 > 0 || i%7 > 0);
		p.set(2);
		for(int i = 2, l = (int)sqrt(n); i < l; i = p.nextSetBit(i+1))
				for(int j = i*i; j < n; j+=2*i)
				   p.clear(j);
		return p;
	}
}