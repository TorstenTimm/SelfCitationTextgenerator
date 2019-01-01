package de.voynich.text.util;

/**
 * helper class to calculate the levenshtein distance
 */
public class Levenshtein {

	private static final char[][] VMS_SIMILAR_ARRAY ={ {'o', 'a', 'y'}, {'d','l','s'}, {'n', 'r', 'l', 'm'}, {'r', 's'}, {'k','t','p','f'}, {'o', 'e'}, {'1', '2', 'e', 'c'}, {'m', 'g'}};

	private static boolean isSimilar(char c1, char c2, final boolean voynich) {
		if (!voynich) {
			return true;
		}
		for (int j = 0; j < VMS_SIMILAR_ARRAY.length; j++) {
			for (char c0 : VMS_SIMILAR_ARRAY[j]) {
				if (c0 == c1) {
					for (char c20 : VMS_SIMILAR_ARRAY[j]) {
						if (c20 == c2) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	protected static int getSimilarity(final CharSequence s1, final CharSequence s2, final boolean damerau, final boolean voynich) {
		if (s1 == null || s2 == null)
			return 0;

		int s1_len = s1.length(); // length of s
		int s2_len = s2.length(); // length of t
		int max_len = s1_len > s2_len ? s1_len : s2_len;
		int distance = getDistance(s1, s2, damerau, voynich);

		//return max_len == 0 ? 0 : new Double((1.0 - new Double(distance) / new Double(max_len)) * 100.0).intValue();
		return max_len == 0 ? 0 : (int) ((1.0 - (double) distance / (double) max_len) * 100.0);
	}

	public static int getDistance(final CharSequence s, final CharSequence t, final boolean damerau, final boolean voynich) {
		int n = s == null ? 0 : s.length(); // length of s
		int m = t == null ? 0 : t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int[] p = new int[n+1]; //'previous' cost array, horizontally
		int[] d = new int[n+1]; // cost array, horizontally
		int[] _d; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int j; // iterates through s
		int i; // iterates through t
		char t_i; // ith character of t
		int cost; // cost

		for (j = 0; j<=n; j++)
			p[j] = j;

		for (i = 1; i<=m; i++) {
			t_i = t.charAt(i-1);
			d[0] = i;

			for (j=1; j<=n; j++) {
				if (damerau) {
					// DAMERAU
					cost = ((s.charAt(j-1) == t_i) ? 0 : j > 1 && i > 1 && s.charAt(j-1) == t.charAt(i-2) && s.charAt(j-2) == t_i ? 0 : isSimilar(s.charAt(j-1), t_i, voynich ) ? 1 : j > 1 && i > 1 && isSimilar(s.charAt(j-1), t.charAt(i-2), voynich) && isSimilar(s.charAt(j-2), t_i, voynich) ? 1 : 2);
				} else {
					// Levenshtein
					cost = s.charAt(j-1)==t_i ? 0 : isSimilar(s.charAt(j-1), t_i, voynich) ? 1 : 2;

				}

				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				d[j] = Math.min(Math.min(d[j-1]+1, p[j]+1),  p[j-1]+cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;	p = d; d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
	}

	protected static int getDistance(final String s, final String t, final int limit)
	{
		int l1 = s.length();
		int l2 = t.length();
		int m = l1 + 1;
		int n = l2 + 1;
		if (m == 1)
			return n - 1;
		if (n == 1)
			return m -1;
		int[] d = new int[m * n];
		int k = 0;
		for (int i = 0; i < n; i++)
			d[i] = i;
		k = n;
		for (int i = 1; i < m; i++)
		{
			d[k] = i;
			k += n;
		}
		int f = 0, g = 0, h = 0, min = 0, b = 0, best = 0, c = 0, cost = 0, tr = 0;
		for (int i = 1; i < n; i++)
		{
			k = i;
			f = 0;
			best = limit;
			for (int j = 1; j < m; j++)
			{
				h = k;
				k += n;
				min = d[h] + 1;
				b = d[k - 1] + 1;
				if (g < l1 && f < l2)
					if (s.charAt(g) == t.charAt(f))
						cost = 0;
					else
					{
						cost = 1;
						/* Sean's transposition */
						if (j < l2 && i < l1)
							if (s.charAt(i) == t.charAt(f) && s.charAt(g) == t.charAt(j))
							{
								tr = d[(h) - 1];
								if (tr < min)
									min = tr;
							}
					}
				else
					cost = 1;
				c = d[h - 1] + cost;
				if (b < min)
					min = b;
				if (c < min)
					min = c;
				d[k] = min;
//				System.out.println("i=" + i + ", j=" + j);
//				for (int v = 0; v < m; v++)
//				{
//					for (int w = 0; w < n; w++)
//						System.out.print(d[v * n + w] + Constants.SPACE);
//					System.out.println();
//				}

				if (min < best)
					best = min;
				f = j;
			}
			if (best >= limit)
				return limit;
			g = i;
		}
		return d[k];
	}
}
