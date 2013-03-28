import java.util.*;

public class C {

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        for(int C = sc.nextInt(); C > 0; C--) {
            int b = sc.nextInt(), g = sc.nextInt(), n = sc.nextInt();
            sc.nextLine();
            boolean like[][] = new boolean[b][g];
            for(int i = 0; i < b; i++) {
                String line = sc.nextLine();
                for(int j = 0; j < g; j++)
                    like[i][j] = line.charAt(j) == 'Y';
            }
            cache = new HashMap();
            System.out.println(count(0, 0, n, new boolean[g], like));
        }
    }    
    
    static HashMap<String, Integer> cache;
    
    static int count(int curr_boy, int curr_count, final int target_count, boolean isGirlTaken[], final boolean like[][]) {        
        if(curr_count == target_count)
            return 1;        
        String key = curr_boy + Arrays.toString(isGirlTaken);        
        int count = 0;
        if(!cache.containsKey(key)) {
            for(int i = curr_boy; i < like.length; i++) {
                for(int j = 0; j < like[i].length; j++) {
                    if(like[i][j] && !isGirlTaken[j]) {
                        isGirlTaken[j] = true;
                        count += count(i + 1, curr_count + 1, target_count, isGirlTaken, like);
                        isGirlTaken[j] = false; 
                    }
                }
            }
            cache.put(key, count);
        }
        return cache.get(key);
    }    
}