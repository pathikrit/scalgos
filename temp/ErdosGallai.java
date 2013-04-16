import java.util.*;
import static java.lang.Math.*;

public class ErdosGallai {


	public static void main(String args[]) {

		//for(int t = 0; t < 100; t++)
		int d[], c= 0, x = 0;
		boolean a,b;
		int t = 51;
		do
		{
			d = new int[t];

			for(int i = 0; i < t; i++)
				d[i] = (int)(t*random());

			a = naiveSort(d);
			b = erdosGallai(d);
			if(a)
				x++;

		}while((a == b) && ++c<100);

		System.out.println(c + " " + x + " " + (c-x));

	}

	public static boolean naiveSort(int d[]) {
		ArrayList<Integer> a = new ArrayList();
		for(int i : d)  a.add(i);

		while(a.size() > 0)
		{
			Collections.sort(a, Collections.reverseOrder());
			int b = a.remove(0);
			if(b > a.size() || b<0)
				return false;
			for(int i = 0; i < b; i++)
				a.set(i, a.get(i) - 1);
		}

		return true;
	}

	/*
	 * returns true iff an undirected graph can be constructed with n vertices such that degree of ith vertex = d[i]
	 * may sort d in non-increasing order
	 * O(n^2)
	 */
	public static boolean erdosGallai(int d[])
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


	public static boolean erdosGallai2(int d[])
	{
		int n = d.length, s[] = new int[n+1];
		Arrays.sort(d);
		//sort d in descending order
		for(int i = 0; i < n/2; i++)
		{
			int t = d[i];
			d[i] = d[n-i-1];
			d[n-i-1] = t;
		}

		for(int i = 0; i < n; i++)
		{
			int a = d[i];
			if(a < 0 || a >= n)
				return false;
			s[i+1] = s[i] + d[i];
		}

		for(int r = 0; ++r < n; )
		{
			int i = r;
			while(i<n && r<d[i]) i++;
			if(s[r] + s[i] > r*(i-1) + s[n])
				return false;
		}

		return s[n]%2 == 0;
	}

		/*
	 * returns true iff an undirected graph can be constructed with n vertices such that degree of ith vertex = d[i]
	 * may sort d in non-increasing order
	 * O(n)
	 */
	public static boolean erdosGallai(int d[])
	{
		int n = d.length, c[] = new int[n], s[] = new int[n+1], g[] = new int[n];
		for(int i : d)
			if(0 <= i && i < n)
				c[i]++;
			else
				return false;

		for(int i = n, j = 0; --i >= 0; )
			while(c[i]-- > 0)
			{
				s[j+1] = s[j] + i;
				d[j] = i;

				j++;
			}

		for(int i  = 0; i < n; i++)
			g[i] = n-i;

		for(int i  = 0; i < n; i++)
			g[d[i]] = max(i, g[d[i]]);

		System.out.println(Arrays.toString(g));

		for(int r = 0; ++r < n; )
		{
			int i = r;
			for( ; i<n && r<d[i]; i++);
			int i2 = g[r];
			System.out.println(r + " " + i + " " + i2 + ": " + s[r] + " <= " + (r*(i-1) + s[n] - s[i]));
			if(s[r] + s[i] > r*(i-1) + s[n])
				return false;
		}
		return s[n]%2 == 0;
	}

	//typecasts src[] to dest[]
	/*public static void arrayCast(Object src[], Object dest[])
	{
		dest = Arrays.asList(src).toArray(new Object[0]);
	}
*/

	public static boolean erdosGallai2(int d[])
	{
		int n = d.length, c[] = new int[n], s[] = new int[n+1], g[] = new int[n];
		for(int i : d)
			if(0 <= i && i < n)
				c[i]++;
			else
				return false;

		for(int i = n, j = 0; --i >= 0; )
			while(c[i]-- > 0)
			{
				s[j+1] = s[j] + i;
				g[d[j] = i] = j++;
			}

		System.out.println(Arrays.toString(g));
		for(int r = 0; ++r < n; )
		{
			int k = max(r, g[r]);
			System.out.println(r + " " + k + " " + ": " + s[r] + " <= " + (r*(k-1) + s[n] - s[k]));
			if(s[r] + s[k] > r*(k-1) + s[n])
				return false;
		}
		return s[n]%2 == 0;
	}
}