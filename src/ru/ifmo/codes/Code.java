package ru.ifmo.codes;

import java.io.PrintStream;

public class Code {
	
	public final static int QUIET = 0;
	public final static int SHOW_STEPS = 1;
	
	private static int mode = QUIET;
	public static void setMode(int mode) {
		if (mode != QUIET && mode != SHOW_STEPS)
			throw new IllegalArgumentException();
		Code.mode = mode;
	}
	
	private static PrintStream out = System.out;
	public static void setPrintStream(PrintStream out) {
		Code.out = out;
	}
	
	int[][] g;
	int n, k;
	
	public Code(int[][] g) {
		this.g = g;
		this.k = g.length;
		this.n = g[0].length;
	}
	
	public int[][] getG() {
		return g;
	}
	
	public Code getDualCode() {
		if (mode == SHOW_STEPS) {
			out.println("Строим код, дуальный к данному.");
			out.println("Для этого методом Гаусса выделим единичную матрицу.");
		}
		for (int i = 0; i < k; i++) {
			while (g[i][i] == 0) {
				int[] tmp = g[i];
				for (int j = i + 1; j < k; j++) {
					g[j - 1] = g[j];
				}
				g[k - 1] = tmp;
			}
			for (int j = i + 1; j < k; j++) {
				if (g[j][i] == g[i][i]) {
					g[j] = BinaryMath.sum(g[j], g[i]);
				}
			}
		}
		for (int i = k - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (g[i][i] == g[j][i]) {
					g[j] = BinaryMath.sum(g[i], g[j]);
				}
			}
		}
		if (mode == SHOW_STEPS) {
			printCode(out);
			out.println("Теперь порождающая матрица приведена к систематическому виду.");
		}		
		int r = n - k;
		int h[][] = new int[r][n];
		for (int i = 0; i < r; i++) {
			for (int j = k; j < n; j++) {
				h[i][j] = (i == (j - k)) ? 1 : 0;
			}
		}
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < k; j++) {
				h[i][j] = g[j][i + k];
			}

		}
		Code result = new Code(h);
		if (mode == SHOW_STEPS) {
			out.println("Порождающая матрица для дуального кода "
					+ "(являющаяся проверочной к исходному) строится\n"
					+ "транспонированием исходной матрицы, приведенной к "
					+ "систематическому виду, и добавлением к ней справа\n"
					+ "единичной матрицы:");
			result.printCode(out);
	}
		return result;
	}
	
	public int distance() {
		int min = Integer.MAX_VALUE;
		int n = g[0].length;
		for (int i = 1; i < 1 << g.length; i++) {
			int cur[] = new int[n];
			for (int j = 0; j < g.length; j++) {
				if ((1 << j & i) > 0) {
					cur = BinaryMath.sum(cur, g[j]);
				}
			}
			int k = 0;
			for (int j = 0; j < n; j++) {
				if (cur[j] > 0) {
					k++;
				}
			}
			min = Math.min(min, k);
		}
		return min;
	}
	
	public void printCode(PrintStream out) {
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				out.print(g[i][j] + " ");
			}
			out.println();
		}
	}
}
