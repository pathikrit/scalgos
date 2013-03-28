
public class ChineseRemainderTheorem {

    public int[] chineseRemainderTheorem(int a[], int r[]) {
        final int L = a.length;

        for(int i = 0; i < L; i++)
            for(int j = i+1; j < L; j++)
                if(a[i] != a[j])


    }

    int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a%b);
    }

    public static void main(String args[]) {

    }


}