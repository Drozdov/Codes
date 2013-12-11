package ru.ifmo.codes.tasks;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import ru.ifmo.codes.BinaryMath;
import ru.ifmo.codes.Code;

public class Task2_7 {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream("input.txt"));
		int r = in.nextInt();
		int n = in.nextInt();
		int h[][] = new int[r][n];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < n; j++) {
				h[i][j] = in.nextInt();
			}
		}
		in.close();
		int[][] table = new int[1 << r][];
		for (int err = 0; err < 1 << n; err++) {
			int[] e = BinaryMath.toVector(err, n);
			int[] s = BinaryMath.mutate(e, h);
			int ss = BinaryMath.toInt(s);
			if (table[ss] == null || BinaryMath.compare(table[ss], e)) {
				table[ss] = e;
			}
		}
		System.out.println("Задание 2.7");
		System.out.println("Строим таблицу синдромов для кода, заданного проверочной матрицей");
		new Code(h).printCode(System.out);
		System.out.println("Синдром|Ошибка");
		for (int i = 0; i < 1 << r; i++) {
			int[] s = BinaryMath.toVector(i, r);
			for (int j = 0; j < s.length; j++) {
				System.out.print(s[j] + " ");
			}
			System.out.print("| ");
			int e[] = table[i];
			for (int j = 0; j < e.length; j++) {
				System.out.print(e[j] + " ");
			}
			System.out.println();
		}
		
	}
	

}
