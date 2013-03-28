import java.util.*;
import static java.util.Arrays.*;
import static java.lang.Math.*;

public class SubsetSum {

	public static void main(String args[]) {
		SubsetSum ss = new SubsetSum();
		int s[] = {-1, -2, -3, -4, 0, 2, 7};
		ArrayList<Integer> as = new ArrayList();
		for(int i : s)
			as.add(i);
		System.out.println(ss.subsetSum(s, -7));
	}

	/*
	 * returns a subset of set such that sum of its elements is target
	 * if no such subset exits, returns null
	 * O(2^n) where n = |set|
	 */
	ArrayList<Integer> bruteforce_subsetSum(ArrayList<Integer> set, int target) {
		if(set.size() == 0)
			return target == 0 ? set : null;
		ArrayList<Integer> temp = new ArrayList(set), ans = null;
		int x = temp.remove(0);
		if((ans = bruteforce_subsetSum(temp, target)) != null)
			return ans;
		if((ans = bruteforce_subsetSum(temp, target - x)) != null)
			ans.add(x);
		return ans;
	}
	
	/*
	 * returns a true iff there exists a subset of set s.t. sum of elements is target
	 * O(Cn + n log n) where n = |set|, c = target	 	 
	 * uses max O( max(c, n) ) space
	 */
	boolean subsetSum(int set[], int target) {
		final int n = set.length;
		sort(set);
		HashSet<Integer> doable = new HashSet();
		doable.add(0);					
		
		int[] maxFromRest = new int[n+1], minFromRest = new int[n+1];
		for(int i = n-1; i >= 0; i--) {		
			maxFromRest[i] = maxFromRest[i+1] + max(0, set[i]);		
			minFromRest[i] = minFromRest[i+1] + min(0, set[i]);
		}			
		
		System.out.println(Arrays.toString(set));
		System.out.println(Arrays.toString(maxFromRest));
		System.out.println(Arrays.toString(minFromRest));
			
		for(int i = 0; i < n; i++) {
			HashSet<Integer> next = new HashSet();					
			
			//figure out max that can be added from rest of array and dont put j s.t j + max <= target
			for(int j : doable) {				
				next.add(j + set[i]);
				if(j + minFromRest[i] <= target && j + maxFromRest[i] >= target)
					next.add(j);
				
			}
			doable = next;
			System.out.println(set[i] + " " + doable);
		}
		return doable.contains(target);		
	}
}