package maindiff.abs.work;

import static maindiff.util.Constant.DEBUG;

import maindiff.abs.output.Path;
import maindiff.abs.output.PathNode;
import maindiff.util.OpValueElementNullException;
import maindiff.util.OperationEnum;
import org.dom4j.DocumentException;

public abstract class AbstractDiff {
    protected Tree leftTree;
    protected Tree rightTree;
    protected OperationValue[][] permanentArr;
    protected OperationValue[][] temporaryArr;

    protected Path[][] operationPaths;

    public AbstractDiff() {
    }

    public abstract void initialize(String leftFileName, String rightFileName) throws DocumentException;

//    protected abstract Operation opValue(Node leftNode, Node rightNode) throws OpValueElementNullException;

//    protected abstract void findMinAndAssign(Node leftNode, Node rightNode,
//                                             int i, int j, int ix, int jx,
//                                             boolean b1, boolean b2, boolean b3);

    protected abstract void initialize(int left, int right);

    protected void compute(int left, int right) throws OpValueElementNullException {
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            if (i - 1 < leftNode.leftMostNodeId) {
                temporaryArr[i][0].assign(temporaryArr[0][0], leftNode, null, i, 0, false);
            } else {
                temporaryArr[i][0].assign(temporaryArr[i - 1][0], leftNode, null, i, 0, false);
            }
        }
        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            if (j - 1 < rightNode.leftMostNodeId) {
                temporaryArr[0][j].assign(temporaryArr[0][0], null, rightNode, 0, j, false);
            } else {
                temporaryArr[0][j].assign(temporaryArr[0][j - 1], null, rightNode, 0, j, false);
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
//                    permanentArr[i][j] = temporaryArr[i][j] = min(
//                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j, false),      // 删除节点i
//                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j, false),      // 增加节点j
//                            temporaryArr[ix][jx].add(opValue(leftChildNode, rightChildNode), i, j, false));    // 将节点i修改为节点j
                    permanentNodePathTrace(permanentArr[i][j], i, j);
                } else {
                    int ix = checkIndexMargin(i, leftNode);
                    int jx = checkIndexMargin(j, rightNode);
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    int iy = checkNodeIndexMargin(leftChildNode, leftNode);
                    int jy = checkNodeIndexMargin(rightChildNode, rightNode);
//                    temporaryArr[i][j] = min(
//                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j, false),
//                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j, false),
//                            temporaryArr[iy][jy].add(permanentArr[i][j], i, j, true));
                }
            }
        }
    }

    private int checkIndexMargin(int index, Node ancestorNode) {
        return index - 1 < ancestorNode.leftMostNodeId ? 0 : index - 1;
    }

    private int checkNodeIndexMargin(Node childNode, Node ancestorNode) {
        return childNode.leftMostNodeId - 1 < ancestorNode.leftMostNodeId ? 0 : childNode.leftMostNodeId - 1;
    }

    public void solve() throws DocumentException, OpValueElementNullException {
        initialize(leftTree.size, rightTree.size);
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
//        backtrace((SimpleOperationValue) permanentArr[leftTree.rootId][rightTree.rootId]);
        findPath(operationPaths[leftTree.rootId][rightTree.rootId]);
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

    private void backtrace(OperationValue v) {
        if (v == null) {
            return;
        }
        System.out.println("current: " + v.curX + " " + v.curY + " " + v.value +
                ", prev: " + v.prevX + " " + v.prevY + (v.isFromPermanentArr ? " PermanentArr" : " TemporaryArr") +
                ", op: " + v.operation.op);
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

    /*
    public static void main(String[] args) throws DocumentException, OpValueElementNullException, IOException {
        long beginTime = System.currentTimeMillis();
        AbstractDiff xmlDiff = new AbstractDiff();
        xmlDiff.initialization("data/left.xml",
                "data/right.xml");
//        xmlDiff.initialization("data/left3.xml", "data/right3.xml");
        xmlDiff.solve();
//        xmlDiff.preOrderOutput();
        long solveTime = System.currentTimeMillis();
        System.out.println((solveTime - beginTime) / 1000);
        new SimpleDiffOutput(xmlDiff.leftTree, xmlDiff.rightTree).resultOutput();
//        System.out.println(xmlDiff.leftOutput);
//        System.out.println(xmlDiff.rightOutput);
//        xmlDiff.resultOutput();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - solveTime) / 1000);
    }
    */
}
