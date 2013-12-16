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
		private int dimension = code.k;
		private int[] vector;
		private BKDRInfo info;
		
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
		
		void print(PrintStream out, int nodeLabel) {
			int[] vector = BinaryMath.toVector(id, Math.max(dimension, 1));
			String str = "";
			switch (nodeLabel) {
			case PRINT_ID:
				for (int i : vector) {
					str += i;
				}
				break;
			case PRINT_ALPHA:
				str = toStr(info.alpha);
				break;
			case PRINT_BETTA:
				str = toStr(info.betta);
				break;
			default:
				throw new IllegalArgumentException();

			}
			out.println(toString() + " [label="  + str + "];");
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
		
		int[] rights = new int[k];
		for (int i = 0; i < k; i++) {
			for (int j = n - 1; j >= 0; j--) {
				if (g[i][j] == 1) {
					rights[i] = j;
					break;
				}
			}
		}
		
		for (int i = n - 1; i >= 0; i--) {
			List<Integer> conflicts = new ArrayList<Integer>();
			for (int j = 0; j < k; j++) {
				if (rights[j] == i) {
					conflicts.add(j);
				}
			}
			if (conflicts.size() > 1) {
				int last = conflicts.get(conflicts.size() - 1);
				for (int c : conflicts) {
					if (c != last) {
						g[c] = BinaryMath.sum(g[c], g[last]);
						for (int j = i; j >= 0; j--) {
							if (g[c][j] == 1) {
								rights[c] = j;
								break;
							}
						}
					}
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
						+ (active[i][1]) + ";");
			}
		}
		
		nodes = new Node[n + 1][];
		nodes[0] = new Node[1];
		nodes[0][0] = new Node(0, 0, 0);
		nodes[0][0].vector = new int[n];
		
		for (int i = 0; i < n; i++) {
			int size = 0;
			int a = -1;
			List<Integer> toRemove = new ArrayList<Integer>();
			int cur = 0;
			int dim = nodes[i][0].dimension;
			for (int j = 0; j < k; j++) {
				if (active[j][0] <= i && i < active[j][1]) {
					size++;
				}
				if (active[j][0] <= i && i <= active[j][1]) {
					cur++;
				}
				if (active[j][1] == i) {
					toRemove.add(cur - 1);
					dim--;
				}
				if (active[j][0] == i) {
					a = j;
					dim++;
				}
			}
			nodes[i + 1] = new Node[1 << size];
			for (Node node : nodes[i]) {
				int val = node.id;
				int val1 = -1;
				if (a != -1) {
					val1 = (1 << node.dimension) + val;
				}
				val = removePositions(val, toRemove);
				val1 = removePositions(val1, toRemove);
				int j = node.vector[i];
				Node first = node.addNext(j, val);
				first.dimension = dim;
				first.vector = node.vector;
				if (a != -1) {
					Node second = node.addNext(1 - j, val1);
					second.dimension = dim;
					second.vector = BinaryMath.sum(node.vector, code.g[a]);
					
				}
				
			}
			
		}
	}

	private int removePositions(int val, List<Integer> toRemove) {
		for (int r : toRemove) {
			int f = (val >> (r + 1)) << r;
			int s = val % (1 << r);
			val = f + s;
		}
		return val;
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
	
	public final static int PRINT_ID = 0;
	public final static int PRINT_ALPHA = 1;
	public final static int PRINT_BETTA = 2;
	public final static int PRINT_VAL = 0;
	public final static int PRINT_GAMMA = 1;
	public final static int PRINT_SIGMA = 2;
	
	public void print(PrintStream out, String name, int nodeLabel, int edgeLabel) {
		out.println("graph " + name + " {");
		out.println("rankdir=LR;");
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes[i].length; j++) {
				Node node = nodes[i][j];
				if (node == null)
					continue;
				node.print(out, nodeLabel);
				for (int k = 0; k <= 1; k++) {
					String label = null;
					switch (edgeLabel) {
					case PRINT_VAL:
						label = "" + k;
						break;
					case PRINT_GAMMA:
					case PRINT_SIGMA:
						if (node.info == null || node.info.sigma == null)
							break;
						label = toStr((edgeLabel == PRINT_GAMMA) ? node.info.gamma[k]
								: node.info.sigma[k]);
						break;
					default:
						throw new IllegalArgumentException();
					}
							
					if (node.next(k) != null) {
						out.println(node + " -- " + node.next(k)
								+ " [label = " + label + "];");
					}
				}
			}
		}
		out.println("}");
		
	}
	
	public void print(PrintStream out, String name) {
		print(out, name, PRINT_ID, PRINT_VAL);
	}
	
	public void print(PrintStream out) {
		print(out, "Trellis");
	}
	
	

	private class BKDRInfo {
		double alpha, betta;
		double[] gamma, sigma;
	}
	
	/**
	 * 
	 * @param vector input sequence
	 * @param p0 error probability
	 * @param p probability of zero (0.5 by default)
	 */
	public void initBKDR(int[] vector, double p0, double p) {
		int n = code.n;
		for (int i = 0; i < n; i++) {
			
			for (Node node  : nodes[i]) {
				BKDRInfo info = new BKDRInfo();
				info.gamma = new double[2];
				info.gamma[0] = (vector[i] == 0) ? (1 - p0)  : p0;
				info.gamma[1] = 1 - info.gamma[0];
				if (node.next[0] != -1 && node.next[1] != -1) {
					info.gamma[0] *= p;
					info.gamma[1] *= (1 - p);
				} else if (node.next[0] == -1) {
					info.gamma[0] = 0;
				} else {
					info.gamma[1] = 0;
				}
				node.info = info;
			}
		}
		nodes[0][0].info.alpha = 1;
		nodes[n][0].info = new BKDRInfo();
		nodes[n][0].info.betta = 1;
		for (int i = 0; i < n; i++) {
			for (Node node : nodes[i]) {
				for (int j = 0; j <= 1; j++) {
					if (node.next[j] != -1) {
						nodes[i + 1][node.next[j]].info.alpha += node.info.alpha
								* node.info.gamma[j];
					}
				}
			}
		}
		for (int i = n - 1; i >= 0; i--) {
			for (Node node : nodes[i]) {
				for (int j = 0; j <= 1; j++) {
					if (node.next[j] != -1) {
						node.info.betta += nodes[i + 1][node.next[j]].info.betta
								* node.info.gamma[j];
					}
				}
			}
		}
		for (int i = 0; i < n; i++) {
			for (Node node : nodes[i]) {
				node.info.sigma = new double[2];
				for (int j = 0; j <= 1; j++) {
					if (node.next[j] != -1) {
						node.info.sigma[j] = node.info.alpha
								* node.info.gamma[j]
								* nodes[i + 1][node.next[j]].info.betta;
					}
					
					
				}
			}
		}
	}
	
	private String toStr(double d) {
		return String.format("%.5f", d).replace(',', '.');
	}
	
	public double getLikelihood(int level) {
		double k0 = 0, k1 = 0;
		for (Node node : nodes[level]) {
			k0 += node.info.sigma[0];
			k1 += node.info.sigma[1];
		}
		return Math.log(1. * k1 / k0);
	}
	
	public double getProbability() {
		return nodes[0][0].info.betta;
	}
	
	public class Pair {
		public Pair(int[] vector, double probability) {
			this.vector = vector;
			this.probability = probability;
		}
		public int[] vector;
		public double probability;
		
		@Override
		public String toString() {
			return String.format("%s: %.5f  %.3f",
					BinaryMath.toString(vector, false), probability,
					probability / getProbability());
		}
	}
	
	public Pair getVector(int i) {
		int[] mask = BinaryMath.toVector(i, code.k);
		int n = code.n;
		int[] vector = new int[n];
		double p = 1;
		Node cur = nodes[0][0];
		int k = 0;
		for (int j = 0; j < n; j++) {
			int q;
			if (cur.next[0] >= 0 && cur.next[1] >= 0) {
				q = mask[k++];
			} else if (cur.next[0] >= 0) {
				q = 0;
			} else {
				q = 1;
			}
			vector[j] = q;
			p *= cur.info.gamma[q];
			cur = cur.next(q);
			
		}
		return new Pair(vector, p);
	}
}
