package newdiff.abs.output;

import newdiff.abs.work.Node;
import newdiff.abs.work.OperationMoveType;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/12/21 0021.
 */
public abstract class DiffOutput {
    public StringBuilder leftOutput;
    public StringBuilder rightOutput;

    public String[] INDENTS = new String[50];
    public String OUTPUT_START = "<pre>";
    public String OUTPUT_END = "</pre>";
    public String SPAN_START = "<span style='display:inline-block;min-width:100%;'>";
    public String SPAN_END = "</span>";
    public String BR = "<br />";

    public String[] OP_SPAN_START = {
            "<span style='background-color:green;display:inline-block;min-width:100%;'>",
            "<span style='background-color:red;display:inline-block;min-width:100%;'>",
            "<span style='background-color:blue;display:inline-block;min-width:100%;'>",
            "<span style='display:inline-block;min-width:100%;'>"
    };

    public DiffOutput() {
        leftOutput = new StringBuilder();
        rightOutput = new StringBuilder();

        INDENTS[0] = "";
        for (int i = 1; i < 50; i++) {
            INDENTS[i] = INDENTS[i - 1] + "   ";
        }
    }

    public void elementTextOutput(StringBuilder sb, Node node,
                                  StringBuilder text, OperationMoveType operationMoveType) {
    }

    public void elementAttributeOutput(StringBuilder sb, Node node, OperationMoveType operationMoveType) {
    }

    public void elementVoidOutput(StringBuilder sb, Node node, OperationMoveType op) {
        int opIndex = op != null ? op.ordinal() : 3;
        sb.append(BR);
        sb.append(OP_SPAN_START[opIndex]);
        sb.append(" ");
        sb.append(SPAN_END);
    }

    public void elementStartOutputWithoutAttributes(StringBuilder sb, Node node, OperationMoveType op) {
        int opIndex = op != null ? op.ordinal() : 3;
        int depth = node.depth;
        Element element = node.element;
        sb.append(BR);
        sb.append(OP_SPAN_START[opIndex]);
        sb.append(INDENTS[depth]);
        sb.append("&lt;");
        sb.append(element.getQualifiedName());
        sb.append("&gt;");
        sb.append(SPAN_END);

    }

    public void elementEndOutput(StringBuilder sb, Node node, OperationMoveType op) {
        int opIndex = op != null ? op.ordinal() : 3;
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

    public void nodesElementOutput(StringBuilder leftSb, StringBuilder rightSb,
                                   OutputNode leftOutputNode, OutputNode rightOutputNode,
                                   OperationMoveType operationMoveType) {
    }

    public void output(List<? extends OutputNode> leftList, List<? extends OutputNode> rightList) {
        Iterator<? extends OutputNode> leftIterator = leftList.iterator();
        Iterator<? extends OutputNode> rightIterator = rightList.iterator();

        OutputNode leftOutputNode = null;
        OutputNode rightOutputNode = null;
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
            if ((leftNode.operationMoveType == null || leftNode.operationMoveType == OperationMoveType.UNCHANGE) &&
                    (rightNode.operationMoveType == null || rightNode.operationMoveType == OperationMoveType.UNCHANGE)) {
                nodesElementOutput(leftOutput, rightOutput, leftOutputNode, rightOutputNode, OperationMoveType.UNCHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            } else if (leftNode.operationMoveType == OperationMoveType.DELETE) {
                nodesElementOutput(leftOutput, rightOutput, leftOutputNode, null, OperationMoveType.DELETE);
                leftNext = true;
                rightNext = false;
                leftIndex++;
            } else if (rightNode.operationMoveType == OperationMoveType.INSERT) {
                nodesElementOutput(leftOutput, rightOutput, null, rightOutputNode, OperationMoveType.INSERT);
                leftNext = false;
                rightNext = true;
                rightIndex++;
            } else if (leftNode.operationMoveType == OperationMoveType.CHANGE &&
                    rightNode.operationMoveType == OperationMoveType.CHANGE) {
                nodesElementOutput(leftOutput, rightOutput, leftOutputNode, rightOutputNode, OperationMoveType.CHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            }
        }
    }

}
