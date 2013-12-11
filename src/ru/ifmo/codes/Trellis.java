package ru.ifmo.codes;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trellis {
	
	private class Node {
		Node(int level, int id) {
			this.id = id;
			this.level = level;
			Arrays.fill(next, -1);
		}
		
		Node(int level, int id, int dimension) {
			this(level, id);
			this.dimension = dimension;
		}
		
		private int id;
		private int level;
		private int[] next = new int[2];
		private boolean printed;
		private int dimension = code.k;
		private int[] vector;
		
		Node addNext(int i, int val) {
			next[i] = val;
			if (nodes[level + 1][val] == null) {
				nodes[level + 1][val] = new Node(level + 1, val);
			}
			return nodes[level + 1][val];
		}
		

		
		Node next(int i) {
			if (next[i] < 0)
				return null;
			return nodes[level + 1][next[i]];
		}
		
		@Override
		public String toString() {
			return "N" + level + "_" + id;
		}
		
		void print(PrintStream out) {
			if (printed)
				return;
			int[] vector = BinaryMath.toVector(id, Math.max(dimension, 1));
			String str = "";
			for (int i : vector) {
				str += i;
			}
			out.println(toString() + " [label="  + str + "];");
			printed = true;
		}
	}
	
	Node[][] nodes;
	private Code code;
	private static PrintStream out;
	
	public static void setPrintWriter(PrintStream out) {
		Trellis.out = out;
	}
	
	public Trellis(Code code, boolean isDual) {
		this.code = code;
		if (isDual) {
			initIfDual(code.g);
		} else {
			initIfNotDual(code.g);
		}
	}
	
	private int[][] active;
	
	private void initIfNotDual(int[][] g) {
		int k = code.k;
		int n = code.n;
		for (int i = k - 1, c = n - 1; i >= 0; i--, c--) {
			loop: while (g[i][c] == 0) {
				for (int j = i - 1; j >= 0; j--) {
					if (g[j][c] == 1) {
						int[] tmp = g[i];
						g[i] = g[j];
						g[j] = tmp;
						break loop;
					}
				}
				c--;
			}
			for (int j = i - 1; j >= 0; j--) {
				if (g[j][c] == g[i][c]) {
					g[j] = BinaryMath.sum(g[j], g[i]);
				}
			}
		}
		for (int i = 0, c = 0; i < k; i++, c++) {
			loop: while (g[i][c] == 0) {
				for (int j = i + 1; j < k; j++) {
					if (g[j][c] == 1) {
						int[] tmp = g[i];
						g[i] = g[j];
						g[j] = tmp;
						break loop;
					}
				}
				c++;
			}
			for (int j = i + 1; j < k; j++) {
				if (g[j][c] == g[i][c]) {
					g[j] = BinaryMath.sum(g[j], g[i]);
				}
			}
		}
		if (out != null) {
			out.println("Приведем порождающую матрицу к МСФ:");
			code.printCode(out);
		}
		
		active = new int[k][2];
		
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				if (g[i][j] == 1) {
					active[i][0] = j;
					break;
				}
			}
		}
		
		for (int i = 0; i < k; i++) {
			for (int j = n - 1; j >= 0; j--) {
				if (g[i][j] == 1) {
					active[i][1] = j;
					break;
				}
			}
		}
		if (out != null) {
			out.println("Активные элементы в строках:");
			for (int i = 0; i < k; i++) {
				out.println("В строке " + (i + 1) + ": c " + (active[i][0] + 1) + " по "
						+ (active[i][1] + 1) + ";");
			}
		}
		
		nodes = new Node[n + 1][];
		nodes[0] = new Node[1];
		nodes[0][0] = new Node(0, 0, 0);
		nodes[0][0].vector = new int[n];
		
		for (int i = 0; i < n; i++) {
			List<Integer> act = new ArrayList<Integer>();
			for (int j = 0; j < k; j++) {
				if (active[j][0] <= i && active[j][1] > i) {
					act.add(j);
				}
			}
			nodes[i + 1] = new Node[1 << act.size()];
			for (Node node : nodes[i]) {
				updateNext(node);
			}
			
		}
	}
	
	private void updateNext(Node node) {
		int val = node.id;
		int n = -1;
		int dim = node.dimension;
		List<Integer> act = new ArrayList<Integer>();
		for (int j = 0; j < code.k; j++) {
			if (active[j][0] <= node.level && active[j][1] >= node.level) {
				act.add(j);
			}
		}
		for (int i = 0; i < act.size(); i++) {
			if (active[act.get(i)][0] == node.level) {
				n = act.get(i);
			}
			if (active[act.get(i)][1] == node.level) {
				int f = (val >> (i + 1)) << i;
				int s = val % (1 << i);
				val = f + s;
				dim--;
			}
		}
		if (n != -1)
			dim++;
		int i = node.vector[node.level/* + 1*/];
		Node first = node.addNext(i, val);
		first.dimension = dim;
		first.vector = node.vector;
		if (n != -1) {
			Node second = node.addNext(1 - i, (1 << (dim - 1)) + val);
			second.dimension = dim;
			second.vector = BinaryMath.sum(node.vector, code.g[n]);
			
		}
	}

	private void initIfDual(int[][] h) {
		nodes = new Node[code.n + 1][1 << code.k];
		nodes[0][0] = new Node(0, 0);
		for (int i = 0; i < code.n; i++) {
			for (int j = 0; j < nodes[i].length; j++) {
				if (nodes[i][j] == null)
					continue;
				nodes[i][j].addNext(0, j);
				int syndr[] = BinaryMath.toVector(j, code.k);
				for (int k = 0; k < code.k; k++) {
					syndr[k] = (syndr[k] + h[k][i]) % 2;
				}

				nodes[i][j].addNext(1, BinaryMath.toInt(syndr));
			}
		}
		for (int i = 1; i < 1 << code.k; i++) {
			nodes[code.n][i] = null;
		}
		for (int i = code.n - 1; i >= 0; i--) {
			for (int j = 0; j < nodes[i].length; j++) {
				if (nodes[i][j] != null) {
					if (nodes[i][j].next(0) == null && 
							nodes[i][j].next(1) == null) {
						nodes[i][j] = null;
					}
				}
			}
		}
	}
	
	public void print(PrintStream out) {
		out.println("digraph Trellis {");
		nodes[0][0].print(out);
		for (int i = 0; i < nodes.length - 1; i++) {
			for (int j = 0; j < nodes[i].length; j++) {
				if (nodes[i][j] == null)
					continue;
				for (int k = 0; k <= 1; k++) {
					if (nodes[i][j].next(k) != null) {
						nodes[i][j].next(k).print(out);
						out.println(nodes[i][j] + " -> " + nodes[i][j].next(k)
								+ " [label = " + k + "];");
					}
				}
			}
		}
		out.println("}");
		
	}
}
