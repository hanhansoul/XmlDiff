package example;

import java.util.StringTokenizer;

/**
 * Created by Administrator on 2018/12/2 0002.
 */
public class LongestCommentSequenceExample {
    public static String[] buildStringArray(StringTokenizer st) {
//        System.out.println(st.countTokens());
        String[] strings = new String[st.countTokens() + 1];
        for (int i = 1; st.hasMoreTokens(); i++) {
            strings[i] = st.nextToken();
        }
        return strings;
    }

    public static void main(String[] args) {
        String[] s1 = buildStringArray(new StringTokenizer("abc def hijk CCC"));
        String[] s2 = buildStringArray(new StringTokenizer("ACC def hijk CCC"));
        int[][] D = new int[s1.length + 1][s2.length + 1];
        D[0][0] = 0;
        for (int i = 1; i < s1.length; i++) {
            for (int j = 1; j < s2.length; j++) {
                if (s1[i].equals(s2[j])) {
                    D[i][j] = D[i - 1][j - 1] + 1;
                } else {
                    D[i][j] = Math.max(D[i - 1][j], D[i][j - 1]);
                }
            }
        }
        System.out.println(D[s1.length - 1][s2.length - 1]);
    }
}
