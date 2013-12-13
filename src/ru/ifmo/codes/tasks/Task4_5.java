package ru.ifmo.codes.tasks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import ru.ifmo.codes.Code;
import ru.ifmo.codes.Trellis;

public class Task4_5 {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream("input.txt"));
		PrintStream out = System.out;
		int k = in.nextInt();
		int n = in.nextInt();
		int g[][] = new int[k][n];
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				g[i][j] = in.nextInt();
			}
		}
		Code code = new Code(g);
		Trellis trellis = new Trellis(code, false);
		//trellis.initBKDR(new int[] {0, 1, 1, 1, 1}, 0.1, 0.5);
		trellis.initBKDR(new int[] {0, 0, 1, 0, 1}, 0.1, 0.5);
		
		trellis.print(System.out);
	}
}
