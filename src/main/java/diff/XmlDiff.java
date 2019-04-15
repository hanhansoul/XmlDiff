package diff;

import diff.simple.SimpleDiffOutput;
import diff.simple.SimpleOperationValue;
import diff.simple.SimplePathNode;
import diff.simple.SimpleTree;
import org.dom4j.DocumentException;

import java.io.IOException;

import static diff.Constant.DEBUG;

import static diff.simple.SimpleOperationValue.opValue;

public class XmlDiff {

    private SimpleTree leftTree;
    private SimpleTree rightTree;
    private OperationValue[][] permanentArr;
    private OperationValue[][] temporaryArr;
    private Path[][] operationPaths;

    public XmlDiff() {
    }

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new SimpleTree(leftFileName);
        rightTree = new SimpleTree(rightFileName);
        permanentArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr[0][0] = new SimpleOperationValue();
        operationPaths = new Path[leftTree.size + 1][rightTree.size + 1];
    }

    private void compute(int left, int right) throws OpValueElementNullException {
        if (DEBUG) {
            System.out.println(left + " " + right);
        }
//        temporaryArr[0][0] = new XmlOperationValue(0, 0, 0);
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            temporaryArr[i][0] = i - 1 < leftNode.leftMostNodeId ?
                    temporaryArr[0][0].add(opValue(leftNode, null), i, 0, false) :
                    temporaryArr[i - 1][0].add(opValue(leftNode, null), i, 0, false);
            if (DEBUG) {
                System.out.println("0: temporaryArr[" + i + "][" + 0 + "] = " +
                        ((SimpleOperationValue) temporaryArr[i][0]).value + " from temporaryArr[" +
                        ((SimpleOperationValue) temporaryArr[i][0]).prevX + "][" +
                        ((SimpleOperationValue) temporaryArr[i][0]).prevY + "] through " +
                        ((SimpleOperationValue) temporaryArr[i][0]).operation.op);
            }
        }
        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            temporaryArr[0][j] = j - 1 < rightNode.leftMostNodeId ?
                    temporaryArr[0][0].add(opValue(null, rightNode), 0, j, false) :
                    temporaryArr[0][j - 1].add(opValue(null, rightNode), 0, j, false);
            if (DEBUG) {
                System.out.println("0: temporaryArr[" + 0 + "][" + j + "] = " +
                        ((SimpleOperationValue) temporaryArr[0][j]).value + " from temporaryArr[" +
                        ((SimpleOperationValue) temporaryArr[0][j]).prevX + "][" +
                        ((SimpleOperationValue) temporaryArr[0][j]).prevY + "] through " +
                        ((SimpleOperationValue) temporaryArr[0][j]).operation.op);
            }
        }
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            for (int j = rightNode.leftMostNodeId; j <= right; j++) {
                if (leftTree.nodeSequence[i].leftMostNodeId == leftNode.leftMostNodeId &&
                        rightTree.nodeSequence[j].leftMostNodeId == rightNode.leftMostNodeId) {
                    int ix = checkIndexMargin(i, leftNode);
                    int jx = checkIndexMargin(j, rightNode);
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    permanentArr[i][j] = temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j, false),      // 删除节点i
                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j, false),      // 增加节点j
                            temporaryArr[ix][jx].add(opValue(leftChildNode, rightChildNode), i, j, false));    // 将节点i修改为节点j
                    permanentNodePathTrace(permanentArr[i][j], i, j);

                    totalPermanent++;
                    if (DEBUG) {
                        System.out.println("1: permanentArr[" + i + "][" + j + "] = " +
                                ((SimpleOperationValue) temporaryArr[i][j]).value + " from temporaryArr[" +
                                ((SimpleOperationValue) temporaryArr[i][j]).prevX + "][" +
                                ((SimpleOperationValue) temporaryArr[i][j]).prevY + "] through " +
                                ((SimpleOperationValue) temporaryArr[i][j]).operation.op);
                    }
                } else {
                    int ix = checkIndexMargin(i, leftNode);
                    int jx = checkIndexMargin(j, rightNode);
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    int iy = checkNodeIndexMargin(leftChildNode, leftNode);
                    int jy = checkNodeIndexMargin(rightChildNode, rightNode);
                    temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j, false),
                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j, false),
                            temporaryArr[iy][jy].add(permanentArr[i][j], i, j, true));
                    if (DEBUG) {
                        if (temporaryArr[i][j].isFromPermanentArr) {
                            System.out.println("2: temporaryArr[" + i + "][" + j + "] = " +
                                    ((SimpleOperationValue) temporaryArr[i][j]).value + " from temporaryArr[" +
                                    ((SimpleOperationValue) temporaryArr[i][j]).prevX + "][" +
                                    ((SimpleOperationValue) temporaryArr[i][j]).prevY + "] through " +
                                    ((SimpleOperationValue) temporaryArr[i][j]).operation.op +
                                    " is from permanentArr[" + i + "][" + j + "] = " + ((SimpleOperationValue) permanentArr[i][j]).value);
                        } else {
                            System.out.println("2: temporaryArr[" + i + "][" + j + "] = " +
                                    ((SimpleOperationValue) temporaryArr[i][j]).value + " from temporaryArr[" +
                                    ((SimpleOperationValue) temporaryArr[i][j]).prevX + "][" +
                                    ((SimpleOperationValue) temporaryArr[i][j]).prevY + "] through " +
                                    ((SimpleOperationValue) temporaryArr[i][j]).operation.op);
                        }
                    }
                }
            }
        }
        if (DEBUG) {
            System.out.println();
        }
    }

    private int checkIndexMargin(int index, Node ancestorNode) {
        return index - 1 < ancestorNode.leftMostNodeId ? 0 : index - 1;
    }

    private int checkNodeIndexMargin(Node childNode, Node ancestorNode) {
        return childNode.leftMostNodeId - 1 < ancestorNode.leftMostNodeId ? 0 : childNode.leftMostNodeId - 1;
    }

    private int totalPermanent = 0;

    public void solve() throws DocumentException, OpValueElementNullException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
