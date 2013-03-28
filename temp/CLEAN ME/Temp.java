// If I have P dollars to start with and I earn r interest (r is between 0 and 1) and I spend S dollars every year, how long would my money last?
public class Temp {
	public static void main(String args[]) {
		double P = 1000000, r = 0.03, S = 70000;
		if(P*r >= S) {
			System.out.println("You are ");
		}

		for(int t = 1; P > 0; t++)
			System.out.printf("End of year %d I will have $%f\n", t, P = P*(1+r) - S);
	}
}
