package ru.ifmo.codes.tasks;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import ru.ifmo.codes.Code;


public class Task2_3 {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream("input.txt"));
		PrintStream out = System.out;//new PrintStream(new FileOutputStream("output.txt"));
		out.println("Задание 2.3");
		int k = in.nextInt();
		int n = in.nextInt();
		int g[][] = new int[k][n];
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				g[i][j] = in.nextInt();
			}
		}
		Code.setPrintStream(out);
		Code.setMode(Code.SHOW_STEPS);
		Code original = new Code(g);
		out.println("Дан исходный код, заданный матрицей");
		original.printCode(out);
		Code dual = original.getDualCode();
		
		out.print("Минимальное расстояние исходного кода равно " + original.distance());
		out.print("Минимальное расстояние дуального кода равно " + dual.distance());
	}

}
