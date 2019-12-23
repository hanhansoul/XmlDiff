package newdiff.abs.work;

import newdiff.abs.output.Path;
import newdiff.abs.output.PathNode;
import org.dom4j.DocumentException;

public abstract class AbstractDiff {

    protected Tree leftTree;
    protected Tree rightTree;
    protected OperationValue[][] permanentArr;
    protected OperationValue[][] temporaryArr;
    public OperationValue[] operationValues;
    protected Path[][] operationPaths;

    /**
     * 初始化
     */
    public abstract void initialize(String leftFileName, String rightFileName) throws DocumentException;

    /**
     * generateOperationMove()用于生成OperationMove
     * 根据leftNode和rightNode生成OperationMove
     */
    public abstract OperationMove generateOperationMove(Node leftNode, Node rightNode, OperationValue operationValue);

    /**
     * 根据permanentOperationValue生成OperationMove
     */
    public abstract OperationMove generateOperationMove(OperationValue permanentOperationValue, OperationValue operationValue);

    /**
     * generateOperationValue()用于更新OperationValue[index]中的数据
     */
    public abstract void generateOperationValueForOperationValueArray(int index, OperationValue operationValue, OperationMove operationMove);

    public abstract void generateOperationValueForOperationValueArray(int index, OperationValue operationValue, OperationValue permanentOperationValue);

    private int checkIndexMargin(int index, Node ancestorNode) {
        return index - 1 < ancestorNode.leftMostNodeId ? 0 : index - 1;
    }

    private int checkNodeIndexMargin(Node childNode, Node ancestorNode) {
        return childNode.leftMostNodeId - 1 < ancestorNode.leftMostNodeId ? 0 : childNode.leftMostNodeId - 1;
    }

    private void compute(int left, int right) {
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            if (i - 1 < leftNode.leftMostNodeId) {
                temporaryArr[i][0].addAndAssign(temporaryArr[0][0], generateOperationMove(leftNode, null, temporaryArr[0][0]));
            } else {
                temporaryArr[i][0].addAndAssign(temporaryArr[i - 1][0], generateOperationMove(leftNode, null, temporaryArr[i - 1][0]));
            }
        }

        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            if (j - 1 < rightNode.leftMostNodeId) {
                temporaryArr[0][j].addAndAssign(temporaryArr[0][0], generateOperationMove(null, rightNode, temporaryArr[0][0]));
            } else {
                temporaryArr[0][j].addAndAssign(temporaryArr[0][j - 1], generateOperationMove(null, rightNode, temporaryArr[0][j - 1]));
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
                    generateOperationValueForOperationValueArray(0, temporaryArr[ix][j], generateOperationMove(leftChildNode, null, temporaryArr[ix][j]));
                    generateOperationValueForOperationValueArray(1, temporaryArr[i][jx], generateOperationMove(null, rightChildNode, temporaryArr[i][jx]));
                    generateOperationValueForOperationValueArray(2, temporaryArr[ix][jx], generateOperationMove(leftChildNode, rightChildNode, temporaryArr[ix][jx]));
                    temporaryArr[i][j].findOptimalOperationValueAndAssign(operationValues);
                    permanentArr[i][j].assign(temporaryArr[i][j]);
                    permanentNodePathTrace(permanentArr[i][j], i, j);
                } else {
                    int ix = checkIndexMargin(i, leftNode);
                    int jx = checkIndexMargin(j, rightNode);
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    int iy = checkNodeIndexMargin(leftChildNode, leftNode);
                    int jy = checkNodeIndexMargin(rightChildNode, rightNode);
                    generateOperationValueForOperationValueArray(0, temporaryArr[ix][j], generateOperationMove(leftChildNode, null, temporaryArr[ix][j]));
                    generateOperationValueForOperationValueArray(1, temporaryArr[i][jx], generateOperationMove(null, rightChildNode, temporaryArr[i][jx]));
                    generateOperationValueForOperationValueArray(2, temporaryArr[iy][jy], generateOperationMove(permanentArr[i][j], temporaryArr[iy][jy]));
                    temporaryArr[i][j].findOptimalOperationValueAndAssign(operationValues);
                }
            }
        }
    }

    public void solve() {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        backtrace(permanentArr[leftTree.rootId][rightTree.rootId]);
        findPath(operationPaths[leftTree.rootId][rightTree.rootId]);
    }

    public void permanentNodePathTrace(OperationValue curNode, int nodeX, int nodeY) {
        if (curNode.curX == 0 && curNode.curY == 0) {
            return;
        }
        OperationValue prevNode = temporaryArr[curNode.prevX][curNode.prevY];
        permanentNodePathTrace(prevNode, nodeX, nodeY);
        if (operationPaths[nodeX][nodeY] == null) {
            operationPaths[nodeX][nodeY] = new Path();
        }
        operationPaths[nodeX][nodeY].nodes.add(new PathNode(curNode));
    }

    private void backtrace(OperationValue operationValue) {
        if (operationValue == null || operationValue.prevX == 0 && operationValue.prevY == 0) {
            return;
        }
        if (operationValue.operationMoveType == OperationMoveType.INSERT) {
            rightTree.nodeSequence[operationValue.curY].operationMoveType = OperationMoveType.INSERT;
        } else if (operationValue.operationMoveType == OperationMoveType.DELETE) {
            leftTree.nodeSequence[operationValue.curX].operationMoveType = OperationMoveType.DELETE;
        } else if (operationValue.operationMoveType == OperationMoveType.CHANGE) {
            leftTree.nodeSequence[operationValue.curX].operationMoveType = OperationMoveType.CHANGE;
            rightTree.nodeSequence[operationValue.curY].operationMoveType = OperationMoveType.CHANGE;
        } else {
            leftTree.nodeSequence[operationValue.curX].operationMoveType = OperationMoveType.UNCHANGE;
            rightTree.nodeSequence[operationValue.curY].operationMoveType = OperationMoveType.UNCHANGE;
        }
        backtrace(permanentArr[operationValue.prevX][operationValue.prevY]);
    }

    private void findPath(Path path) {
        if (path == null) {
            return;
        }
        for (PathNode node : path.nodes) {
            if (node.isFromPermanent) {
                findPath(operationPaths[node.curX][node.curY]);
            } else {
                if (node.operationMoveType == OperationMoveType.INSERT) {
                    rightTree.nodeSequence[node.curY].operationMoveType = OperationMoveType.INSERT;
                } else if (node.operationMoveType == OperationMoveType.DELETE) {
                    leftTree.nodeSequence[node.curX].operationMoveType = OperationMoveType.DELETE;
                } else if (node.operationMoveType == OperationMoveType.CHANGE) {
                    leftTree.nodeSequence[node.curX].operationMoveType = OperationMoveType.CHANGE;
                    rightTree.nodeSequence[node.curY].operationMoveType = OperationMoveType.CHANGE;
                }
            }
        }
    }
}
