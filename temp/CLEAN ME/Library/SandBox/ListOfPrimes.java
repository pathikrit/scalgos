import java.util.*;
import java.io.*;

public class ListOfPrimes  {

    final static int INF = (int)1E9;
    public static void main(String args[]) throws IOException {
        FileWriter f = new FileWriter("list_of_primes.txt");
        for(int i = 2; i <= INF; i++)
            if(isPrime(i))
                f.write(", " + i);
        f.close();
    }
    
    static boolean isPrime(int n) {
        for(int i = 2; i*i <= n; i++)
            if(n%i == 0)
                return false;
        return true;    
    }
}