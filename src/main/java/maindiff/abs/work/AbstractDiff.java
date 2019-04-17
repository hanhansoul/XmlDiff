package maindiff.abs.work;

import maindiff.abs.output.Path;
import maindiff.abs.output.PathNode;
import maindiff.simple.output.SimplePathNode;
import maindiff.simple.work.SimpleOperationValue;
import maindiff.util.OperationEnum;
import org.dom4j.DocumentException;

import static maindiff.util.Constant.DEBUG;

public abstract class AbstractDiff {
    protected Tree leftTree;
    protected Tree rightTree;
    protected OperationValue[][] permanentArr;
    protected OperationValue[][] temporaryArr;

    protected Path[][] operationPaths;

    public AbstractDiff() {
    }

    /**
     * 数组初始化
     * 1. 初始化XML树结构
     * 2. 初始化递推方程数组permanentArr与temporaryArr，并创建对象
     * 3. 初始化路径数组
     */
    public abstract void initialize(String leftFileName, String rightFileName) throws DocumentException;

    /**
     * 生成中间操作对象进行比较，选取权值最小的赋值给转移方程。
     * isFromPermanentArr始终为false
     */
    public abstract Operation generateOperation(OperationValue arrValue,
                                                Node leftNode, Node rightNode,
                                                OperationEnum operationType);

    /**
     * 生成中间操作对象进行比较，选取权值最小的赋值给转移方程。
     * isFromPermanentArr始终为true
     */
    public abstract Operation generateOperation(OperationValue arrValue,
                                                OperationValue permanentArrValue);

