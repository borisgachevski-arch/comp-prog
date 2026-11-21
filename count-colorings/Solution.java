import java.util.*;

public class Solution {
	
	static long mod = 1_000_000_007;
	
	public static ArrayList<ArrayList<Integer>> generatePatterns(long w) {
		if (w == 1) {
			ArrayList<ArrayList<Integer>> ret = new ArrayList<>();
			ret.add(new ArrayList<>());
			ret.get(0).add(0);
			return ret;
		}
		ArrayList<ArrayList<Integer>> smallerPatterns = generatePatterns(w-1);
		ArrayList<ArrayList<Integer>> answer = new ArrayList<>();
		for (ArrayList<Integer> currPat : smallerPatterns) {
			int q = Collections.max(currPat) + 1;
			for (int i=0; i <= q; i++) {
				ArrayList<Integer> newPat = new ArrayList<>(currPat);
				newPat.add(i);
				answer.add(newPat);
			}
		}
		return answer;
	}
	
	public static long ways(ArrayList<Integer> p2, ArrayList<Integer> p1, long c) {
		int q1 = Collections.max(p1) + 1;
		int q2 = Collections.max(p2) + 1;
		if (q1 > c || q2 > c) {
			return 0;
		}
		boolean[][] forbidden = new boolean[q2][q1];
		for (int i=0; i < p1.size(); i++) {
			int topCol = p1.get(i);
			int bottomLabel = p2.get(i);
			forbidden[bottomLabel][topCol] = true;
		}
		boolean[] used = new boolean[q1];
		long answer = countWays(0, q2, c, forbidden, used, 0)%mod;
		for (int i=0; i < p2.size()-1; i++) {
			if (p2.get(i).equals(p2.get(i+1))) {
				answer *= 2;
				answer %= mod;
			}
		}
		return answer%mod;
	}

	public static long countWays(int curr, int q2, long c,
			boolean[][] forbidden, boolean[] used, int freshUsed) {
		if (curr == q2) {
			return 1;
		}
		long answer = 0;
		for (int col=0; col < used.length; col++) {
			if (!forbidden[curr][col] && !used[col]) {
				used[col] = true;
				answer += countWays(curr+1, q2, c, forbidden, used, freshUsed);
				answer %= mod;
				used[col] = false;
			}
		}
		long available = c - used.length - freshUsed;
		if (available > 0) {
			answer += ((available * countWays(curr+1, q2, c,
					forbidden, used, freshUsed+1)) % mod);
			answer %= mod;
		}
		return answer;
	}
	
	public static long[][] generateMatrix(long w, 
			ArrayList<ArrayList<Integer>> patterns, long c) {
		int k = patterns.size();
		long[][] M = new long[k][k];
		for (int i=0; i < k; i++) {
			for (int j=0; j < k; j++) {
				M[i][j] = ways(patterns.get(i), patterns.get(j), c);
			}
		}
		return M;
	}
	
	public static long[] baseCase(ArrayList<ArrayList<Integer>> patterns, long c) {
		int s = patterns.size();
		long[] v0 = new long[s];
		for (int i=0; i < s; i++) {
			ArrayList<Integer> currPat = patterns.get(i);
			long q = Collections.max(currPat) + 1;
			long eqN = 0;
			for (int j=0; j < currPat.size() - 1; j++) {
				if (currPat.get(j) == currPat.get(j+1)) {
					eqN++;
				}
			}
			long rest=1;
			for (int j=0; j < q; j++) {
				rest *= (c - j);
				rest %= mod;
			}
			v0[i] = ((long) Math.pow(2, eqN))*rest;
			v0[i] %= mod;
		}
		return v0;
	}
	
	public static long[][] identityMatrix(int s) {
		long[][] M = new long[s][s];
		for (int i=0; i < s; i++) {
			M[i][i] = 1;
		}
		return M;
	}
	
	public static long[][] matrixPow(long[][] M, long n) {
		int s = M.length;
		if (n == 0) {
			return identityMatrix(s);
		}
		if (n == 1) {
			return M;
		}
		long mid = n/2;
		long rest = n - mid - mid;
		long[][] A = matrixPow(M, mid);
		long[][] A1 = matrixMatrix(A, A);
		long[][] A2 = matrixMatrix(A1, matrixPow(M, rest));
		return A2;
	}
	
	public static long[][] matrixMatrix(long[][] M, long[][] N) {
		int m = M.length;
		int n = M[0].length;
		int k = N[0].length;
		long[][] ans = new long[m][k];
		for (int i=0; i < m; i++) {
			for (int j=0; j < k; j++) {
				for (int z=0; z < n; z++) {
					ans[i][j] += M[i][z]*N[z][j];
					ans[i][j] %= mod;
				}
			}
		}
		return ans;
	}
	
	public static long[] matrixVector(long[][] M, long[] v) {
		int m = M.length;
		int n = v.length;
		long[] ans = new long[m];
		for (int i=0; i < m; i++) {
			for (int j=0; j < n; j++) {
				ans[i] += M[i][j] * v[j];
				ans[i] %= mod;
			}
		}
		return ans;
	}
	
	public static long solution(long n, long w, long c) {
		ArrayList<ArrayList<Integer>> patterns = generatePatterns(w);
		long[][] M = generateMatrix(w, patterns, c);
		long s = M.length;
		long[] v0 = baseCase(patterns, c);
		long[] result = matrixVector(matrixPow(M, n-1), v0);
		long answer=0;
		for (int i=0; i < s; i++) {
			answer += result[i];
			answer %= mod;
		}
		return answer;
	}
	
	public static void main (String[] args) {
		Scanner in = new Scanner(System.in);
		int t = in.nextInt();
	    for (int test = 0; test < t; test++) {
	        long n = in.nextLong();
	        long w = in.nextLong();
	        long c = in.nextLong();

	        System.out.println(solution(n, w, c));
	    }
	}
}
