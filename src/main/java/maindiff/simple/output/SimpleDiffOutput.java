package maindiff.simple.output;

import maindiff.abs.output.DiffOutput;
import maindiff.abs.work.Node;
import maindiff.abs.work.Tree;
import maindiff.simple.work.SimpleTree;
import maindiff.util.OperationEnum;
import org.dom4j.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static maindiff.util.Constant.DEBUG;

public class SimpleDiffOutput extends DiffOutput {
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

    public SimpleDiffOutput(Tree leftTree, Tree rightTree) {
        this.leftTree = (SimpleTree) leftTree;
        this.rightTree = (SimpleTree) rightTree;

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
            if ((leftNode.operationType == null || leftNode.operationType == OperationEnum.UNCHANGE) &&
                    (rightNode.operationType == null || rightNode.operationType == OperationEnum.UNCHANGE)) {
                elementOutput(leftOutput, leftOutputNode, OperationEnum.UNCHANGE);
                elementOutput(rightOutput, rightOutputNode, OperationEnum.UNCHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            } else if (leftNode.operationType == OperationEnum.DELETE) {
                elementOutput(leftOutput, leftOutputNode, OperationEnum.DELETE);
                elementOutput(rightOutput, null, OperationEnum.DELETE);
                leftNext = true;
                rightNext = false;
                leftIndex++;
            } else if (rightNode.operationType == OperationEnum.INSERT) {
                elementOutput(leftOutput, null, OperationEnum.INSERT);
                elementOutput(rightOutput, rightOutputNode, OperationEnum.INSERT);
                leftNext = false;
                rightNext = true;
                rightIndex++;
            } else if (leftNode.operationType == OperationEnum.CHANGE && rightNode.operationType == OperationEnum.CHANGE) {
                elementOutput(leftOutput, leftOutputNode, OperationEnum.CHANGE);
                elementOutput(rightOutput, rightOutputNode, OperationEnum.CHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            }
        }
    }

    public void resultOutput() throws IOException {
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
        output();
        leftOutput.append(OUTPUT_END);
        rightOutput.append(OUTPUT_END);
        FileWriter writer = new FileWriter("data/output10.html");
        writer.write(String.valueOf(leftOutput));
        writer.flush();
        writer.close();
        writer = new FileWriter("data/output20.html");
        writer.write(String.valueOf(rightOutput));
        writer.flush();
        writer.close();
    }

}