    /**
     * 核心计算方法
     */
    private void compute(int left, int right) {
        if (DEBUG) {
            System.out.println(left + " " + right);
        }
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            if (i - 1 < leftNode.leftMostNodeId) {
                /* temporaryArr[i][0] = temporaryArr[0][0] + operationType(DELETE, leftNode, null) */
                temporaryArr[i][0].assign(temporaryArr[0][0], leftNode, null, i, 0, false);
            } else {
                /* temporaryArr[i][0] = temporaryArr[i - 1][0] + operationType(DELETE, leftNode, null) */
                temporaryArr[i][0].assign(temporaryArr[i - 1][0], leftNode, null, i, 0, false);
            }
            if (DEBUG) {
                System.out.println("0: temporaryArr[" + i + "][" + 0 + "] = " +
                        ((SimpleOperationValue) temporaryArr[i][0]).value + " from temporaryArr[" +
                        ((SimpleOperationValue) temporaryArr[i][0]).prevX + "][" +
                        ((SimpleOperationValue) temporaryArr[i][0]).prevY + "] through " +
                        ((SimpleOperationValue) temporaryArr[i][0]).operationType);
            }
        }

        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            if (j - 1 < rightNode.leftMostNodeId) {
                /* temporaryArr[0][j] = temporaryArr[0][0] + operationType(ADD, null, rightNode) */
                temporaryArr[0][j].assign(temporaryArr[0][0], null, rightNode, 0, j, false);
            } else {
                /* temporaryArr[0][j] = temporaryArr[0][j - 1] + operationType(ADD, null, rightNode) */
                temporaryArr[0][j].assign(temporaryArr[0][j - 1], null, rightNode, 0, j, false);
            }
            if (DEBUG) {
                System.out.println("0: temporaryArr[" + 0 + "][" + j + "] = " +
                        ((SimpleOperationValue) temporaryArr[0][j]).value + " from temporaryArr[" +
                        ((SimpleOperationValue) temporaryArr[0][j]).prevX + "][" +
                        ((SimpleOperationValue) temporaryArr[0][j]).prevY + "] through " +
                        ((SimpleOperationValue) temporaryArr[0][j]).operationType);
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
                    /*
                    temporaryArr[ix][j] + operationType(DELETE, leftChildNode, null)
                    temporaryArr[i][jX] + operationType(ADD, null, rightChildNode)
                    temporaryArr[ix][j] + operationType(UPDATE, leftChildNode, rightChildNode)

                    permanentArr[i][j] = temporaryArr[i][j] = min(
                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j, false),      // 删除节点i
                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j, false),      // 增加节点j
                            temporaryArr[ix][jx].add(opValue(leftChildNode, rightChildNode), i, j, false));    // 将节点i修改为节点j
                    */
                    temporaryArr[i][j].findMinAndAssign(i, j,
                            generateOperation(temporaryArr[ix][j], leftChildNode, null, OperationEnum.DELETE),
                            generateOperation(temporaryArr[i][jx], null, rightChildNode, OperationEnum.INSERT),
                            generateOperation(temporaryArr[ix][jx], leftChildNode, rightChildNode, null)
                    );
                    permanentArr[i][j].assign(temporaryArr[i][j]);
                    permanentNodePathTrace(permanentArr[i][j], i, j);

                    if (DEBUG) {
                        System.out.println("1: permanentArr[" + i + "][" + j + "] = " +
                                ((SimpleOperationValue) temporaryArr[i][j]).value + " from temporaryArr[" +
                                ((SimpleOperationValue) temporaryArr[i][j]).prevX + "][" +
                                ((SimpleOperationValue) temporaryArr[i][j]).prevY + "] through " +
                                ((SimpleOperationValue) temporaryArr[i][j]).operationType);
                    }
                } else {
                    int ix = checkIndexMargin(i, leftNode);
                    int jx = checkIndexMargin(j, rightNode);
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    int iy = checkNodeIndexMargin(leftChildNode, leftNode);
                    int jy = checkNodeIndexMargin(rightChildNode, rightNode);
                    /*
                    temporaryArr[ix][j] + operationType(DELETE, leftChildNode, null)
                    temporaryArr[i][jX] + operationType(ADD, null, rightChildNode)
                    temporaryArr[ix][j] + operationType(UPDATE, leftChildNode, rightChildNode)

                    temporaryArr[i][j] = min(
                        temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j, false),
                        temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j, false),
                        temporaryArr[iy][jy].add(permanentArr[i][j], i, j, true));
                    */
                    temporaryArr[i][j].findMinAndAssign(i, j,
                            generateOperation(temporaryArr[ix][j], leftChildNode, null, OperationEnum.DELETE),
                            generateOperation(temporaryArr[i][jx], null, rightChildNode, OperationEnum.INSERT),
                            generateOperation(temporaryArr[iy][jy], permanentArr[i][j])
                    );
                    if (DEBUG) {
                        if (temporaryArr[i][j].isFromPermanentArr) {
                            System.out.println("2: temporaryArr[" + i + "][" + j + "] = " +
                                    ((SimpleOperationValue) temporaryArr[i][j]).value + " from temporaryArr[" +
                                    temporaryArr[i][j].prevX + "][" +
                                    temporaryArr[i][j].prevY + "] through " +
                                    temporaryArr[i][j].operationType +
                                    " is from permanentArr[" + i + "][" + j + "] = " + ((SimpleOperationValue) permanentArr[i][j]).value)
                            ;
                        } else {
                            System.out.println("2: temporaryArr[" + i + "][" + j + "] = " +
                                    ((SimpleOperationValue) temporaryArr[i][j]).value + " from temporaryArr[" +
                                    temporaryArr[i][j].prevX + "][" +
                                    temporaryArr[i][j].prevY + "] through " +
                                    temporaryArr[i][j].operationType);
                        }
                    }
                }
            }
        }
        if (DEBUG) {
            System.out.println();
        }
    }

    /**
     * compute()辅助方法
     */
    private int checkIndexMargin(int index, Node ancestorNode) {
        return index - 1 < ancestorNode.leftMostNodeId ? 0 : index - 1;
    }

    /**
     * compute()辅助方法
     */
    private int checkNodeIndexMargin(Node childNode, Node ancestorNode) {
        return childNode.leftMostNodeId - 1 < ancestorNode.leftMostNodeId ? 0 : childNode.leftMostNodeId - 1;
    }

    /**
     * 算法入口
     */
    public void solve() throws DocumentException {
        for (int i = 1; i <= leftTree.keyRootsIndex; i++) {
            for (int j = 1; j <= rightTree.keyRootsIndex; j++) {
                compute(leftTree.keyRoots[i], rightTree.keyRoots[j]);
            }
        }
        backtrace(permanentArr[leftTree.rootId][rightTree.rootId]);
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

    private void backtrace(OperationValue v) {
        if (v == null || v.prevX == 0 && v.prevY == 0) {
            return;
        }
//        System.out.println("current: " + v.curX + " " + v.curY + " " + ((SimpleOperationValue) v).value +
//                ", prev: " + v.prevX + " " + v.prevY + (v.isFromPermanentArr ? " PermanentArr" : " TemporaryArr") +
//                ", operationType: " + v.operationType);
        if (v.operationType == OperationEnum.INSERT) {
            rightTree.nodeSequence[v.curY].operationType = OperationEnum.INSERT;
        } else if (v.operationType == OperationEnum.DELETE) {
            leftTree.nodeSequence[v.curX].operationType = OperationEnum.DELETE;
        } else if (v.operationType == OperationEnum.CHANGE) {
            leftTree.nodeSequence[v.curX].operationType = OperationEnum.CHANGE;
            rightTree.nodeSequence[v.curY].operationType = OperationEnum.CHANGE;
        }
        backtrace(temporaryArr[v.prevX][v.prevY]);
    }

    private void findPath(Path path) {
        if (path == null) {
            return;
        }
        for (PathNode node : path.nodes) {
            if (node.isFromPermanent) {
                findPath(operationPaths[node.curX][node.curY]);
            } else {
                if (node.operationType == OperationEnum.INSERT) {
//                    System.out.println("INSERT: " + node.curY);
                    rightTree.nodeSequence[node.curY].operationType = OperationEnum.INSERT;
                } else if (node.operationType == OperationEnum.DELETE) {
//                    System.out.println("DELETE: " + node.curX);
                    leftTree.nodeSequence[node.curX].operationType = OperationEnum.DELETE;
                } else if (node.operationType == OperationEnum.CHANGE) {
//                    System.out.println("CHANGE: " + node.curX + " " + node.curY);
                    leftTree.nodeSequence[node.curX].operationType = OperationEnum.CHANGE;
                    rightTree.nodeSequence[node.curY].operationType = OperationEnum.CHANGE;
                }
            }
        }
    }
}
