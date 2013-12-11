package ru.ifmo.codes.tasks;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import ru.ifmo.codes.Code;
import ru.ifmo.codes.Trellis;

public class Task4_4 {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream("input.txt"));
		PrintStream out = new PrintStream(new FileOutputStream("tree.dot"));
		int k = in.nextInt();
		int n = in.nextInt();
		int g[][] = new int[k][n];
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				g[i][j] = in.nextInt();
			}
		}
		in.close();
		Code code = new Code(g);
		Trellis.setPrintWriter(System.out);
		new Trellis(code, false).print(out, "Direct");
		new Trellis(code, true).print(out, "Dual");
	}
}
