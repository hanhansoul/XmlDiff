package maindiff.util;

import maindiff.abs.work.OperationValue;

public class DiffHelper {

    public static void fillZero(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = 0;
            }
        }
    }

    public static void outputArray(int[][] T, int x, int y) {
        for (int i = 1; i <= x; i++) {
            for (int j = 1; j <= y; j++) {
                System.out.print(T[i][j] + " ");
            }
            System.out.println();
        }
    }

}
