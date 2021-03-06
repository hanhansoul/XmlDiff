package diff.simple;

import diff.Node;
import diff.OperationEnum;
import org.dom4j.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static diff.Constant.DEBUG;

public class SimpleDiffOutput {
    private SimpleTree leftTree;
    private SimpleTree rightTree;

    public StringBuilder leftOutput = new StringBuilder();
    public StringBuilder rightOutput = new StringBuilder();

    private final String[] INDENTS = new String[50];
    private final String OUTPUT_START = "<pre>";
    private final String OUTPUT_END = "</pre>";
    private final String SPAN_START = "<span style='display:inline-block;min-width:100%;'>";
    private final String SPAN_END = "</span>";
    private final String BR = "<br />";

    private final String[] OP_SPAN_START = {
            "<span style='background-color:green;display:inline-block;min-width:100%;'>",
            "<span style='background-color:red;display:inline-block;min-width:100%;'>",
            "<span style='background-color:blue;display:inline-block;min-width:100%;'>",
            "<span style='display:inline-block;min-width:100%;'>"
    };
    private final String DELETE_SPAN_START = "<span style='background-color:red;display:inline-block;min-width:100%;'>";
    private final String INSERT_SPAN_START = "<span style='background-color:green;display:inline-block;min-width:100%;'>";
    private final String CHANGE_SPAN_START = "<span style='background-color:blue;display:inline-block;min-width:100%;'>";

    public SimpleDiffOutput(SimpleTree leftTree, SimpleTree rightTree) {
        this.leftTree = leftTree;
        this.rightTree = rightTree;

        INDENTS[0] = "";
        for (int i = 1; i < 50; i++) {
            INDENTS[i] = INDENTS[i - 1] + "   ";
        }
    }

    private void elementStartOutput(StringBuilder sb, Node node, OperationEnum op) {
        int opIndex = op != null ? op.ordinal() : 3;
        if (node == null) {
            sb.append(BR);
            sb.append(OP_SPAN_START[opIndex]);
            sb.append(" ");
            sb.append(SPAN_END);
        } else {
            int depth = node.depth;
            Element element = node.element;
            sb.append(BR);
            sb.append(OP_SPAN_START[opIndex]);
            sb.append(INDENTS[depth]);
            sb.append("&lt;");
            sb.append(element.getQualifiedName());
            if (node.attributesMap != null) {
                for (Map.Entry<String, String> entry : node.attributesMap.entrySet()) {
                    sb.append(" ");
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(entry.getValue());
                }
            }
            sb.append("&gt;");
//            if (node.element.nodeCount() <= 0) {
//                sb.append("/&gt;");
//            } else {
//                sb.append("&gt;");
//            }
            sb.append(SPAN_END);
        }
    }

    private void elementEndOutput(StringBuilder sb, Node node, OperationEnum op) {
        int opIndex = op != null ? op.ordinal() : 3;
        if (node == null) {
            sb.append(BR);
            sb.append(OP_SPAN_START[opIndex]);
            sb.append(" ");
            sb.append(SPAN_END);
//        } else if (node.element.nodeCount() > 0) {
        } else {
            int depth = node.depth;
            Element element = node.element;
            sb.append(BR);
            sb.append(OP_SPAN_START[opIndex]);
            sb.append(INDENTS[depth]);
            sb.append("&lt;/");
            sb.append(element.getQualifiedName());
            sb.append("&gt;");
            sb.append(SPAN_END);
        }
    }


    private void outputNodeOutputSequence() {
        StringBuilder sb = new StringBuilder();
        for (SimpleOutputNode node : rightTree.nodeOutputSequence) {
            elementOutput(sb, node, OperationEnum.UNCHANGE);
        }
        System.out.println(sb);
    }

    private void elementTextOutput(StringBuilder sb, Node node, OperationEnum op) {
        int opIndex = op != null ? op.ordinal() : 3;
        int depth = node.depth;
        sb.append(BR);
        sb.append(OP_SPAN_START[opIndex]);
        sb.append(INDENTS[depth + 1]);
        for (String text : node.text) {
            sb.append(text);
            sb.append(" ");
        }
        sb.append(SPAN_END);
    }

    private int auxIndex;
    private boolean isLeftMain;

