package diff;

public class XmlDiffHelper {
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

    public static OperationValue min(OperationValue... opvs) {
        OperationValue minValue = null;
        for (OperationValue value : opvs) {
            if (minValue == null) {
                minValue = value;
            } else if (minValue.compareTo(value) > 0) {
                minValue = value;
            }
        }
        return minValue;
    }
}
