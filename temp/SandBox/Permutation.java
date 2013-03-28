import java.util.*;

public class Permutation {

    public static void main(String args[]) {
        //int a[] = {3,2,1};
        //permute(a, a.length);
        long x = Long.parseLong("11", 2);
        int c = 0;
        do {
            System.out.println(++c + ". " + Long.toString(x, 2));
        } while( (x =  nextChooseK(7, x)) > 0);
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

    private static void swap(int a[], int p1, int p2) {
        a[p1] ^= a[p2]^(a[p2] = a[p1]);
    }

    // for(long c = (1<<k)-1; c != 0; c = nextChooseK(n, c)) { print(Long.toBinaryString(c))}
    static long nextChooseK(int n, long c) {
        long u = -c&c, v = c + u;
        return (c = v + (v^c)/u/4)>>n == 0 ? c : 0;
    }
}