    /**
     * error
     *
     * @param sb
     * @param mainNode
     * @param st
     */
    private void outputDfs(StringBuilder sb, Node mainNode, SimpleTree st) {
        Node auxNode = st.nodePreOrderSequence[auxIndex];

        if (mainNode.op == null && auxNode.op == null ||
                mainNode.op == OperationEnum.UNCHANGE && auxNode.op == OperationEnum.UNCHANGE) {
            elementStartOutput(sb, mainNode, OperationEnum.UNCHANGE);
            if (mainNode.children != null) {
                for (Node node : mainNode.children) {
                    ++auxIndex;
                    outputDfs(sb, node, st);
                }
            }
            elementEndOutput(sb, mainNode, OperationEnum.UNCHANGE);
        } else if (isLeftMain && mainNode.op == OperationEnum.DELETE || !isLeftMain && auxNode.op == OperationEnum.DELETE) {
//        } else if (isLeftMain && mainNode.operationType == OperationEnum.DELETE) {
            elementStartOutput(sb, mainNode, OperationEnum.DELETE);
            if (mainNode.children != null) {
                for (Node node : mainNode.children) {
                    outputDfs(sb, node, st);
                }
            }
            elementEndOutput(sb, mainNode, OperationEnum.DELETE);
        } else if (isLeftMain && auxNode.op == OperationEnum.INSERT || !isLeftMain && mainNode.op == OperationEnum.INSERT) {
//        } else if (isLeftMain && auxNode.operationType == OperationEnum.INSERT) {
            elementStartOutput(sb, null, OperationEnum.INSERT);
            ++auxIndex;
            outputDfs(sb, mainNode, st);
            elementEndOutput(sb, null, OperationEnum.INSERT);
        } else if (mainNode.op == OperationEnum.CHANGE && auxNode.op == OperationEnum.CHANGE) {
            elementStartOutput(sb, mainNode, OperationEnum.CHANGE);
            if (mainNode.children != null) {
                for (Node node : mainNode.children) {
                    ++auxIndex;
                    outputDfs(sb, node, st);
                }
            }
            elementEndOutput(sb, mainNode, OperationEnum.CHANGE);
        }

    }

    /**
     * error
     *
     * @param leftRoot
     * @param leftIterator
     * @param rightRoot
     * @param rightIterator
     */
    private void outputDfs(Node leftRoot, Iterator<Node> leftIterator, Node rightRoot, Iterator<Node> rightIterator) {

        if (leftRoot.op == null && rightRoot.op == null ||
                leftRoot.op == OperationEnum.UNCHANGE && rightRoot.op == OperationEnum.UNCHANGE) {
            elementStartOutput(leftOutput, leftRoot, OperationEnum.UNCHANGE);
            elementStartOutput(rightOutput, rightRoot, OperationEnum.UNCHANGE);
//            outputDfs(leftIndex + 1, leftRoot, rightIndex + 1, rightRoot);
            elementEndOutput(leftOutput, leftRoot, OperationEnum.UNCHANGE);
            elementEndOutput(rightOutput, rightRoot, OperationEnum.UNCHANGE);
        } else if (leftRoot.op == OperationEnum.DELETE) {
            elementStartOutput(leftOutput, leftRoot, OperationEnum.DELETE);
            elementStartOutput(rightOutput, null, OperationEnum.DELETE);
//            outputDfs(leftIndex + 1, leftRoot, rightIndex, leftRoot);
            elementEndOutput(leftOutput, leftRoot, OperationEnum.DELETE);
            elementEndOutput(rightOutput, null, OperationEnum.DELETE);
        } else if (rightRoot.op == OperationEnum.INSERT) {
            elementStartOutput(leftOutput, null, OperationEnum.INSERT);
            elementStartOutput(rightOutput, rightRoot, OperationEnum.INSERT);
//            outputDfs(leftIndex, leftRoot, rightIndex + 1, rightRoot);
            elementEndOutput(leftOutput, null, OperationEnum.INSERT);
            elementEndOutput(rightOutput, rightRoot, OperationEnum.INSERT);
        } else if (leftRoot.op == OperationEnum.CHANGE && rightRoot.op == OperationEnum.CHANGE) {
            elementStartOutput(leftOutput, leftRoot, OperationEnum.CHANGE);
            elementStartOutput(rightOutput, rightRoot, OperationEnum.CHANGE);
//            outputDfs(leftIndex + 1, leftRoot, rightIndex + 1, rightRoot);
            elementEndOutput(leftOutput, leftRoot, OperationEnum.CHANGE);
            elementEndOutput(rightOutput, rightRoot, OperationEnum.CHANGE);
        }

    }

