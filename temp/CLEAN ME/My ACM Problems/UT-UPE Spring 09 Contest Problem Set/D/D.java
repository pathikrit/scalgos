import java.util.*;

public class D {

	final static long M = 1000000007;
    final static int K = 51;

    static long C[][] = new long[K][K];
	static HashMap<Integer, Long> cache = new HashMap();

    public static void main(String args[]) {
        pascal();
        Scanner sc = new Scanner(System.in);
        for(int T = sc.nextInt(); T > 0; T--)
            System.out.println(value(sc.nextInt(), sc.nextInt()));
    }

    static long value(int n, int k) {
		int hash = K*n + k;
		if(!cache.containsKey(hash)) {
            long r;
            if(n == 0)
                r = 0;
            else if(n%2 == 1)
                r = (value(n-1, k) + pow(n, k))%M;
            else {
                n /= 2;
                r = (2*value(n, k) + pow(n, k+1))%M;
                for(int i = 1; i < k; i++)
                  r = (r + (((C[k][i]*value(n, i))%M)*pow(n, k-i))%M)%M;
            }
            cache.put(hash, r);
        }
		return cache.get(hash);
	}

	static long pow(long n, long k) {
		if(k == 0)
			return 1;
		long h = pow(n, k/2);
        return k%2 == 0 ? (h*h)%M : (((h*h)%M)*n)%M;
	}

    static void pascal() {
		for(int i = 0; i < K; i++)
		    for(int j = 0; j <= i; j++)
                C[i][j] = j == 0 || j == i ? 1 : (C[i-1][j-1] + C[i-1][j])%M;
    }
 }