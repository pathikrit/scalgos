public class B_gen {
    
    public static void main(String args[]) {
        
        for(int l = 1; l < 24*60*60; l++) {
            int d = 1 + (int)((1024*1000*1000-2)*Math.random());
            int s = 1 + (int)((1024*1000*1000-2)*Math.random());
            System.out.println(d + " " + s + " " + l);
        }   
    }
}