    /**
     * error
     *
     * @param leftIndex
     * @param leftRoot
     * @param rightIndex
     * @param rightRoot
     */
    private void outputDfs(int leftIndex, Node leftRoot, int rightIndex, Node rightRoot) {
        Node leftNode = leftIndex < leftTree.nodePreOrderSequence.length ?
                leftTree.nodePreOrderSequence[leftIndex] : null;
        Node rightNode = rightIndex < rightTree.nodePreOrderSequence.length ?
                rightTree.nodePreOrderSequence[rightIndex] : null;

        if (leftNode == null && rightNode == null) {
            return;
        } else if (leftNode == null) {
            elementStartOutput(leftOutput, leftNode, OperationEnum.UNCHANGE);
            outputDfs(leftIndex + 1, leftNode, rightIndex, rightNode);
            elementEndOutput(leftOutput, leftNode, OperationEnum.UNCHANGE);
            return;
        } else if (rightNode == null) {
            elementStartOutput(rightOutput, rightNode, OperationEnum.UNCHANGE);
            outputDfs(leftIndex, leftNode, rightIndex + 1, rightNode);
            elementEndOutput(rightOutput, rightNode, OperationEnum.UNCHANGE);
            return;
        }

        if (leftNode.op == null && rightNode.op == null ||
                leftNode.op == OperationEnum.UNCHANGE && rightNode.op == OperationEnum.UNCHANGE) {
            elementStartOutput(leftOutput, leftNode, OperationEnum.UNCHANGE);
            elementStartOutput(rightOutput, rightNode, OperationEnum.UNCHANGE);
            outputDfs(leftIndex + 1, leftNode, rightIndex + 1, rightNode);
            elementEndOutput(leftOutput, leftNode, OperationEnum.UNCHANGE);
            elementEndOutput(rightOutput, rightNode, OperationEnum.UNCHANGE);
        } else if (leftNode.op == OperationEnum.DELETE) {
            elementStartOutput(leftOutput, leftNode, OperationEnum.DELETE);
            elementStartOutput(rightOutput, null, OperationEnum.DELETE);
            outputDfs(leftIndex + 1, leftNode, rightIndex, leftNode);
            elementEndOutput(leftOutput, leftNode, OperationEnum.DELETE);
            elementEndOutput(rightOutput, null, OperationEnum.DELETE);
        } else if (rightNode.op == OperationEnum.INSERT) {
            elementStartOutput(leftOutput, null, OperationEnum.INSERT);
            elementStartOutput(rightOutput, rightNode, OperationEnum.INSERT);
            outputDfs(leftIndex, leftNode, rightIndex + 1, rightNode);
            elementEndOutput(leftOutput, null, OperationEnum.INSERT);
            elementEndOutput(rightOutput, rightNode, OperationEnum.INSERT);
        } else if (leftNode.op == OperationEnum.CHANGE && rightNode.op == OperationEnum.CHANGE) {
            elementStartOutput(leftOutput, leftNode, OperationEnum.CHANGE);
            elementStartOutput(rightOutput, rightNode, OperationEnum.CHANGE);
            outputDfs(leftIndex + 1, leftNode, rightIndex + 1, rightNode);
            elementEndOutput(leftOutput, leftNode, OperationEnum.CHANGE);
            elementEndOutput(rightOutput, rightNode, OperationEnum.CHANGE);
        }
    }

    private void elementOutput(StringBuilder sb, SimpleOutputNode outputNode, OperationEnum op) {
        if (outputNode == null) {
            elementStartOutput(sb, null, op);
            return;
        }
        if (!outputNode.isEndTag) {
            elementStartOutput(sb, outputNode.node, op);
        } else {
            elementEndOutput(sb, outputNode.node, op);
        }
    }

