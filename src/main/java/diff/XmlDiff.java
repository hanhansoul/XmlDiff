package diff;

import diff.simple.SimpleDiffOutput;
import diff.simple.SimpleOperationValue;
import diff.simple.SimpleTree;
import org.dom4j.DocumentException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import static diff.simple.SimpleOperationValue.opValue;

public class XmlDiff {

    private SimpleTree leftTree;
    private SimpleTree rightTree;
    private OperationValue[][] permanentArr;
    private OperationValue[][] temporaryArr;

    public StringBuilder leftOutput = new StringBuilder();
    public StringBuilder rightOutput = new StringBuilder();

    private final String[] TAB_STRINGS = new String[50];
    private final String OUTPUT_START = "<pre>";
    private final String OUTPUT_END = "</pre>";
    private final String DIV_START = "<span style='display:inline-block;min-width:100%;'>";
    private final String DIV_END = "</span>";
    private final String BR = "<br />";

    private final String DELETE_DIV_START = "<span style='background-color:red;display:inline-block;min-width:100%;'>";
    private final String INSERT_DIV_START = "<span style='background-color:green;display:inline-block;min-width:100%;'>";
    private final String CHANGE_DIV_START = "<span style='background-color:blue;display:inline-block;min-width:100%;'>";

    public XmlDiff() {
        TAB_STRINGS[0] = "";
        for (int i = 1; i < 50; i++) {
            TAB_STRINGS[i] = TAB_STRINGS[i - 1] + "   ";
        }
    }

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

    private void backtrace(SimpleOperationValue v) {
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

    public void preOrderOutput() {
        Node[] nodes = leftTree.nodePreOrderSequence;
        for (int i = 1; i < nodes.length; i++) {
            System.out.println(nodes[i].element.getName() + " " + nodes[i].op + " " + nodes[i].depth + " " + nodes[i].rightMostNodeId);
        }
        System.out.println();
        nodes = rightTree.nodePreOrderSequence;
        for (int i = 1; i < nodes.length; i++) {
            System.out.println(nodes[i].element.getName() + " " + nodes[i].op + " " + nodes[i].depth + " " + nodes[i].rightMostNodeId);
        }
    }

    public void resultOutput() throws IOException {
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
        dfs(1, 1);
        leftOutput.append(OUTPUT_END);
        rightOutput.append(OUTPUT_END);
        System.out.println(leftOutput);
        System.out.println();
        System.out.println(rightOutput);
        FileWriter writer = new FileWriter("data/output1.html");
        writer.write(String.valueOf(leftOutput));
        writer.flush();
        writer.close();
        writer = new FileWriter("data/output2.html");
        writer.write(String.valueOf(rightOutput));
        writer.flush();
        writer.close();
    }

    public void dfs(int leftIndex, int rightIndex) {
        Node leftNode = leftIndex < leftTree.nodePreOrderSequence.length ?
                leftTree.nodePreOrderSequence[leftIndex] : null;
        Node rightNode = rightIndex < rightTree.nodePreOrderSequence.length ?
                rightTree.nodePreOrderSequence[rightIndex] : null;
        int depth;

        if (leftNode == null && rightNode == null) {
            return;
        } else if (leftNode == null) {
            depth = leftNode.depth;
            leftOutput.append(DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex);
            leftOutput.append(DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
        } else if (rightNode == null) {
            depth = rightNode.depth;
            rightOutput.append(DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex, rightIndex + 1);
            rightOutput.append(DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        }

        depth = Math.max(leftNode.depth, rightNode.depth);
        if (leftNode.op == null && rightNode.op == null ||
                leftNode.op == OperationEnum.UNCHANGE && rightNode.op == OperationEnum.UNCHANGE) {
            leftOutput.append(DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex + 1);
            leftOutput.append(DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        } else if (leftNode.op == OperationEnum.DELETE) {
            leftOutput.append(DELETE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DELETE_DIV_START + "  " + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex);
            leftOutput.append(DELETE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DELETE_DIV_START + "  " + DIV_END + BR);
        } else if (rightNode.op == OperationEnum.INSERT) {
            leftOutput.append(INSERT_DIV_START + "  " + DIV_END + BR);
            rightOutput.append(INSERT_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex, rightIndex + 1);
            leftOutput.append(INSERT_DIV_START + "  " + DIV_END + BR);
            rightOutput.append(INSERT_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        } else if (leftNode.op == OperationEnum.CHANGE && rightNode.op == OperationEnum.CHANGE) {
            leftOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex + 1);
            leftOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        }
    }

    public static void main(String[] args) throws DocumentException, OpValueElementNullException, IOException {
        long beginTime = System.currentTimeMillis();
        XmlDiff xmlDiff = new XmlDiff();
//        xmlDiff.initialization("data/CSCA350-353000-00M01-01-X_2_20180901.xml",
//                "data/CSCA350-353000-00M01-01-X_3_20181001.xml");
        xmlDiff.initialization("data/left.xml", "data/right.xml");
        xmlDiff.solve();
        xmlDiff.preOrderOutput();
        new SimpleDiffOutput(xmlDiff.leftTree, xmlDiff.rightTree).resultOutput();
//        System.out.println(xmlDiff.leftOutput);
//        System.out.println(xmlDiff.rightOutput);
//        xmlDiff.resultOutput();
        long endTime = System.currentTimeMillis();
//        System.out.println((endTime - beginTime) / 1000);
    }
}
