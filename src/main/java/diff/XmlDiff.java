package diff;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class XmlDiff {

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

    private void fillZero(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = 0;
            }
        }
    }

    private int opValue(int left, int right) {
        Element leftElement = leftTree.sequence[left].element;
        Element rightElement = rightTree.sequence[right].element;
        int res;
        if (leftElement != null && rightElement != null && leftElement.getName().equals(rightElement.getName())) {
            res = 0;
        } else {
            res = 1;
        }
        return res;
    }

    private void compute(int left, int right) {
//        System.out.println(left + " " + right);
        T[0][0] = 0;
        Node leftNode = leftTree.sequence[left];
        Node rightNode = rightTree.sequence[right];
        for (int i = leftNode.leftMostNode; i <= left; i++) {
            T[i][0] = T[i - 1][0] + opValue(i, 0);
        }
        for (int j = rightNode.leftMostNode; j <= right; j++) {
            T[0][j] = T[0][j - 1] + opValue(0, j);
        }
        for (int i = leftNode.leftMostNode; i <= left; i++) {
            for (int j = rightNode.leftMostNode; j <= right; j++) {
                if (leftTree.sequence[i].leftMostNode == leftNode.leftMostNode &&
                        rightTree.sequence[j].leftMostNode == rightNode.leftMostNode) {
                    F[i][j] = T[i][j] = Math.min(
                            T[i - 1][j] + opValue(i, 0), Math.min(
                                    T[i][j - 1] + opValue(0, j),
                                    T[i - 1][j - 1] + opValue(i, j)));
                } else {
                    T[i][j] = Math.min(Math.min(
                            T[i - 1][j] + opValue(i - 1, j),
                            T[i][j - 1] + opValue(i, j - 1)),
                            T[leftTree.sequence[i].leftMostNode - 1][rightTree.sequence[j].leftMostNode - 1] + F[i][j]);
                }
            }
        }
        fillZero(T);
//        System.out.println(T[3][2]);
        if (left == 6 && right == 6) {
            outputF(left, right);
        }
    }

    public void solve() throws DocumentException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        System.out.println(F[leftTree.rootId][rightTree.rootId]);
    }

    private void outputF(int x, int y) {
        for (int i = 1; i <= x; i++) {
            for (int j = 1; j <= y; j++) {
                System.out.print(F[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws DocumentException {
        XmlDiff xmlDiff = new XmlDiff();
        xmlDiff.initialization("data/left.xml", "data/right.xml");
//        xmlDiff.leftTree.keyRootsTraversal();
//        System.out.println();
//        xmlDiff.rightTree.keyRootsTraversal();
        xmlDiff.solve();
    }
}