    private void output() {
        List<SimpleOutputNode> leftList = leftTree.nodeOutputSequence;
        List<SimpleOutputNode> rightList = rightTree.nodeOutputSequence;

        Iterator<SimpleOutputNode> leftIterator = leftList.iterator();
        Iterator<SimpleOutputNode> rightIterator = rightList.iterator();

        SimpleOutputNode leftOutputNode = null;
        SimpleOutputNode rightOutputNode = null;
        Node leftNode = null;
        Node rightNode = null;
        boolean leftNext = true;
        boolean rightNext = true;
        int leftIndex = 0;
        int rightIndex = 0;
        int leftSize = leftList.size();
        int rightSize = rightList.size();

        while (true) {
            if (leftIndex >= leftSize && rightIndex >= rightSize) {
                break;
            }
            if (leftNext && leftIterator.hasNext()) {
                leftOutputNode = leftIterator.next();
                leftNode = leftOutputNode.node;
            }
            if (rightNext && rightIterator.hasNext()) {
                rightOutputNode = rightIterator.next();
                rightNode = rightOutputNode.node;
            }
            if ((leftNode.op == null || leftNode.op == OperationEnum.UNCHANGE) &&
                    (rightNode.op == null || rightNode.op == OperationEnum.UNCHANGE)) {
                elementOutput(leftOutput, leftOutputNode, OperationEnum.UNCHANGE);
                elementOutput(rightOutput, rightOutputNode, OperationEnum.UNCHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            } else if (leftNode.op == OperationEnum.DELETE) {
                elementOutput(leftOutput, leftOutputNode, OperationEnum.DELETE);
                elementOutput(rightOutput, null, OperationEnum.DELETE);
                leftNext = true;
                rightNext = false;
                leftIndex++;
            } else if (rightNode.op == OperationEnum.INSERT) {
                elementOutput(leftOutput, null, OperationEnum.INSERT);
                elementOutput(rightOutput, rightOutputNode, OperationEnum.INSERT);
                leftNext = false;
                rightNext = true;
                rightIndex++;
            } else if (leftNode.op == OperationEnum.CHANGE && rightNode.op == OperationEnum.CHANGE) {
                elementOutput(leftOutput, leftOutputNode, OperationEnum.CHANGE);
                elementOutput(rightOutput, rightOutputNode, OperationEnum.CHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            }
        }
    }

    public void resultOutput() throws IOException {
        if (DEBUG) {
            outputNodeOutputSequence();
        }
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
//        auxIndex = 1;
//        isLeftMain = true;
//        outputDfs(leftOutput, leftTree.rootNode, rightTree);
//        auxIndex = 1;
//        isLeftMain = false;
//        outputDfs(rightOutput, rightTree.rootNode, leftTree);
        output();
        leftOutput.append(OUTPUT_END);
        rightOutput.append(OUTPUT_END);
        if (DEBUG) {
            System.out.println(leftOutput);
            System.out.println();
            System.out.println(rightOutput);
        }
        FileWriter writer = new FileWriter("data/output1.html");
        writer.write(String.valueOf(leftOutput));
        writer.flush();
        writer.close();
        writer = new FileWriter("data/output2.html");
        writer.write(String.valueOf(rightOutput));
        writer.flush();
        writer.close();
    }

    /*
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

    public void preOrderOutput() {
        Node[] nodes = leftTree.nodePreOrderSequence;
        for (int i = 1; i < nodes.length; i++) {
            System.out.println(nodes[i].element.getName() + " " + nodes[i].operationType + " " + nodes[i].depth + " " + nodes[i].rightMostNodeId);
        }
        System.out.println();
        nodes = rightTree.nodePreOrderSequence;
        for (int i = 1; i < nodes.length; i++) {
            System.out.println(nodes[i].element.getName() + " " + nodes[i].operationType + " " + nodes[i].depth + " " + nodes[i].rightMostNodeId);
        }
    }

    public void resultOutput() throws IOException {
        TAB_STRINGS[0] = "";
        for (int i = 1; i < 50; i++) {
            TAB_STRINGS[i] = TAB_STRINGS[i - 1] + "   ";
        }
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
        if (leftNode.operationType == null && rightNode.operationType == null ||
                leftNode.operationType == OperationEnum.UNCHANGE && rightNode.operationType == OperationEnum.UNCHANGE) {
            leftOutput.append(DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex + 1);
            leftOutput.append(DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        } else if (leftNode.operationType == OperationEnum.DELETE) {
            leftOutput.append(DELETE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DELETE_DIV_START + "  " + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex);
            leftOutput.append(DELETE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(DELETE_DIV_START + "  " + DIV_END + BR);
        } else if (rightNode.operationType == OperationEnum.INSERT) {
            leftOutput.append(INSERT_DIV_START + "  " + DIV_END + BR);
            rightOutput.append(INSERT_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex, rightIndex + 1);
            leftOutput.append(INSERT_DIV_START + "  " + DIV_END + BR);
            rightOutput.append(INSERT_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        } else if (leftNode.operationType == OperationEnum.CHANGE && rightNode.operationType == OperationEnum.CHANGE) {
            leftOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
            dfs(leftIndex + 1, rightIndex + 1);
            leftOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + leftNode.element.getName() + DIV_END + BR);
            rightOutput.append(CHANGE_DIV_START + TAB_STRINGS[depth] + rightNode.element.getName() + DIV_END + BR);
        }
    }

    */
}
