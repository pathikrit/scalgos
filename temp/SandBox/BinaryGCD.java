public class BinaryGCD {

	public static void main(String args[]) {
		for(int i = 0; i < 100; i++) {
			int a = (int)(1 + 100*Math.random()),
			    b = (int)(1 + 100*Math.random());

			if(binaryGCD(a,b) != euclidGCD(a,b))
				System.out.println(a + " " + b);
		}
	}

	/*
	 * slightly facter than euclid's aglorithm
	 * O((log_2 (ab)^2)
	 * no check for overflows
	 */
	static int binaryGCD(int a, int b) {
     	if (a == 0 || b == 0)
       		return a | b;
       	int s = 0;
     	for(; ((a | b) & 1) == 0; a >>= 1, b >>= 1, s++);
     	while((a & 1) == 0) a >>= 1;
     	do {
        	for(; (b & 1) == 0; b >>= 1);
			//a = min(a, b), b = abs(a - b)
         	if (a < b) {
            	b -= a;
         	} else {
	            int d = a - b;
	            a = b;
	            b = d;
         	}
	    } while (b != 0);
     	return a << s;
	}

	/////////////
	static int euclidGCD(int a, int b) {
		return b == 0 ? a : euclidGCD(b, a%b);
	}
}