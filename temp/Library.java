import java.awt.*;
import java.math.*;
import java.util.regex.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.lang.Integer.parseInt;
import static java.util.AbstractMap.*;
import static java.lang.System.*;
import static java.lang.Math.*;
import java.awt.geom.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class Library
{

    public static void main(String cmdLine[])
    {
        /*String url = "http://www.example.com/";
        for(Scanner sc = new Scanner((new URL(url)).openStream()); sc.hasNext();)
        {
            String s =     sc.nextLine();
            print(s);
        }*/


        Language l = Language.JAVA;
        System.out.println(l.getExtension());

        String test = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ potty";
        for(char c : test.toCharArray())
            out.println(c + " -> " + (char)(c^32));
    }

    //toggle 1 or 0
    void print(Object ...o) { assert 1>0 : deepToString(o);}

    //returns 1 if a is greater than b, -1 if a is less than be b, 0 if they are equal
    int compareDoubles(double a, double b)
    {
        double d = a-b;
        return d > EPS ? 1 : d < -EPS ? -1 : 0;
    }

    // - a_min because it fucks other mins in this file
    int a_min(int ...a) {
        int min = INF;
        for(int i : a)
            min = min(min, i);
        return min;
    }

    // returns true iff a*b overflows
    boolean isOverFlow(int a, int b) {
        return a != 0 && a*b/a != b || b/Long.MIN_VALUE == -a;
    }


    /*
     * park-miller random number generator
     * use seed = rng(seed);
     */
    int prng(int seed) {
        return (seed*16807)%Integer.MAX_VALUE;
    }

	// 2 rectangles intersect iff distance from center of one to corner of another is less than the distance from center to any of its own 4 corners

    /*
     * this mod makes sure answer is always between 0 (inclusive) and b (not inclusive)
     */
    int mod(int a, int b) {
        return (a%b) * (int)(signum(a)*signum(b));
    }

    /*
     * returns AB
     * O(n^3)
     */
    double[][] matrixMultiply(double A[][], double B[][])
    {
        int rA = A.length;
        if(rA == 0)
            return new double[0][0];
        int cA = A[0].length, rB = B.length;
        if(cA != rB)
            return null;
        if(cA == 0)
            return new double[0][0];
        int cB = B[0].length;
        double C[][] = new double[rA][cB];
        for(int i = 0; i < rA; i++)
            for(int j = 0; j < cB; j++)
                for(int k = 0; k < cA /*= rB*/; k++)
                    C[i][j] = A[i][k]*B[k][j];
        return C;
    }

    /*
     * solves AX = B, returns X[][] and does not modify A and B
     * O(n^4)
     */
    double[][] solve(double A[][], double B[][])
    {
        int rA = A.length, rB = B.length;
        if(rA != rB)
            return null;
        if(rA == 0 || rB == 0)
            return new double[0][0];
        int cA = A[0].length, cB = B[0].length;
        double X[][] = new double[cA][cB];
        for(int c = 0; c < cB; c++)
        {
            double b[] = new double[rB];
            for(int r = 0; r < rB; r++)
                b[r] = B[r][c];
            double x[] = solve(A, b);
            for(int r = 0; r < cA; r++)
                X[r][c] = x[r];
        }
        return X;
    }

    /*
     * solves Ax = b, returns X, does not modify A and B
     * O(n^3)
     */
    double[] solve(double A[][], double b[])
    {
        int rA = A.length, rB = b.length;
        if(rA != rB)
            return null;
        if(rA == 0)
            return new double[0];
        int cA = A[0].length;
        double X[] = new double[cA], aug[][] = new double[rA][cA+1];
        for(int i = 0; i < rA; i++)
        {
            aug[i] = Arrays.copyOf(A[i], cA+1);
            aug[i][cA] = b[i];
        }

        reducedEchelonForm(aug);

        for(int i = 0; i < rA; i++)
            if(aug[i][cA] > 0)
            {
                int j;
                for(j = 0; j < cA-1 && aug[i][j] == 0; j++);
                if(j == cA)
                    return null;
            }

        for(int i = 0, c = 0; i < rA && c < cA; i++, c++)
            if(aug[i][c] == 0)
                i--;
            else
                X[c] = aug[i][cA];

        return X;
    }

    /*
     * All zero rows are at the bottom of the matrix
     * The leading entry of each nonzero row after the first occurs to the right of the leading entry of the previous row
     * The leading entry in any nonzero row is 1
     * All entries in the column above and below a leading 1 are zero
     */
    void reducedEchelonForm(double a[][])
    {
        echelonForm(a);
        int R = a.length, C = a[0].length;
        for(int i = 0, c = 0; i < min(R, C-1) && c < C-1; i++, c++)
            if(a[i][c] == 0)
                i--;
            else
                for (int row = i-1; row >= 0; row--)
                    for (int col = C-1; col >= c; col--)
                        a[row][col] -= a[i][col]*a[row][c];
    }

    /*
     * All nonzero rows are above any rows of all zeroes
     * The leading coefficient of a row is always strictly to the right of the leading coefficient of the row above it
     */
    void echelonForm(double a[][])
    {
        triangularForm(a);
        int R = a.length, C = a[0].length;
        for(int i = 0; i < R; i++)
        {
            int c;
            for(c = 0; c < C && a[i][c] == 0; c++);
            if(c == C) break;
            for(int j = C; --j >= c; a[i][j] /= a[i][c]);
        }
    }

    /*
     * upper triangular form
     * recquired for calculating determinant
     */
    void triangularForm(double aug[][])
    {
        int R = aug.length, C = aug[0].length;
        for(int i = 0, c = 0; i < min(R, C-1) && c < C-1; i++, c++)
        {
            if(aug[i][c] == 0)
            {
                int r;
                for(r = i+1; r < R && aug[r][c] == 0; r++);
                if(r == R)
                {
                    i--;
                    continue;
                }
                else
                {
                    double t[] = aug[i];
                    aug[i] = aug[r];
                    aug[r] = t;
                }
            }
            for(int j = i+1; j < R; j++)
                for(int k = C-1; k >= c; k--)
                    aug[j][k] -= aug[j][c]*aug[i][k]/aug[i][c];
        }
    }

    /*
     * modifies mat to its upper triangular form
     * O(n^3)
     * wrong answer or error if not square
     */
    double determinant(double mat[][])
    {
        triangularForm(mat);
        double d = 1;
        for(int i = 0; i < mat.length; d *= mat[i][i++]);
        return d;
    }

    /*
     * calculates determinant - recursively
     * O(n!)
     */
    double recursive_determinant(double mat[][])
    {
        int n = mat.length;
        if(n == 0) return 1;
        double det = 0.0;
        for(int i = 0; i < n; i++)
        {
            double newMat[][] = new double[n-1][n-1];
            for(int j = 1; j < n; j++)
                for(int k = 1; k < n; k++)
                    newMat[j-1][k-1] = mat[j][(i+k)%n];
            det += (n%2==0 && i%2 == 1 ? -1 : 1)*mat[0][i]*recursive_determinant(newMat);
        }
        return det;
    }

    //computes a0 + a1x + a2x^2 + ...
    double evaluateFunction(double a[], double x)
    {
        int n = a.length;
        double xp[] = new double[n], f = a[0];
        xp[0] = 1;
        for(int i = 1; i < n; i++)
            f += a[i]*(xp[i] = x*xp[i-1]);
        return f;
    }

    /*
     * receives a N x D matrix. N = D+1
     * computes the volume enclosed by N points in D dimensions (area of triangle if D = 2)
     */
    double simplexVolume(double points[][])
    {
        int N = points.length, D = points[0].length;
        double matrix[][] = new double[N][N];

        for(int i = 0; i < N; i++)
            for(int j = 0; j < N; j++)
                matrix[i][j] = j<N-1 ? points[i][j] : 1;

        return abs(1.0 * determinant(matrix)/factorial(D));
    }

    //area of 2D triangle
    double areaOfTriangle(double x[], double y[])
    {
        double matrix[][] = {{x[0],y[0],1},{x[1],y[1],1},{x[2],y[2],1}};
        return determinant(matrix)/2;
    }

    //area of 3D triangle
    double areaOfTriangle(double x[], double y[], double z[])
    {
        if(z == null) z = new double[3];
        double a[] = {x[0]-x[1], y[0]-y[1], z[0]-z[1]}, b[] = {x[2]-x[1], y[2]-y[1], z[2]-z[1]};
        return sqrt(pow(a[1]*b[2]-a[2]*b[1],2) + pow(a[0]*b[2]-a[2]*b[0],2) + pow(a[0]*b[1]-a[1]*b[0],2))/2;
    }

    /*
     * returns area of the polygon specified by (x,y)
    *  must be in clockwise or anticlockwise order - doesn't do error checks!
    */
    double polygonArea(double x[], double y[])
    {
        double area = 0;
        for(int i = 1; i < x.length-1; i++)
            area += (x[i]-x[0])*(y[i+1]-y[0])-(x[i+1]-x[0])*(y[i]-y[0]);
        return area/2;
    }

	// K-nearest point for N points:
	// Sort by x-axis and y-axis, examine the 4 neighbouring points in the 2 sorted arrays
	// Initial sort is O(N log N) - further queries in O(K)
	// For dynamic addition of new points to original set, use threaded-BST

    /*
     * number of integers in (a,b) that is divisible by d
     * [a,b] = (a-1, b+1)
     * O(1)
     * b must be > a, weird answer otherwise
     * if overflow, switch everything to longs
     */
    int numOfMultiples(int a, int b, int d)
    {
        if(d < 0) d = -d;    //becoz of mod fuck ups
        if(a < 0 && b < 0) {
            int t = a;
            a = -b;
            b = -t;            //might overflow here for t = -2^31
        }
        if(a<0)
            return numOfMultiples(0,-a,d) + numOfMultiples(0,b,d) + 1; //+1 for 0
        long l = (long)b-a;
        int q = (int)(l/d), r = (int)(l%d);
        if((b-r)%d > b%d)
            q++;
        if(b%d == 0)
            q--;
        return q;
    }

    /*
     * returns true iff an undirected graph can be constructed with n vertices such that degree of ith vertex = d[i]
     * may sort d in non-increasing order
     * use of counting sort
     * O(n^2)
     */
    boolean erdosGallai(int d[])
    {
        int n = d.length, c[] = new int[n], s[] = new int[n+1];
        for(int i : d)
            if(0 <= i && i < n)
                c[i]++;
            else
                return false;

        for(int i = n, j = 0; --i >= 0; )
            while(c[i]-- > 0)
                s[j+1] = s[j] + (d[j++] = i);

        for(int r = 0, i; ++r < n; )
        {
            for(i = r; i<n && r<d[i]; i++);
            if(s[r] + s[i] > r*(i-1) + s[n])
                return false;
        }

        return s[n]%2 == 0;
    }

    /*
     * supports bases 1 to 36 - no error checking
     * use BigDecimal if overlfows
     */
    String baseChange(String number, int thisBase, int toBase) {return Long.toString(Long.parseLong(number, thisBase), toBase);}

    //same as above
    String changeBase(String n, int b1, int b2)
    {
        String an = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", a = "";
        long tn = 0;
        for(int i = n.length(); --i >= 0; tn += an.indexOf(""+n.charAt(i)) * pow(1.0*b1, 1.0*(n.length()-i-1)));
        do
            a = an.charAt((int)(tn%b2)) + a;
        while((tn /= b2) > 0);
        return a;
    }

    //supports bases 2 to 10 - no error checking!
    long baseChange(long number, int thisBase, int toBase)
    {
        long decimal = 0, ans = 0;
        for(int i = 0; number > 0; i++)    //convert number from thisBase to base 10
        {
            long digit = number%10;
            number /= 10;
            decimal += digit*pow(thisBase, i);
        }
        for(int i = 0; decimal > 0; i++)    //convert number from base 10 to toBase
        {
            long remainder = decimal%toBase;
            decimal = decimal/toBase;
            ans += remainder*pow(10, i);
        }
        return ans;
    }

    //try at coding BigInteger's add
    String add(String a, String b)
    {
        if(a.length() < b.length())
        {
            String temp = b;
            b = a;
            a = temp;
        }
        while(b.length() < a.length()) b = "0"+b;
        String s = "";
        int c = 0;
        for(int i = a.length(), num = 0; --i>=0 && (c=(num = a.charAt(i)+b.charAt(i)-96+c)/10)<2; s = num%10+s);
        return c>0 ? "1"+s : s;
    }

    // a sample use of binary search
    double sqrRoot(double a)
    {
        double low = 0, high = a, root = a/2;
        for(int i = 0; ++i < 100; root = (high+low)/2) {
            double f = f(root);
            if(f > a+EPS)
                high = root;
            else if(f < a-EPS)
                low = root;
            else
                return root;
        }
        return root;
    }

	int binarySearch(int low, int high, int value) {
		if(high < low)
           return -1; // not found

		int mid = low + ((high-low)/2); //saves high+low overflow

		if(mid > value)
			high = mid-1;
		else if(mid < value)
			low = mid+1;
		else
			return mid;
		return binarySearch(low, high, value);
	}


    /*
     * uses ternary search to find maxima/minima of f between l and r
     * f must be U (or upside down U) between l and r
     * r>l
     * MAX tells method to search maxima or minima
     * c can be as low as 100 but better be safe than sorry
     */
    double ternarySearch(double l, double r, final boolean MAX) {
        for(int c = 10000; c >= 0; c--) {
            double ll = (2*l+r)/3, rr = (l+2*r)/3;
            if(f(ll) > f(rr) ^ MAX)
                l = ll;
            else
                r = rr;
         }
        return (l+r)/2;
     }

    /*
     * test function for ternarySearch and sqrRoot (binarySearch)
     */
     double f(double x) {
         return x+3;
     }

    /*
     * Works from dates after Sep 14, 1752 (New System of Gregorian Calendar (UK adoption))
     * May crash or return junk value for negative or other invalid inputs.
     * Sunday = 1, Monday = 2, ... Saturday = 0 (not 7)
     */
    int getDay(int d, int m, int y)
    {
         int k[] = {1, 4, 4, 0, 2, 5, 0, 3, 6, 1, 4, 6}, a = 7;
         switch(y/100)
        {
             case 18: a += 2; break;
             case 20: a -= 1; break;
         }
         if((y%4 == 0 && (y%100 != 0 || y%400 == 0)) && m <= 2)
             a--;
        y %= 100;
        a += y/12 + y%12 + (y%12)/4 + k[m-1] + d;
        return a%7;
    }

    /*
    * use quantity, max coins all set to INF
    * change+1 denotes INF i.e. impossile
    */
    int minChange(int coins[], int quantity[], int change, int maxCoins)
    {
        int numCoins[] = new int[change+1], coinsUsed[][] = new int[change+1][coins.length];
        for(int c = 1; c <= change; c++)
        {
            numCoins[c] = change+1;
            int coin = -1, number = -1;
            for(int i = 0; i < coins.length; i++)
            {
                for(int n = 0; n <= quantity[i] & n*coins[i] <= c; n++)
                {
                    int pos = c - n*coins[i];
                    if(numCoins[c] > numCoins[pos]+n && coinsUsed[pos][i] + n <= quantity[i] && numCoins[pos] + n <= maxCoins)
                    {
                        numCoins[c] = numCoins[pos]+n;
                        coin = i;
                        number = n;
                    }
                }
            }
            if(coin >= 0)
            {
                coinsUsed[c] = coinsUsed[c - number*coins[coin]].clone();
                coinsUsed[c][coin] += number;
            }
        }
        return numCoins[change];
    }

    /*
     * returns minimum number of coins of type denomination coins[] to exactly make change.
     * returns change+1 if impossbile (aka infnity)
     */
    int minChange(int coins[], int change)
    {
        int minChange[] = new int[change+1]; //use HashMap if out of space
        sort(coins);
        for(int i = 1; i <= change; i++)
        {
            minChange[i] = change+1;
            for(int j = 0; j < coins.length && coins[j] <= i; j++)
                minChange[i] = min(minChange[i], 1 + minChange[i - coins[j]]);
        }
        return minChange[change];
    }

    /*
     * recursive minChange - must be called with cache[change+1]
     * returns INF, if impossible
     */
    int minChange(int coins[], int change, int cache[])
    {
        if(change == 0) return 0;
        if(change < 0) return INF;
        if(cache[change] > 0) return cache[change];
        int ans = INF;
        for(int i = 0; i < coins.length; i++)
            ans = min(ans, 1+minChange(coins, change-coins[i], cache));
        return cache[change] = ans;
    }

    /* b = boxes, a.length = biscuits*/
    boolean nextCombination(int a[], int b)
    {
        int i = a.length;
        for(; i-- > 0 && ++a[i] == b; a[i] = 0);
        return i>=0;
    }

    /*
     * always k values of b is true and n-k values of b is false where n = b.length
     * first call with b[i] = true for all 0 <= i < k and b[i] = false for all i >= k
     * this works by basically flipping all 01 to 10 starting with 0000000111 (for example) and pushing all 1s to the right of first found 01 to the rightmost part
     * TODO: Convert this to use a bitset
     * do-while loop
     * nCk times
     */
    boolean nextChooseK(boolean b[])
    {
        for(int i = 0, n = b.length; i < n-1; i++)
            if(b[i] && !b[i+1])
            {
                b[i] = false;
                b[i+1] = true;
                for(int s = 0, e = i; s < e; )  //start, end
                    if(!b[s] && b[e])
                    {
                        b[s] = true;
                        b[e] = false;
                    }
                    else
                    {
                        if(b[s])
                            s++;
                        if(!b[e])
                            e--;
                    }
                return true;
            }
        return false;
    }

    /*
     * returns nth permutation of the list {0, 1, ... , l-1}
     * no error checking - n must be less than l!
     * O(l^2)
     */
    int[] nthPermutation(int n, int l) {
        ArrayList<Integer> start = new ArrayList<Integer>(l);
        int  perm[] = new int[l];
        long fact[] = new long[l];
        for(int i = 0; i<l; i++) {
            fact[i] = i == 0 ? 1 : i*fact[i-1];
            start.add(i);
        }
        for(int i = l; --i>=0; n %= fact[i])
            perm[l-1-i] = start.remove((int)(n/fact[i]));
        return perm;
    }
    
    /*
     * Different from next_permutation
     * Faster than nextPermuation for most cases
     * But always generate n! repetitions irrespective of repetitions and starting point
     */
    private static void permute(int[] a, int n) {
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                permute(a, n-1);
                swap(a, n%2 == 1 ? 0 : i, n-1);
            }
            return;
        }
        // Main algorithm here
        System.out.println(Arrays.toString(a));
    }

    //needs nextPermuation
    boolean previousPermuation(int a[])
    {
        int n = a.length,  t[] = new int[n];
        for(int i = 0; i < n; t[i] = -a[i++]);
        boolean r = nextPermutation(t);
        for(int i = 0; i < n; a[i] = -t[i++]);
        return r;
    }

	/**
	 * e.g. doPermute(new ArrayDeque<Integer>(), new boolean[3], 1, 2, 3);
	 * TODO: Use bitset for used
	 */
	void doPermute(ArrayDeque<Integer> out, boolean used[], int ...a) {
		if(out.size() == a.length) {
			System.out.println(out);
			return;
		}
		
		for(int i = 0; i < a.length; i++) {
			if(used[i]) continue;
			out.add(a[i]);
			used[i] = true;
			doPermute(out, used, a);
			used[i] = false;
			out.removeLast();
		}
	}
	
	/**
	 * e.g. doCombine(new int[]{1,2,3}, new ArrayDeque<Integer>(), 0)
	 */
	void doCombine(int a[], ArrayDeque<Integer> out, int level) {
		for(int i = level; i < a.length; i++) {
			out.add(a[i]);
			System.out.println(out);
			doCombine(a, out, i+1);
			out.removeLast();
		}
	}
	
    /*
     * makes sure all boxes (b) have atleast 1 element in it.
     * a.length must be >= b - nor error checking!
     * every integer between 0 and b is covered inside a
     * a[i] is between 0 and b-1 inclusive and indicates ball i is in box a[i]
     * do not use "do-while" - use "while" instead to take care of first case
     */
    boolean nextBoseEinstein(int a[], int b)
    {
        boolean c = true, f[] = new boolean[b];
        for(int i = a.length; i-- > 0; c &= a[i]==b, f[a[i]%=b] = true)
            if(c)
                a[i]++;
        for(boolean j: f)
            if(!j)
                return !c && nextBoseEinstein(a, b);
        return !c;
    }

    //overflows after C(66, 33) ie n > 66
    long C(int n, int r)
    {
        long c[][] = new long[n+1][r+1];
        for(int i = 0; i <= n; i++)
            for(int j = 0; j <= r; j++)
                c[i][j] = (j == 0 || j >= i) ? 1 : c[i-1][j-1] + c[i-1][j];
        return c[n][r];
    }

    //return log of a to the base b - no error checking
    double logBase(long a, long b)
    {
        return log(a)/log(b);
    }

    //returns a*e[0] + b*e[1] = gcd(a, b)
    int[] extendedEuclidean(int a, int b)
    {
        if(b == 0)
            return new int[]{1, 0};
        int e[] = extendedEuclidean(b, a%b);
        return new int[]{e[1], e[0] - (a/b)*e[1]};

    }

    /*
     * if a or b negative, unpredictable answer - best to always mod(answer) to get positive answer always.
     * gcd(a, b) where a*b = 0 returns correct answer (except when a or b is -ve)
     */
    int gcd(int a, int b)
    {
        return b == 0 ? a : gcd(b, a%b);
    }

    //gcd required
    int lcm(int a, int b)
    {
        return a/gcd(a, b) * b;
    }

    //overflow at n = 33
    long catalan(int n)
    {
        return n > 0 ? (4*n+2)*catalan(n-1)/(n+2) : 1;
    }

    //overflow at n = 37, best we can do with 64-bit signed integers
    long catalan(int n, long cache[])
    {
        if(n == 0) return 1;
        if(cache[n] > 0) return cache[n];
        for(int i = 0; i < n; cache[n] += catalan(i)*catalan(n-++i));
        return cache[n];
    }

    /*
     * Stirling numbers of the second kind
     * S(n,k) = number of ways to partition a set of n elements into k non-empty sets
     * overflow at n > 26
     * exception if called with k>n or k or n negative
     */
    long stirling2(int n, int k, long S[][])
    {
        if(n == k) return 1;
        if(k == 0) return 0;
        if(S[n][k] > 0) return S[n][k];
        return S[n][k] = k*stirling2(n-1, k, S) + stirling2(n-1, k-1, S);
    }

    //for long overflow at n > 92, for int overflow at n > 46
    long fibonacci(int n)
    {
        if(n == 0)
            return 0;
        long f[] = new long[n+1];
        f[1] = 1;
        for(int i = 1; ++i <= n; f[i] = f[i-1] + f[i-2]);
        return f[n];
    }

    //returns number of positive integers less than n that are coprime to n
    int phi(int n)
    {
        int phi = n;
        boolean comp[] = new boolean[n+1];
        for(int i = 2; i <= n; i++)
            if(!comp[i])
            {
                for(int j = i*i; j <= n; j += i)
                    comp[j] = true;
                if(phi%i == 0)
                    phi = phi/i * (i-1);
            }
        return phi;
    }

    //overflows after P(20, 20) i.e n >= 21
    long P(int n, int r)
    {
        long p = 1;
        for(int i = n-r; i < n; p *= ++i);
        return p;
    }

    //overflows after n = 20, alrite at n = 20
    long factorial(int n)
    {
        return n>1 ? n*factorial(n-1) : 1;
    }

    //return pow(a, b) % c
    long modPow(int a, int b, int c)
    {
        if(b == 0)
            return 1;
        long h = modPow(a, b/2, c);
        return (((h*h)%c)*(b%2 == 0 ? 1 : a))%c;
    }

	// returns (a/b)%c
	long modDiv(int a, int b, int c) {
        return ((a %= c) *inverse(b, c))%c;
	}

	// returns x such that x*a = 1 mod p, p is prime
	long inverse(int a, int m) {
	    return modPow(a, m-2, m);
	}

    //returns pow(a, b)
    long powInt(int a, int b)
    {
        if(b == 0)
            return 1;
        long h = powInt(a, b/2);
        return h*h* (b%2 == 0 ? 1 : a);
    }

    /*
     * number of dearrangements
     * overflow for n > 22
     */
    long dearangement(int n)
    {
        long d[] = new long[n+1];
        d[0] = 1;
        for(int i = 0; i++ < n; d[i] = i*d[i-1] + (i%2 == 0 ? 1 : -1));
        return d[n];
    }

    /*
     * k fixed points
     * overflow for n > 22
     */
    long partialDearangement(int n, int k)
    {
        if(k == n) return 1;
        if(k == n-1) return 0;
        return C(n,k)*dearangement(n-k);
    }

    //no error checking e.g. sigma(r) = n
    long genC(int n, int ...r)
    {
        return genChelper(n, r, r.length);
    }

    //helper for above method
    long genChelper(int n, int r[], int l)
    {
        if(l == 1) return C(n, r[0]);
        r[l-2] += r[l-1];
        return C(r[l-2], r[l-1]) * genChelper(n, r, --l);
    }

    /*
     * call this only once for quick on hand data
     * run-time is O(sqrt(n))
     * do not use this, use fermatTest
     */
    boolean isPrime(int n)
    {
        for(int i = 2; i*i <= n; i++)
            if(n%i == 0)
                return false;
        return n>1;
    }

    //f[i] contains the number of different factors of i (including 1 and i)
    int[] allFactors(int n)
    {
        int f[] = new int[n+1];
        for(int i = 1; i <= n; i++)
            for(int j = i; j <= n; j+=i)
                f[j]++;
        return f;
    }

    //prime[i] = true iff i is prime, prime[0] = prime[1] = false and i can be from 0 to n (both inclusive)
    boolean[] sieveOfEratosthenes(int n) //max n = 2^15
    {
        boolean prime[] = new boolean[n+1];
        fill(prime, 2, n, true);
        for(int i = 2; i <= n; i++)
            if(prime[i])
                for(int j = i*i; j <= n; j+=i) //check for i*i overflow
                    prime[j] = false;
        return prime;
    }

    /*
     * returns all primes <= n
     * O(n log n)
     * no input validation
     */
    ArrayList<Integer> sieveOfSundaram(int n)   {
        ArrayList<Integer> p = new ArrayList();
        if(n > 1)
            p.add(2);
        BitSet s = new BitSet(n = (n+1)/2);
        for(int i = 1; 2*i < n; i++)
            for(int j = 1, x; (x = i + j + 2*i*j) < n; j++)
                s.set(x);
        for (int i = 0; (i = s.nextClearBit(i+1)) < n; p.add(2*i+1));
        return p;
    }

    //fills an array with value, the type of value can ofcourse be changed
    void deepFill(Object array, long value)
    {
        if(array instanceof long[])
            Arrays.fill((long[])array, value);
        else
            for(Object subArray : (Object[])array)
                deepFill(subArray, value);
    }

    //my implementation of various sorting algorithms
    void insertionSort(int a[])
    {
        for(int i = 1; i < a.length; i++)
            for(int j = i; j > 0 && a[j] < a[j-1]; a[j] = a[j]^a[j-1]^(a[j-1] = a[j--]));
    }
    void bubbleSort(int a[])
    {
        for(int i = 0; i < a.length; i++)
            for(int j = i; ++j < a.length; a[j] = a[j]<a[i] ? a[j]^a[i]^(a[i] = a[j]) : a[j]);
    }
    void selectionSort(int a[])
    {
        for(int i = 0; i < a.length; i++)
        {
            int min = i;
            for(int j = i+1; j < a.length; j++)
                if(a[min] < a[j])
                    min = j;
            a[i] = a[i]^a[min]^(a[min] = a[i]);
        }
    }
    void mergeSort(int a[])
    {
        int n = a.length;

        if(n == 1)
            return;

        int left[] = new int[n/2], right[] = new int[n - n/2];

        for(int i = 0; i < n/2; i++)
        {
            left[i] = a[i];
            right[i] = a[n/2+i];
        }

        if(n%2 == 1)
            right[n/2] = a[n-1];

        mergeSort(left);
        mergeSort(right);

        for(int l = 0, r = 0; l < left.length || r < right.length;)
            if(r == right.length)
                a[l+r] = left[l++];
            else if(l == left.length)
                a[l+r] = right[r++];
            else if(left[l] < right[r])
                a[l+r] = left[l++];
            else
                a[l+r] = right[r++];
    }

	// LCA in O(1) and O(height):
	// Figure depth of node1 and node2 in O(height)
	// Lift lower to same level such that node1 and node2 are in same level O(height)
	// Then pair wise climb up

    /*
     * given a DAG in mat with s and t as start and end vertices and mat[i][j] = distance between i and j
     * mat[i][j] = INF means i and j are disconnected
     * before calling this set adjList = matrix2List(adjMat)
     * returns the longest weighted path from s to t
     * does not error check for errors like graph not being a DAG
     * initialize cache array of size V with -INF
     * after completion, cache[i] = longest path from i to t
     */
    int longestPath(LinkedList<SimpleEntry> adjMat[], int s, int t, int cache[]) {
        if(t == s)
            return 0;

        if(cache[s] > -INF)
            return cache[s];

        for(SimpleEntry<Integer, Integer> v : adjMat[s])
            cache[s] = max(cache[s], v.getValue() + longestPath(adjMat, v.getKey(), t, cache));

        return cache[s];
    }

    /*
     * Naive Prim's Minimum Spanning Tree algorithm
     * O(V^3)
     * if u, v disconnected mat[u][v] = INF
     * mat[i][i] = 0
     */
    public static ArrayList<int[]> primsMST(int mat[][]) {
        final int V = mat.length;
        HashSet<Integer> visited = new HashSet(), unvisited = new HashSet();
        ArrayList<int[]> spanningEdges = new ArrayList();
        visited.add(0);
        for(int i = 1; i < V; i++)
            unvisited.add(i);

        while(visited.size() < V) {
            int u = -1, v = -1, w = INF;
            for(int i : visited)
                for(int j : unvisited)
                    if(mat[i][j] <= w) {
                        u = i;
                        v = j;
                        w = mat[i][j];
                    }
            assert u > -1 && v > -1;
            spanningEdges.add(new int[]{u, v});
            unvisited.remove(v);
            visited.add(v);
        }
        return spanningEdges;
    }

    /*
     * modifies flowNet to residual network in maxflow
     * O((|V|^2)*|E|) = O(n^4)
     * no error checking for snk - src edges
     */
    int edmondsKarp(int flowNet[][], int src, int snk)
    {
        int n = flowNet.length;

        //re orient all negative edges
        for(int i = 0; i < n; i++)
            for(int j = 0; j < i; j++)
                if(flowNet[i][j] < 0)
                {
                    flowNet[j][i] -= flowNet[i][j];
                    flowNet[i][j] = 0;
                }
        for(int i = 0; i < n; i++)
            for(int j = i; j < n; j++)
                if(flowNet[i][j] < 0)
                {
                    flowNet[j][i] -= flowNet[i][j];
                    flowNet[i][j] = 0;
                }

        for(int maxFlow = 0, bottleneck; /*while there exists an augmenting path*/; maxFlow += bottleneck)
        {
            //bfs starts - search for an augmenting path
            boolean visited[] = new boolean[n];
            int parent[] = new int[n];
            ArrayDeque<Integer> queue = new ArrayDeque();
            parent[src] = parent[snk] = -1;
            visited[src] = true;
            queue.addLast(src);
            while(!queue.isEmpty())
            {
                int v = queue.removeFirst();
                if(v == snk)
                    break;
                for(int i = 0; i < n; i++)
                    if(flowNet[v][i] > 0 && !visited[i])
                    {
                        queue.addLast(i);
                        parent[i] = v;
                        visited[i] = true;
                    }
            }

            if(parent[snk] == -1) //no more augmenting path left
                return maxFlow;

            //find bottleneck edge in the augmenting path found
            bottleneck = INF;
            for(int v = snk; parent[v] != -1; v = parent[v])
                bottleneck = min(bottleneck, flowNet[parent[v]][v]);

            //push flow throw augmenting path - update flowNet to residual network
            for(int v = snk; parent[v] != -1; v = parent[v])
            {
                flowNet[parent[v]][v] -= bottleneck;
                flowNet[v][parent[v]] += bottleneck;    //we might push back flow later, maybe above line was a wrong descision
            }
        }
    }

    /*
     * w[i][j] = amount bidder j is willing to pay for item i (0 if he is not bidding)
     * run time is O(nm^2) where n = #of items and m = #of bidders
     * resets negative bids in w to 0
     * returns a, where a[i] = j means ith item got assigned to bidder j
     * a[i] = -1 means item i did not get assigned
     * for minimizing set w[i][j] = max(w) - w[i][j]
     * for assigning all, w[i][j] = min(w) + w[i][j]
     */
    int[] hungarianMethod(int w[][])
    {
        final int n = w.length, m = w[0].length, PHI = -1, NOL = -2;
        boolean[] x[] = new boolean[n][m], ss = new boolean[n], st = new boolean[m];
        int[] u = new int[n], v = new int[m], p = new int[m], ls = new int[n], lt = new int[m], a = new int[n];
        int f = 0;

        for(int i = 0; i < n; i++)
            for(int j = 0; j < m; j++)
                f = max(f, w[i][j]);

        fill(u, f);
        fill(p, INF);
        fill(lt, NOL);
        fill(ls, PHI);
        fill(a, -1);

        while(true)
        {
            f = -1;
            for(int i = 0; i < n && f == -1; i++)
                if(ls[i] != NOL && !ss[i])
                    f = i;

            if(f != -1)
            {
                ss[f] = true;
                for(int j = 0; j < m; j++)
                    if(!x[f][j] && u[f] + v[j] - w[f][j] < p[j])
                    {
                        lt[j] = f;
                        p[j] = u[f] + v[j] - w[f][j];
                    }
            }
            else
            {
                for(int i = 0; i < m && f == -1; i++)
                    if(lt[i] != NOL && !st[i] && p[i] == 0)
                        f = i;

                if(f == -1)
                {
                    int d1 = INF, d2 = INF, d;
                    for(int i : u)
                        d1 = min(d1, i);

                    for(int i : p)
                        if(i > 0)
                            d2 = min(d2, i);

                    d = min(d1, d2);

                    for(int i = 0; i < n; i++)
                        if(ls[i] != NOL)
                            u[i] -= d;

                    for(int i = 0; i < m; i++)
                    {
                        if(p[i] == 0)
                            v[i] += d;
                        if(p[i] > 0 && lt[i] != NOL)
                            p[i] -= d;
                    }

                    if(d2 >= d1)
                        break;
                }
                else
                {
                    st[f] = true;
                    int s = -1;

                    for(int i = 0; i < n && s == -1; i++)
                        if(x[i][f])
                            s = i;

                    if(s == -1)
                    {
                        for(int l,r ;;f=r)
                        {
                            r = f;
                            l = lt[r];

                            if(r >= 0 && l >= 0)
                                x[l][r] = !x[l][r];
                            else
                                break;

                            r = ls[l];
                            if(r >= 0 && l >= 0)
                                x[l][r] = !x[l][r];
                            else
                                break;
                        }

                        fill(p, INF);
                        fill(lt, NOL);
                        fill(ls, NOL);
                        fill(ss, false);
                        fill(st, false);

                        for(int i = 0; i < n; i++)
                        {
                            boolean ex = true;
                            for(int j = 0; j < m && ex; j++)
                                ex = !x[i][j];
                            if(ex)
                                ls[i] = PHI;
                        }
                    }
                    else
                        ls[s] = f;
                }
            }
        }

        for(int i = 0; i < n; i++)
            for(int j = 0; j < m; j++)
                if(x[i][j])
                    a[j] = i;
        return a;
    }
    
    /**
     * Is there a subset of values which sum to target?
     * O(values.ength * target)
     * Can be used to answer partition problem
     * recursive canAchieve(vals, target, i = N-1) = canAchive(vals, target, i-1) or canAchice(vals, target-val[i], i-1)
     */
    boolean numWaysToAcheiveSum(int values[], int target) {
        final int N = values.length;
        int ways[][] = new int[N+1][target+1];     //way[i][j] = number of ways of achieving sum j with first i values
        ways[0][0] = 1;     // 1 way to achieve 0 sum using 0 values
        
        for(int i = 0; i < N; i++) {
            for(int j = 0; j <= target; j++) {
                ways[i+1][j] = ways[i][j] + (j >= values[i] ? ways[i][j - value[i]] : 0);
            }            
        }
        return values[N][target];
    }

    /*
     * Finds the optimal selection of values[x] with weights[x] and quantity[x]
     * such that maximum value is selected and total weight is less than maxWeight
     * and no more than quantity[x] of item x is selected.
     * and total number of items selected is no more than maxItem
     * no error catching
     * works only for positive integer weights & maxWeight
     * works for negative values
     * for 0-1 knapsack fill(quantity, 1) & maxItem = infinity
     * for unlimited, fill(quantity, infinity) & maxItem = infinity
     */
    double discreteKnapSack(double values[], int weights[], int quantity[], int maxWeight, int maxItem)
    {
        double maxValue[] = new double[maxWeight+1];
        int weightUsed[] = new int[maxWeight+1],
            used[][] = new int[maxWeight+1][weights.length],
            numItems[] = new int[maxWeight+1];

        for(int i=0, max = 0; i <= maxWeight; i++)
        {
            int item= -1, number= -1;

            maxValue[i] = maxValue[max];
            weightUsed[i] = weightUsed[max];
            used[i] = used[max].clone();
            numItems[i] = numItems[max];

            for(int j=0; j<weights.length; j++)
            {
                for(int k=0; k <= quantity[j] && k*weights[j] <= i; k++)
                {
                    int pos = i-k*weights[j];
                    if(maxValue[i]<maxValue[pos]+k*values[j] && used[pos][j]+k<=quantity[j] && numItems[pos]+k<=maxItem)
                    {
                        maxValue[i] = maxValue[pos] + k*values[j];
                        item = j;
                        number = k;
                    }
                }
            }

            if(maxValue[max] < maxValue[i])
                max = i;

            if(item>=0)
            {
                weightUsed[i] = weightUsed[i-number*weights[item]] + number*weights[item];
                used[i] = used[i-number*weights[item]].clone();
                used[i][item] += number;
                numItems[i] = numItems[i-number*weights[item]] + number;
            }

            //System.out.println(i + ".\t" + maxValue[i] + "\t" + weightUsed[i] + "\t" + Arrays.toString(used[i]));
        }

        System.out.println("Best strategy for maximum weight = "+maxWeight+" and maximum items = "+maxItem+" is:");
        for(int line = 0, i = 0; i < weights.length; i++)
            if(used[maxWeight][i] > 0)
                System.out.println("\n" + ++line+". Pick "+used[maxWeight][i]+" of item \'"+(i+1)+"\':\n\tweight = "+used[maxWeight][i]+" X "+weights[i]+" = "+(used[maxWeight][i]*weights[i])+"\n\tvalue = "+used[maxWeight][i]+" X "+values[i] + " = " + (used[maxWeight][i]*values[i])+".");
        System.out.println("\nThus " + numItems[maxWeight] + " item" + (numItems[maxWeight]>0?"s are":" is")+ " selected for a total weight = " + weightUsed[maxWeight] + " and a total value = " + maxValue[maxWeight]);

        return maxValue[maxWeight];
    }

    //can break up weights!
    double continuosKnapSack(double values[], double weights[], double maxWeight)
    {
        int n = weights.length;
        double valuePerWeight[] = new double[n], ans = 0;

        for(int i=0; i<n; i++)
            valuePerWeight[i] = values[i]/weights[i];

        while(maxWeight > 0)
        {
            int max = -1;
            double maxValuePerWeight = 0;

            for(int i = 0; i < n; i++)
                if(maxValuePerWeight < valuePerWeight[i])
                {
                    maxValuePerWeight = valuePerWeight[i];
                    max = i;
                }

            if(max >= 0)
            {
                ans += valuePerWeight[max]*(min(weights[max], maxWeight));
                maxWeight -= weights[max];
                valuePerWeight[max] = -1;
            }
            else
                break;
        }
        return ans;
    }

    /*
     * returns the fewest operations needed to convert String a to String b.
     * call with cache[a.length][b.length]
     */
    int editDistance(String a, String b, int cache[][])
    {
        final int DEL = 1, INS = 1, REP = 1;
        int x = a.length(), y = b.length();
        if(cache[x][y] >= 0)
            return cache[x][y];
        if(x*y == 0)
            return cache[x][y] = max(DEL*(x-y), INS*(y-x));
        String aa = a.substring(1), bb = b.substring(1);
        if(a.charAt(0) == b.charAt(0))
            return cache[x][y] = editDistance(aa, bb, cache);
        int del = DEL+editDistance(aa,b,cache),
            ins=INS+editDistance(a,bb,cache),
            rep=REP+editDistance(aa,bb,cache);
        return cache[x][y] = min(del, min(ins, rep));
    }
    
    /**
     * O(n log n) algorithm to combine intervals
     * @param intervals Each element of intervals is an array of length 2 denoting [start, end]
     * @return List of intervals after combining overlapping intervals
     */
    static List<int[]> combineIntervals(int intervals[][]) {
        TreeMap<Integer, Integer> combined = new TreeMap<Integer, Integer>();

        for(int interval[] : intervals) {
            int start = interval[0], end = interval[1];
            if(start > end)
                throw new IllegalArgumentException(String.format("Start = %d is after end = %d", start, end));
            if(combined.containsKey(start))
                end = max(end, combined.get(start));
            combined.put(start, end);
        }

        List<int[]> answer = new ArrayList<int[]>();

        while(!combined.isEmpty()) {
            Map.Entry<Integer, Integer> first = combined.pollFirstEntry(), second = combined.firstEntry();
            if(second == null || second.getKey() > first.getValue())
                answer.add(new int[]{first.getKey(), first.getValue()});
            else
                combined.put(first.getKey(), max(first.getValue(), combined.pollFirstEntry().getValue()));
        }

        return answer;
    }

    //a class that tries to mimic the real world "fractions" i.e a/b
    class Fraction
    {
        private long a,b;
        private long gcd(long a,long b) {return b==0?a:gcd(b,a%b);}
        private void initialize(long n,long d) {long l=gcd(n,d);a=n/l;b=d/l;}
        public Fraction(long n,long d) {initialize(n,d);}
        public Fraction(long n) {initialize(n,1);}
        public Fraction(double d)
        {
            long denom = 1;
            for(final double EPS = 1E-12; abs(d-floor(d)) > EPS; d *= 10) denom *= 10;
            initialize((long)d, denom);
        }
        public Fraction(String f){initialize(Long.parseLong(f.substring(0,f.indexOf("/"))),Long.parseLong(f.substring(f.indexOf("/")+1)));}
        public Fraction add(Fraction f){return new Fraction(a*f.b+b*f.a,b*f.b);}
        public Fraction subtract(Fraction f){return new Fraction(a*f.b-b*f.a,b*f.b);}
        public Fraction multiply(Fraction f){return new Fraction(a*f.a,b*f.b);}
        public Fraction divide(Fraction f){return new Fraction(a*f.b,b*f.a);}
        public boolean equals(Fraction f){return a==f.a&&b==f.b;}
        public int compareTo(Fraction f){return(int)(a*f.b-b*f.a);}
        public long getNumerator(){return a;}
        public long getDenominator(){return b;}
        public String toString(){return(signum(a/b)<0?"-":"")+abs(a)+"/"+abs(b);}
        public double toDouble(){return 1.0*a/b;}
        public double toFloat(){return 1.0*a/b;}
    }

    //an n-dimensional vector
    class VectorD
    {
        private double x[];        //vector = (x[0], x[1], ... , x[n])
        private int n;

        public VectorD(double ...d)
        {
            int n = d.length;
            x = d;
        }

        //vector from point P1 = (a1, a2, a3, ...., an) to P2 = (b1, b2, b3, ... bk)
        public VectorD(double p1[], double p2[])
        {
            int n = max(p1.length, p2.length);
            x = new double[n];
            for(int i = 0; i < n; i++)
                if(i >= p1.length)
                    x[i] = p2[i];
                else if(i >= p2.length)
                    x[i] = -p1[i];
                else
                    x[i] = p2[i] - p1[i];
        }

        public VectorD add(VectorD v)
        {
            double newD[] = new double[max(v.n, n)];
            for(int i = 0; i < newD.length; i++)
                newD[i] = v.x[i] + x[i];
            return new VectorD(newD);
        }

        public double dotProduct(VectorD v)
        {
            double dot = 0.0;
            int newN = min(v.n, n);
            for(int i = 0; i < newN; i++)
                dot += v.x[0]*x[0];
            return dot;
        }

        public double norm()
        {
            double square = 0.0;
            for(int i = 0; i < n; i++)
                square += x[i]*x[i];
            return sqrt(square);
        }

        public double angleBetween(VectorD v) {return acos(dotProduct(v)/(v.norm()*norm()));}

        public VectorD crossProduct(VectorD v)
        {
            double a = x[0],
                   b = x[1],
                   c = n==2 ? 0 : x[2],
                   d = v.x[0],
                   e = v.x[1],
                   f = v.n==2 ? 0 : v.x[2];

            return new VectorD(f*b-e*c, d*c-f*a, a*e-d*b);
        }

        public String toString()
        {
            String s =  "(" + Arrays.toString(x).substring(1);
            return s.substring(0, s.length()-1) + ")";
        }

        public double get(int i) {return x[i];}

        public VectorD scalarProduct(double a)
        {
            double d[] = new double[n];
            for(int i=0; i<n; i++)
                d[i] = a*x[i];
            return new VectorD(d);
        }

        public int getDimension() {return n;}
    }


    
    public class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private static final int MAX_ENTRIES = 1000;
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    }
}
