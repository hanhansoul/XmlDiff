package diff;

import org.dom4j.DocumentException;

import static diff.SimpleOperationValue.opValue;

public class XmlDiff {

    private Tree leftTree;
    private Tree rightTree;
    private OperationValue[][] permanentArr;  // permanent array
    private OperationValue[][] temporaryArr;  // temporary array
    private PreviousOperation[][] previousOpArr;

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new Tree(leftFileName);
        rightTree = new Tree(rightFileName);
        permanentArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr[0][0] = new SimpleOperationValue();

        previousOpArr = new PreviousOperation[leftTree.size + 1][rightTree.size + 1];
    }

    private void compute(int left, int right) throws OpValueElementNullException {
        System.out.println(left + " " + right);
//        temporaryArr[0][0] = new XmlOperationValue(0, 0, 0);
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
//        System.out.println(left + " " + right + ": ");
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            temporaryArr[i][0] = i - 1 < leftNode.leftMostNodeId ? opValue(leftNode, null) :
                    temporaryArr[i - 1][0].add(i - 1, 0, opValue(leftNode, null));
//            System.out.println(i + " " + 0 + ": " + temporaryArr[i][0]);
        }
        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            temporaryArr[0][j] = j - 1 < rightNode.leftMostNodeId ? opValue(null, rightNode) :
                    temporaryArr[0][j - 1].add(0, j - 1, opValue(null, rightNode));
//            System.out.println(0 + " " + j + ": " + temporaryArr[0][j]);
        }
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            for (int j = rightNode.leftMostNodeId; j <= right; j++) {
                if (leftTree.nodeSequence[i].leftMostNodeId == leftNode.leftMostNodeId &&
                        rightTree.nodeSequence[j].leftMostNodeId == rightNode.leftMostNodeId) {
                    int ix = i - 1 < leftNode.leftMostNodeId ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNodeId ? 0 : j - 1;
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    permanentArr[i][j] = temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(ix, j, opValue(leftChildNode, null)),      // 删除节点i
                            temporaryArr[i][jx].add(i, jx, opValue(null, rightChildNode)),      // 增加节点j
                            temporaryArr[ix][jx].add(ix, jx, opValue(leftChildNode, rightChildNode)));  // 将节点i修改为节点j
                    System.out.println(i + " " + j + ": " +
                            ((SimpleOperationValue) temporaryArr[i][j]).value + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevX + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevY);
                } else {
                    int ix = i - 1 < leftNode.leftMostNodeId ? 0 : i - 1;
                    int jx = j - 1 < rightNode.leftMostNodeId ? 0 : j - 1;
                    int iy = leftTree.nodeSequence[i].leftMostNodeId - 1 < leftNode.leftMostNodeId ?
                            0 : leftTree.nodeSequence[i].leftMostNodeId - 1;
                    int jy = rightTree.nodeSequence[j].leftMostNodeId - 1 < rightNode.leftMostNodeId ?
                            0 : rightTree.nodeSequence[j].leftMostNodeId - 1;
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(ix, j, opValue(leftChildNode, null)),
                            temporaryArr[i][jx].add(i, jx, opValue(null, rightChildNode)),
                            temporaryArr[iy][jy].add(iy, jy, permanentArr[i][j]));
                    System.out.println(i + " " + j + ": " +
                            ((SimpleOperationValue) temporaryArr[i][j]).value + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevX + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevY);
                }
            }
        }
        System.out.println();
//        fillZero(temporaryArr);
    }

    public void solve() throws DocumentException, OpValueElementNullException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        System.out.println();
        for (int i = 1; i <= leftTree.size; i++) {
            for (int j = 1; j <= leftTree.size; j++) {
                System.out.print(((SimpleOperationValue) permanentArr[i][j]).value + " ");
            }
            System.out.println();
        }
        System.out.println(leftTree.rootId + " " + rightTree.rootId + " " +
                ((SimpleOperationValue) permanentArr[leftTree.rootId][rightTree.rootId]).value);

        System.out.println();
        backtrace((SimpleOperationValue) permanentArr[leftTree.rootId][rightTree.rootId]);
    }

    public void backtrace(SimpleOperationValue v) {
        if (v == null) {
            return;
        }
        System.out.println("current: " + v.value + ", prev: " + v.prevX + " " + v.prevY);
        backtrace((SimpleOperationValue) permanentArr[v.prevX][v.prevY]);
    }

    public static void main(String[] args) throws DocumentException, OpValueElementNullException {
        long beginTime = System.currentTimeMillis();
        XmlDiff xmlDiff = new XmlDiff();
//        xmlDiff.initialization("data/CSCA350-353000-00M01-01-X_2_20180901.xml",
//                "data/CSCA350-353000-00M01-01-X_3_20181001.xml");
        xmlDiff.initialization("data/left.xml", "data/right.xml");
        xmlDiff.solve();
        long endTime = System.currentTimeMillis();
//        System.out.println((endTime - beginTime) / 1000);
    }
}
