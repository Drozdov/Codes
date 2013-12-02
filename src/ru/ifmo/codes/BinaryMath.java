package ru.ifmo.codes;

public class BinaryMath {
	/* This class is not intended to be instanced */
	private BinaryMath() {}
	
	public static int multiply(int[] a, int[] b) {
		int k = 0;
		for (int i = 0; i < a.length; i++) {
			k += a[i] * b[i];
		}
		return k % 2;
	}
	
	public static int[] sum(int[] a, int[] b) {
		int res[] = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] += (a[i] + b[i]) % 2;
		}
		return res;
	}
	
	public static int[] mutate(int[] a, int[][] b) {
		int[] res = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			int r = 0;
			for (int j = 0; j < a.length; j++) {
				r += a[j] * b[i][j];
			}
			res[i] = r % 2;
		}
		return res;
	}
	
	public static boolean compare(int[] a, int[] b) {
		int i1 = 0, i2 = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > 0) {
				i1++;
			}
			if (b[i] > 0) {
				i2++;
			}
		}
		return i1 > i2;
	}
	
	public static int toInt(int[] a) {
		int ss = 0;
		for (int i = 0; i < a.length; i++) {
			ss = ss * 2 + a[i];
		}
		return ss;
	}
	
	public static int[] toVector(int a, int n) {
		int[] res = new int[n];
		for (int i = 0; i < n; i++) {
			res[n - i - 1] = (a & 1 << i) > 0 ? 1 : 0;
		}
		return res;
	}
}