//        if (DEBUG) {
//            System.out.println();
//            for (int i = 1; i <= leftTree.size; i++) {
//                for (int j = 1; j <= rightTree.size; j++) {
//                    System.out.print(((SimpleOperationValue) permanentArr[i][j]).value + " ");
//                }
//                System.out.println();
//            }
//            System.out.println(leftTree.rootId + " " + rightTree.rootId + " " +
//                    ((SimpleOperationValue) permanentArr[leftTree.rootId][rightTree.rootId]).value);
//
//            System.out.println();
//        }

        backtrace((SimpleOperationValue) permanentArr[leftTree.rootId][rightTree.rootId]);
        findPath(operationPaths[leftTree.rootId][rightTree.rootId]);

//        if (DEBUG) {
//            for (Node node : leftTree.nodeSequence) {
//                if (node == null) {
//                    continue;
//                }
//                System.out.println(node.id + " " + node.op);
//            }
//            for (Node node : rightTree.nodeSequence) {
//                if (node == null) {
//                    continue;
//                }
//                System.out.println(node.id + " " + node.op);
//            }
//            System.out.println(totalPermanent);
//        }
    }

    private void permanentNodePathTrace(OperationValue curNode, int nodeX, int nodeY) {
        if (curNode.curX == 0 && curNode.curY == 0) {
            return;
        }
        OperationValue prevNode = temporaryArr[curNode.prevX][curNode.prevY];
        permanentNodePathTrace(prevNode, nodeX, nodeY);
        if (operationPaths[nodeX][nodeY] == null) {
            operationPaths[nodeX][nodeY] = new Path();
        }
        operationPaths[nodeX][nodeY].nodes.add(new SimplePathNode(curNode));
    }

    private void findPath(Path path) {
        if (path == null) {
            return;
        }
        for (PathNode node : path.nodes) {
            if (node.isFromPermanent) {
                findPath(operationPaths[node.curX][node.curY]);
            } else {
                if (node.op == OperationEnum.INSERT) {
                    rightTree.nodeSequence[node.curY].op = OperationEnum.INSERT;
                } else if (node.op == OperationEnum.DELETE) {
                    leftTree.nodeSequence[node.curX].op = OperationEnum.DELETE;
                } else if (node.op == OperationEnum.CHANGE) {
                    leftTree.nodeSequence[node.curX].op = OperationEnum.CHANGE;
                    rightTree.nodeSequence[node.curY].op = OperationEnum.CHANGE;
                }
            }
        }
    }

    private void backtrace(SimpleOperationValue v) {
        if (v == null) {
            return;
        }
        System.out.println("current: " + v.curX + " " + v.curY + " " + v.value +
                ", prev: " + v.prevX + " " + v.prevY + (v.isFromPermanentArr ? " PermanentArr" : " TemporaryArr") +
                ", operationType: " + v.operation.op);
        if (v == null || v.prevX == 0 && v.prevY == 0) {
            return;
        }
        if (v.operation.op == OperationEnum.INSERT) {
            rightTree.nodeSequence[v.curY].op = OperationEnum.INSERT;
        } else if (v.operation.op == OperationEnum.DELETE) {
            leftTree.nodeSequence[v.curX].op = OperationEnum.DELETE;
        } else if (v.operation.op == OperationEnum.CHANGE) {
            leftTree.nodeSequence[v.curX].op = OperationEnum.CHANGE;
            rightTree.nodeSequence[v.curY].op = OperationEnum.CHANGE;
        }
        backtrace((SimpleOperationValue) temporaryArr[v.prevX][v.prevY]);
    }

    public static void main(String[] args) throws DocumentException, OpValueElementNullException, IOException {
        long beginTime = System.currentTimeMillis();
        XmlDiff xmlDiff = new XmlDiff();
        xmlDiff.initialization("data/left.xml", "data/right.xml");
//        xmlDiff.initialization("data/left3.xml", "data/right3.xml");
        xmlDiff.solve();
        long solveTime = System.currentTimeMillis();
        System.out.println((solveTime - beginTime) / 1000);
        new SimpleDiffOutput(xmlDiff.leftTree, xmlDiff.rightTree).resultOutput();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - solveTime) / 1000);
    }
}
