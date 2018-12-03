package diff;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.Map;

public class XmlDiff {
    private final XmlOperationValue ELEMENT_ZERO_DIFF_VAL = new XmlOperationValue(0, 0, 0);
    private final XmlOperationValue ELEMENT_NAME_DIFF_VAL = new XmlOperationValue(1, 0, 0);
    private final XmlOperationValue ELEMENT_ATTRIBUTE_DIFF_VAL = new XmlOperationValue(0, 1, 0);
    private final XmlOperationValue ELEMENT_TEXT_DIFF_VAL = new XmlOperationValue(0, 0, 1);
    //    private final XmlOperationValue ELEMENT_MISSING_VAL = 10;
    private Tree leftTree;
    private Tree rightTree;
    private OperationValue[][] permanentArr;  // permanent array
    private OperationValue[][] temporaryArr;  // temporary array

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new Tree(leftFileName);
        rightTree = new Tree(rightFileName);
        permanentArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr[0][0] = ELEMENT_ZERO_DIFF_VAL;
    }

    public OperationValue opValue(int left, int right) throws OpValueElementNullException {
        if (left == 0 && right == 0) {
            throw new OpValueElementNullException();
        }
        if (left == 0 || right == 0) {
            return ELEMENT_NAME_DIFF_VAL;
        }
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
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

            return new XmlOperationValue(0, attributeDiffValue, textDiffValue);
        }

    }

    private void compute(int left, int right) throws OpValueElementNullException {
//        System.out.println(left + " " + right);
//        temporaryArr[0][0] = new XmlOperationValue(0, 0, 0);
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
//        System.out.println(left + " " + right + ": ");
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            temporaryArr[i][0] = i - 1 < leftNode.leftMostNodeId ? opValue(i, 0) : temporaryArr[i - 1][0].add(opValue(i, 0));
//            System.out.println(i + " " + 0 + ": " + temporaryArr[i][0]);
        }
        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            temporaryArr[0][j] = j - 1 < rightNode.leftMostNodeId ? opValue(0, j) : temporaryArr[0][j - 1].add(opValue(0, j));
//            System.out.println(0 + " " + j + ": " + temporaryArr[0][j]);
        }
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            for (int j = rightNode.leftMostNodeId; j <= right; j++) {
                if (leftTree.nodeSequence[i].leftMostNodeId == leftNode.leftMostNodeId &&
                        rightTree.nodeSequence[j].leftMostNodeId == rightNode.leftMostNodeId) {
                    int ix = i - 1 < leftNode.leftMostNodeId ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNodeId ? 0 : j - 1;
                    permanentArr[i][j] = temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(opValue(i, 0)),    // 删除节点i
                            temporaryArr[i][jx].add(opValue(0, j)),     // 增加节点j
                            temporaryArr[ix][jx].add(opValue(i, j)));        // 将节点i修改为节点j
//                    System.out.println(i + " " + j + ": " + temporaryArr[i][j]);
                } else {
                    int ix = i - 1 < leftNode.leftMostNodeId ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNodeId ? 0 : j - 1;
                    int iy = leftTree.nodeSequence[i].leftMostNodeId - 1 < leftNode.leftMostNodeId ?
                            0 : leftTree.nodeSequence[i].leftMostNodeId - 1;
                    int jy = rightTree.nodeSequence[j].leftMostNodeId - 1 < rightNode.leftMostNodeId ?
                            0 : rightTree.nodeSequence[j].leftMostNodeId - 1;
                    temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(opValue(i, 0)),
                            temporaryArr[i][jx].add(opValue(0, j)),
                            temporaryArr[iy][jy].add(permanentArr[i][j]));
//                    System.out.println(i + " " + j + ": " + temporaryArr[i][j]);
                }
            }
        }
//        System.out.println();
//        fillZero(temporaryArr);
    }

    public void solve() throws DocumentException, OpValueElementNullException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        System.out.println(permanentArr[leftTree.rootId][rightTree.rootId]);
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
