public class SwapPermute {

  public static void main(String[] args) {
    swapPermute(5);
  }

  /**
   * Generates all permutations of [0..n-1]
   * each permutation differ from previous by one adjacent-swap only - x,y where y = x+-1
   * Steinhaus–Johnson–Trotter algorithm - with Even's speedup
   * swaps involving highest element are O(1) - otherwise O(N)
   * Since the latter occurs <1/N times - each is O(1) ammortized
   */
  static void swapPermute(int N) {
    int[] a = new int[N], d = new int[N];

    for(int i = 1; i < N; i++) {
      a[i] = i;
      d[i] = -1;
    }

    for (int x = N-1, c = 1, y; d[x] != 0;c++) {
      y = x + d[x];

      {
        // code goes here
        System.out.print(c + ": [");
        for(int i = 0; i < N; i++) {
          System.out.print(d[i] > 0 ? ">" : d[i] < 0 ? "<" : "");
          System.out.print(a[i] + " ");
        }
        System.out.println("] " + a[x]);
      }

      swap(a, x, y);
      swap(d, x, y);

      x = y;

      if (x == 0 || x == N-1 || a[x + d[x]] > a[x]) {
        d[x] = 0;
      } else if(a[x] == N-1) {
        continue;
      }

      for(int i = 0; i < N; i++) {
        if (a[i] > a[y]) {
          d[i] = i<y ? 1 : -1;
        }
        if(d[i] != 0 && (a[i] > a[x] || d[x] == 0)) {
          x = i;
        }
      }
    }
  }

  static void swap(int[] a, int x, int y) {
    int t = a[x];
    a[x] = a[y];
    a[y] = t;
  }
}
