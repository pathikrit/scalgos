import java.util.*;

public class DNA {

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);

		for(int N = sc.nextInt(); N > 0; N--) {
			String dna = "";

			for(StringTokenizer st = new StringTokenizer(sc.next(), "AGCTU", true); st.hasMoreTokens(); ) {

				String b = st.nextToken();
				int k = 1;

				try {
					k = Integer.parseInt(b);
					b = st.nextToken();
				} catch(Throwable t) {}

				while(k-- > 0)
					dna += b;
			}

			System.out.println(sc.next().equals(dna) ? "Yes" : "No");
		}
	}
}