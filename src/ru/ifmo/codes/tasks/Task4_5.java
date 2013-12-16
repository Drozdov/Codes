package ru.ifmo.codes.tasks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import ru.ifmo.codes.Code;
import ru.ifmo.codes.Trellis;

public class Task4_5 {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream("input.txt"));
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
		trellis.initBKDR(new int[] {0, 1, 1, 1, 1}, 0.1, 0.5);
		
		trellis.print(System.out);
		trellis.print(System.out, "WithGamma", Trellis.PRINT_ID, Trellis.PRINT_GAMMA);
		trellis.print(System.out, "WithAlpha", Trellis.PRINT_ALPHA, Trellis.PRINT_GAMMA);
		trellis.print(System.out, "WithBetta", Trellis.PRINT_BETTA, Trellis.PRINT_GAMMA);
		trellis.print(System.out, "WithSigma", Trellis.PRINT_ID, Trellis.PRINT_SIGMA);
		
		System.out.println();
		System.out.println("Логарифмы отношений вероятностей");
		for (int i = 0; i < n; i++) {
			System.out.printf("%d: %.5f", i + 1, trellis.getLikelihood(i));
			System.out.println();
		}
		
		System.out.println("Вероятности векторов y и c:");
		System.out.println("c      p(yc)    p(c|y)");
		for (int i = 0; i < (1 << k); i++) {
			System.out.println(trellis.getVector(i));
		}
	}
}
