package diff;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.Map;

public class XmlDiff {
    private final OperationValue ELEMENT_ZERO_DIFF_VAL = new OperationValue(0, 0, 0);
    private final OperationValue ELEMENT_NAME_DIFF_VAL = new OperationValue(1, 0, 0);
    private final OperationValue ELEMENT_ATTRIBUTE_DIFF_VAL = new OperationValue(0, 1, 0);
    private final OperationValue ELEMENT_TEXT_DIFF_VAL = new OperationValue(0, 0, 1);
    //    private final OperationValue ELEMENT_MISSING_VAL = 10;
    private Tree leftTree;
    private Tree rightTree;
    private OperationValue[][] F;  // permanent array
    private OperationValue[][] T;  // temporary array

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new Tree(leftFileName);
        rightTree = new Tree(rightFileName);
        F = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        T = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        T[0][0] = ELEMENT_ZERO_DIFF_VAL;
    }

    public OperationValue opValue(int left, int right) throws OpValueElementNullException {
        if (left == 0 && right == 0) {
            throw new OpValueElementNullException();
        }
        if (left == 0 || right == 0) {
            return ELEMENT_NAME_DIFF_VAL;
        }
        Node leftNode = leftTree.sequence[left];
        Node rightNode = rightTree.sequence[right];
        Element leftElement = leftNode.element;
        Element rightElement = rightNode.element;
        if (leftElement == null || rightElement == null) {
            throw new OpValueElementNullException();
        }
        if (!leftElement.getName().equals(rightElement.getName())) {
            return ELEMENT_NAME_DIFF_VAL;
        } else {
            // TODO
            int attributeDiffValue = 0;
            Map<String, String> leftAttributeMap = leftNode.attributesMap;
            Map<String, String> rightAttributeMap = rightNode.attributesMap;
            if (leftAttributeMap.size() < rightAttributeMap.size()) {
                for (Map.Entry entry : leftAttributeMap.entrySet()) {
                    if (!(rightAttributeMap.containsKey(entry.getKey()) &&
                            rightAttributeMap.get(entry.getKey()).equals(entry.getValue()))) {
                        attributeDiffValue++;
                    }
                }
            } else {
                for (Map.Entry entry : rightAttributeMap.entrySet()) {
                    if (!(leftAttributeMap.containsKey(entry.getKey()) &&
                            leftAttributeMap.get(entry.getKey()).equals(entry.getValue()))) {
                        attributeDiffValue++;
                    }
                }
            }

            String[] leftText = leftNode.text;
            String[] rightText = rightNode.text;
            int[][] D = new int[leftText.length + 1][rightText.length + 1];
            D[0][0] = 0;
            for (int i = 1; i < leftText.length; i++) {
                for (int j = 1; j < rightText.length; j++) {
                    if (leftText[i].equals(rightText[j])) {
                        D[i][j] = D[i - 1][j - 1] + 1;
                    } else {
                        D[i][j] = Math.max(D[i - 1][j], D[i][j - 1]);
                    }
                }
            }
            int textDiffValue = leftText.length + rightText.length - 2 * D[leftText.length - 1][leftText.length - 1];

            return new OperationValue(0, attributeDiffValue, textDiffValue);
        }

    }

    private void compute(int left, int right) throws OpValueElementNullException {
//        System.out.println(left + " " + right);
//        T[0][0] = new OperationValue(0, 0, 0);
        Node leftNode = leftTree.sequence[left];
        Node rightNode = rightTree.sequence[right];
//        System.out.println(left + " " + right + ": ");
        for (int i = leftNode.leftMostNode; i <= left; i++) {
            T[i][0] = i - 1 < leftNode.leftMostNode ? opValue(i, 0) : T[i - 1][0].add(opValue(i, 0));
//            System.out.println(i + " " + 0 + ": " + T[i][0]);
        }
        for (int j = rightNode.leftMostNode; j <= right; j++) {
            T[0][j] = j - 1 < rightNode.leftMostNode ? opValue(0, j) : T[0][j - 1].add(opValue(0, j));
//            System.out.println(0 + " " + j + ": " + T[0][j]);
        }

        for (int i = leftNode.leftMostNode; i <= left; i++) {
            for (int j = rightNode.leftMostNode; j <= right; j++) {
                if (leftTree.sequence[i].leftMostNode == leftNode.leftMostNode &&
                        rightTree.sequence[j].leftMostNode == rightNode.leftMostNode) {
                    int ix = i - 1 < leftNode.leftMostNode ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNode ? 0 : j - 1;
                    F[i][j] = T[i][j] = XmlDiffHelper.min(
                            T[ix][j].add(opValue(i, 0)),
                            T[i][jx].add(opValue(0, j)),
                            T[ix][jx].add(opValue(i, j)));
//                    System.out.println(i + " " + j + ": " + T[i][j]);
                } else {
                    int ix = i - 1 < leftNode.leftMostNode ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNode ? 0 : j - 1;
                    int iy = leftTree.sequence[i].leftMostNode - 1 < leftNode.leftMostNode ?
                            0 : leftTree.sequence[i].leftMostNode - 1;
                    int jy = rightTree.sequence[j].leftMostNode - 1 < rightNode.leftMostNode ?
                            0 : rightTree.sequence[j].leftMostNode - 1;
                    T[i][j] = XmlDiffHelper.min(
                            T[ix][j].add(opValue(i, 0)),
                            T[i][jx].add(opValue(0, j)),
                            T[iy][jy].add(F[i][j]));
//                    System.out.println(i + " " + j + ": " + T[i][j]);
                }
            }
        }
//        System.out.println();
//        fillZero(T);
    }

    public void solve() throws DocumentException, OpValueElementNullException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        System.out.println(F[leftTree.rootId][rightTree.rootId]);
    }

    public static void main(String[] args) throws DocumentException, OpValueElementNullException {
        long beginTime = System.currentTimeMillis();
        XmlDiff xmlDiff = new XmlDiff();
        xmlDiff.initialization("data/CSCA350-353000-00M01-01-X_2_20180901.xml",
                "data/CSCA350-353000-00M01-01-X_3_20181001.xml");
        xmlDiff.solve();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - beginTime) / 1000);
    }
}
