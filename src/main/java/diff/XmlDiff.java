package diff;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import static diff.XmlDiffHelper.fillZero;

public class XmlDiff {
    private final int ELEMENT_NAME_DIFF_VAL = 10;
    private final int ELEMENT_ATTRIBUTE_DIFF_VAL = 1;
    private final int ELEMENT_TEXT_DIFF_VAL = 1;
    private final int ELEMENT_MISSING_VAL = 10;
    private Tree leftTree;
    private Tree rightTree;
    private int[][] F;  // permanent array
    private int[][] T;  // temporary array

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new Tree(leftFileName);
        rightTree = new Tree(rightFileName);
        F = new int[leftTree.size + 1][rightTree.size + 1];
        fillZero(F);
        T = new int[leftTree.size + 1][rightTree.size + 1];
        fillZero(T);
    }

    private int opValue(int left, int right) throws OpValueElementNullException {
        if (left == 0 && right == 0) {
            throw new OpValueElementNullException();
        }
        if (left == 0 || right == 0) {
            return ELEMENT_MISSING_VAL;
        }
        Element leftElement = leftTree.sequence[left].getElement();
        Element rightElement = rightTree.sequence[right].getElement();
        if (leftElement == null || rightElement == null) {
            throw new OpValueElementNullException();
        }
        if (!leftElement.getName().equals(rightElement.getName())) {
            return ELEMENT_NAME_DIFF_VAL;
        } else {
            int value = 0;

            return value;
        }

//        int res;
//        if (leftElement != null && rightElement != null && leftElement.getName().equals(rightElement.getName())) {
//            res = 0;
//        } else {
//            res = 1;
//        }
    }

    private void compute(int left, int right) {
//        System.out.println(left + " " + right);
        T[0][0] = 0;
        Node leftNode = leftTree.sequence[left];
        Node rightNode = rightTree.sequence[right];

//        System.out.println(left + " " + right + ": ");
        for (int i = leftNode.leftMostNode; i <= left; i++) {
            T[i][0] = i - 1 < leftNode.leftMostNode ? opValue(i, 0) : T[i - 1][0] + opValue(i, 0);
//            System.out.println(i + " " + 0 + ": " + T[i][0]);
        }
        for (int j = rightNode.leftMostNode; j <= right; j++) {
            T[0][j] = j - 1 < rightNode.leftMostNode ? opValue(0, j) : T[0][j - 1] + opValue(0, j);
//            System.out.println(0 + " " + j + ": " + T[0][j]);
        }

        for (int i = leftNode.leftMostNode; i <= left; i++) {
            for (int j = rightNode.leftMostNode; j <= right; j++) {
                if (leftTree.sequence[i].leftMostNode == leftNode.leftMostNode &&
                        rightTree.sequence[j].leftMostNode == rightNode.leftMostNode) {
                    int ix = i - 1 < leftNode.leftMostNode ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNode ? 0 : j - 1;
                    F[i][j] = T[i][j] = Math.min(
                            T[ix][j] + opValue(i, 0), Math.min(
                                    T[i][jx] + opValue(0, j),
                                    T[ix][jx] + opValue(i, j)));
//                    System.out.println(i + " " + j + ": " + T[i][j]);
                } else {
                    int ix = i - 1 < leftNode.leftMostNode ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNode ? 0 : j - 1;
                    int iy = leftTree.sequence[i].leftMostNode - 1 < leftNode.leftMostNode ?
                            0 : leftTree.sequence[i].leftMostNode - 1;
                    int jy = rightTree.sequence[j].leftMostNode - 1 < rightNode.leftMostNode ?
                            0 : rightTree.sequence[j].leftMostNode - 1;
                    T[i][j] = Math.min(Math.min(
                            T[ix][j] + opValue(i, 0),
                            T[i][jx] + opValue(0, j)),
                            T[iy][jy] + F[i][j]);
//                    System.out.println(i + " " + j + ": " + T[i][j]);
                }
            }
        }
//        System.out.println();
//        fillZero(T);
    }

    public void solve() throws DocumentException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        System.out.println(F[leftTree.rootId][rightTree.rootId]);
    }

    public static void main(String[] args) throws DocumentException {
        long beginTime = System.currentTimeMillis();
        XmlDiff xmlDiff = new XmlDiff();
        xmlDiff.initialization("data/CSCA350-353000-00M01-01-X_2_20180901.xml",
                "data/CSCA350-353000-00M01-01-X_3_20181001.xml");
        xmlDiff.solve();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - beginTime) / 1000);
    }
}
