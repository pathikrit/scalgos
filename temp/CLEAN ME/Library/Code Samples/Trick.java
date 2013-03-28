import java.util.*;

public class Trick {

    public static void main(String args[]) {
		System.out.println("/////////////////////////////////////////////////");
		HashSet<Long> h = new HashSet();
		Long l = (long)4;
		h.add(l);
		System.out.println(h.contains(4));

		System.out.println("/////////////////////////////////////////////////");
		int x1 = (int)(20.02*100), x2 = (int)(2.002*1000), x3 = (int)200.2*10;
		System.out.println(x1 + " = " + x2 + " = " + x3);

		System.out.println("/////////////////////////////////////////////////");
		int x[] = {1,2,3};
		rotate1(x);
		System.out.println(Arrays.toString(x));
		rotate2(x);
		System.out.println(Arrays.toString(x));

		System.out.println("/////////////////////////////////////////////////");
		System.out.printf("%n", "hello world", "0k");
		System.out.printf("%s", "hello world\r", "ok");

		System.out.println("/////////////////////////////////////////////////");
		System.out.println(Math.atan(0/1) + " = " + Math.atan(-0/1) + " = " + Math.atan(-0./1) + " = " + Math.atan(-0/1.));
		System.out.println((Math.atan(-0./1) == Math.atan(0/1)) + "\n" +
			               (Math.atan(-0./1) < Math.atan(0/1)) + "\n" +
                           (1/Math.atan(-0./1) == 1/Math.atan(0/1)) + "\n" +
                           (1/Math.atan(-0./1) < 1/Math.atan(0/1)));

		System.out.println("/////////////////////////////////////////////////");
		ArrayList<String> as = new ArrayList<String>();
		HashSet hs = new HashSet<ArrayList>();
		hs.add(as);
		as.add("B8");
		System.out.println(hs.contains(as));
		hs.add(as);
		System.out.println(hs.contains(as));
		as.remove("B8");
		as.add("AW");
		System.out.println(hs.contains(as));

		System.out.println("/////////////////////////////////////////////////");
        //find a string that overflows int in its hascode

		System.out.println("/////////////////////////////////////////////////");
        byte b1 = 23, b2 = 35;
		Object o = b1^b2;
		System.out.println((o instanceof Integer) + " -- " + (o instanceof Byte));

		System.out.println("/////////////////////////////////////////////////");
		for(int i = 127; i < 129; i++) {
			Integer a = i, b = i, c = new Integer(i), d = (Integer)i;
			System.out.println((a == b) + " " + (b == c) + " " + (c == d) + " " + (d == a));
			//System.out.println(a.equals(b) + " " + b.equals(c) + " " + c.equals(d) + " " + d.equals(a));
		}

		System.out.println("/////////////////////////////////////////////////");		
		System.out.println(frack());
    }

    static int frack() {		
		double d1 = Math.atan(-0/1.), d2 = Math.atan(-0./1);		
		Double D1 = Math.atan(-0/1.), D2 = Math.atan(-0./1);	
		
		int c = 0, b = 0;
       			
		while(d1 == d2) {		
			try {
				try {
					return D1.equals(D2) ? --b : ++c;
				} finally {
					return (double)D1 == (double)D2 ? ++c : --b;
				}
			} finally {
				if(D1.doubleValue() == D2.doubleValue())
					break;				
				else
					continue;
			}						
    	}
		
		if(D1.isNaN() || D2.isNaN() || D1.isInfinite() || D2.isInfinite())
		{
			return --b;
		}
		
		if(D1 < D2 || D1 > D2 || D1 == D2)
		{
			return --b;
		}
		
		return D1 <= D2 ? ++c : --b;
	}

    public static void rotate1(int input[]) {
    	final int N = input.length;
        int temp[] = new int[N];
        for (int i = 0; i < N; i++)
            temp[i] = input[(i + 1) % N];
        input = temp;
    }

    public static void rotate2(int input[]) {
    	final int N = input.length;
        int temp = input[0];
        for (int i = 0; i < N-1; i++)
            input[i] = input[i + 1];
        input[N-1] = temp;
    }
}