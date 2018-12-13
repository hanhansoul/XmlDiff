package diff;

import diff.simple.SimpleOperationValue;
import diff.simple.SimpleTree;
import org.dom4j.DocumentException;

import java.util.Iterator;

import static diff.simple.SimpleOperationValue.opValue;

public class XmlDiff {

    private SimpleTree leftTree;
    private SimpleTree rightTree;
    private OperationValue[][] permanentArr;
    private OperationValue[][] temporaryArr;

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new SimpleTree(leftFileName);
        rightTree = new SimpleTree(rightFileName);
        permanentArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr[0][0] = new SimpleOperationValue();
    }

    private void compute(int left, int right) throws OpValueElementNullException {
        System.out.println(left + " " + right);
//        temporaryArr[0][0] = new XmlOperationValue(0, 0, 0);
        Node leftNode = leftTree.nodeSequence[left];
        Node rightNode = rightTree.nodeSequence[right];
//        System.out.println(left + " " + right + ": ");
        for (int i = leftNode.leftMostNodeId; i <= left; i++) {
            temporaryArr[i][0] = i - 1 < leftNode.leftMostNodeId ?
                    temporaryArr[0][0].add(opValue(leftNode, null), i, 0) :
                    temporaryArr[i - 1][0].add(opValue(leftNode, null), i, 0);
//            System.out.println(i + " " + 0 + ": " + temporaryArr[i][0]);
        }
        for (int j = rightNode.leftMostNodeId; j <= right; j++) {
            temporaryArr[0][j] = j - 1 < rightNode.leftMostNodeId ?
                    temporaryArr[0][0].add(opValue(null, rightNode), 0, j) :
                    temporaryArr[0][j - 1].add(opValue(null, rightNode), 0, j);
//            System.out.println(0 + " " + j + ": " + temporaryArr[0][j]);
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
                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j),      // 删除节点i
                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j),      // 增加节点j
                            temporaryArr[ix][jx].add(opValue(leftChildNode, rightChildNode), i, j));    // 将节点i修改为节点j
                    System.out.println("1: " + i + " " + j + " -> " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevX + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevY + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).operation.op + " -> " +
                            ((SimpleOperationValue) temporaryArr[i][j]).value);
                } else {
                    int ix = checkIndexMargin(i, leftNode);
                    int jx = checkIndexMargin(j, rightNode);
                    Node leftChildNode = leftTree.nodeSequence[i];
                    Node rightChildNode = rightTree.nodeSequence[j];
                    int iy = checkNodeIndexMargin(leftChildNode, leftNode);
                    int jy = checkNodeIndexMargin(rightChildNode, rightNode);
                    temporaryArr[i][j] = XmlDiffHelper.min(
                            temporaryArr[ix][j].add(opValue(leftChildNode, null), i, j),
                            temporaryArr[i][jx].add(opValue(null, rightChildNode), i, j),
                            temporaryArr[iy][jy].add(permanentArr[i][j], i, j));
                    System.out.println("2: " + i + " " + j + " -> " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevX + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).prevY + " " +
                            ((SimpleOperationValue) temporaryArr[i][j]).operation.op + " -> " +
                            ((SimpleOperationValue) temporaryArr[i][j]).value);
                }
            }
        }
        System.out.println();
    }

    private int checkIndexMargin(int index, Node ancestorNode) {
        return index - 1 < ancestorNode.leftMostNodeId ? 0 : index - 1;
    }

    private int checkNodeIndexMargin(Node childNode, Node ancestorNode) {
        return childNode.leftMostNodeId - 1 < ancestorNode.leftMostNodeId ? 0 : childNode.leftMostNodeId - 1;
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
        System.out.println("current: " + v.curX + " " + v.curY + " " + v.value +
                ", prev: " + v.prevX + " " + v.prevY + ", op: " + v.operation.op);
        if (v.prevX == 0 && v.prevY == 0) {
            return;
        }
        if (v.operation.op == OperationEnum.INSERT) {
            rightTree.nodeSequence[v.curY].counterpartId = v.curX;
            rightTree.nodeSequence[v.curY].op = OperationEnum.INSERT;
        } else if (v.operation.op == OperationEnum.DELETE) {
            leftTree.nodeSequence[v.curX].counterpartId = v.curY;
            leftTree.nodeSequence[v.curX].op = OperationEnum.DELETE;
        } else if (v.operation.op == OperationEnum.CHANGE) {
            leftTree.nodeSequence[v.curX].counterpartId = v.curY;
            rightTree.nodeSequence[v.curY].counterpartId = v.curX;
            leftTree.nodeSequence[v.curX].op = OperationEnum.CHANGE;
            rightTree.nodeSequence[v.curY].op = OperationEnum.CHANGE;
        }
        backtrace((SimpleOperationValue) temporaryArr[v.prevX][v.prevY]);
    }

    public StringBuilder leftOutput = new StringBuilder();
    public StringBuilder rightOutput = new StringBuilder();

    public void output() {
        int leftIndex = 1;
        int rightIndex = 1;
        while (true) {
            Node leftNode = leftTree.nodePreOrderSequence[leftIndex];
            Node rightNode = rightTree.nodePreOrderSequence[rightIndex];
            int depth = Math.max(leftNode.depth, rightNode.depth);
            if (leftNode.op == null && rightNode.op == null ||
                    leftNode.op == OperationEnum.UNCHANGE && rightNode.op == OperationEnum.UNCHANGE) {
                // TODO 正常输出leftNode和rightNode节点操作
                // ...
                leftOutput
                leftIndex++;
                rightIndex++;
            } else if (leftNode.op == OperationEnum.DELETE) {
                // TODO 输出删除leftNode节点操作
                // ...
                leftIndex++;
            } else if (rightNode.op == OperationEnum.INSERT) {
                rightIndex++;
            } else if (leftNode.op == OperationEnum.CHANGE && rightNode.op == OperationEnum.CHANGE) {
                leftIndex++;
                rightIndex++;
            }
        }
    }

    public void dfs(Node leftRoot, Node rightRoot) {

        if (leftRoot == null || rightRoot == null) {
            return;
        }

        Iterator<Node> leftChildIterator = leftRoot.children != null ? leftRoot.children.iterator() : null;
        Iterator<Node> rightChildIterator = rightRoot.children != null ? rightRoot.children.iterator() : null;

        Node leftNode = leftChildIterator.hasNext() ? leftChildIterator.next() : null;
        Node rightNode = rightChildIterator.hasNext() ? rightChildIterator.next() : null;

        while (true) {

            if (leftNode.op == null && rightNode.op == null) {
                // TODO 正常输出leftNode和rightNode节点操作
                // ...
                dfs(leftNode, rightNode);
            } else if (leftNode.op == OperationEnum.DELETE) {
                // TODO 输出删除leftNode节点操作
                // ...
                leftNode = leftChildIterator.hasNext() ? leftChildIterator.next() : null;
                dfs(leftNode, rightNode);
            } else if (rightRoot.op == OperationEnum.INSERT) {

                rightNode = rightChildIterator.hasNext() ? rightChildIterator.next() : null;
                dfs(leftNode, rightNode);
            } else if (leftRoot.op == OperationEnum.CHANGE && rightRoot.op == OperationEnum.CHANGE) {

                leftNode = leftChildIterator.hasNext() ? leftChildIterator.next() : null;
                rightNode = rightChildIterator.hasNext() ? rightChildIterator.next() : null;
                dfs(leftNode, rightNode);
            }

        }

    }

    public void diffOutput(Node leftRoot, Iterator<Node> leftIterator, Node rightRoot, Iterator<Node> rightIterator) {
//        Node leftNode = leftTree.nodeSequence[leftIndex];
//        Node rightNode = rightTree.nodeSequence[rightIndex];
//        Iterator<Node> leftIterator = leftRoot.children.iterator();
//        Iterator<Node> rightIterator = rightRoot.children.iterator();
        if (leftRoot == null || rightRoot == null) {

        }
        if (leftRoot.op == null && rightRoot.op == null) {
            // TODO 正常输出leftNode和rightNode节点操作
            // ...
            leftOutput.append(leftRoot.element.getName());
            rightOutput.append(rightRoot.element.getName());


            Node leftNode = leftIterator.hasNext() ? leftIterator.next() : null;
            Node rightNode = rightIterator.hasNext() ? rightIterator.next() : null;

            Iterator<Node> leftChildIterator = leftIterator == null && leftRoot.children != null ? leftRoot.children.iterator() : null;
            Iterator<Node> rightChildIterator = rightIterator == null && rightRoot.children != null ? rightRoot.children.iterator() : null;

            if (leftNode != null && rightNode != null) {

            }

            leftOutput.append(leftRoot.element.getName());
            rightOutput.append(rightRoot.element.getName());
        } else if (leftRoot.op == OperationEnum.DELETE) {
            // TODO 输出删除leftNode节点操作
            // ...

        } else if (rightRoot.op == OperationEnum.INSERT) {

        } else if (leftRoot.op == OperationEnum.CHANGE && rightRoot.op == OperationEnum.CHANGE) {

        }


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
