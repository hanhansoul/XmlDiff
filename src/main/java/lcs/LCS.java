package lcs;

public class LCS {

    private static void show(int i, int j, int[][] p, String s) {
        if (i == 0 || j == 0) {
            return;
        }
        if (p[i][j] == 1) {
            show(i - 1, j - 1, p, s);
            System.out.print(s.charAt(i - 1));
        } else if (p[i][j] == 2) {
            show(i - 1, j, p, s);
        } else {
            show(i, j - 1, p, s);
        }
    }

    private static void solve() {
        String s1 = "ABCBBCABAA";
        String s2 = "ACCBCAABBC";
        int[][] f = new int[s1.length() + 1][s2.length() + 1];
        int[][] p = new int[s1.length() + 1][s2.length() + 1];
        f[0][0] = 0;
        for (int i = 1; i < s1.length() + 1; i++) {
            for (int j = 1; j < s2.length() + 1; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    f[i][j] = f[i - 1][j - 1] + 1;
                    p[i][j] = 1;
                } else if (f[i - 1][j] > f[i][j - 1]) {
                    f[i][j] = f[i - 1][j];
                    p[i][j] = 2;
                } else {
                    f[i][j] = f[i][j - 1];
                    p[i][j] = 3;
                }
            }
        }
        System.out.println(f[s1.length()][s2.length()]);
        show(s1.length(), s2.length(), p, s1);
    }

    public static void main(String[] args) {
        solve();
    }
}
