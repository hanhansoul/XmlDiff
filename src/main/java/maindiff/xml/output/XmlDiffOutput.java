package maindiff.xml.output;

import maindiff.abs.output.DiffOutput;
import maindiff.abs.output.OutputNode;
import maindiff.abs.work.Node;
import maindiff.abs.work.Tree;
import maindiff.util.OperationEnum;
import maindiff.xml.textdiff.TextDiff;
import maindiff.xml.work.XmlNode;
import maindiff.xml.work.XmlTree;

import java.io.FileWriter;
import java.io.IOException;

public class XmlDiffOutput extends DiffOutput {
    private XmlTree leftTree;
    private XmlTree rightTree;

    public XmlDiffOutput(Tree leftTree, Tree rightTree) {
        this.leftTree = (XmlTree) leftTree;
        this.rightTree = (XmlTree) rightTree;
    }

    public void elementTextOutput(StringBuilder sb, Node node, StringBuilder text, OperationEnum operationType) {

        int depth = node.depth;
        if (text == null || text.length() == 0) {
            return;
        }
        if (operationType == OperationEnum.INSERT || operationType == OperationEnum.DELETE) {
            int opIndex = operationType.ordinal();
            sb.append(BR);
            sb.append(OP_SPAN_START[opIndex]);
            sb.append(INDENTS[depth]);
            sb.append(text);
            sb.append(SPAN_END);
        } else if (operationType == OperationEnum.CHANGE) {
            sb.append(BR);
            sb.append(SPAN_START);
            sb.append(INDENTS[depth]);
            sb.append(text);
            sb.append(SPAN_END);
        }
    }

    public void elementOutput(StringBuilder sb, OutputNode outputNode, StringBuilder text, OperationEnum op) {
        if (outputNode == null) {
            elementVoidOutput(sb, null, op);
            return;
        }
        if (!outputNode.isEndTag) {
            elementStartOutputWithoutAttributes(sb, outputNode.node, op);
            elementTextOutput(sb, outputNode.node, text, op);
        } else {
            elementEndOutput(sb, outputNode.node, op);
        }
    }

    public void nodesElementOutput(StringBuilder leftSb, StringBuilder rightSb,
                                   OutputNode leftOutputNode, OutputNode rightOutputNode,
                                   OperationEnum operationType) {
        XmlNode leftNode = null;
        XmlNode rightNode = null;
        if (leftOutputNode != null) {
            leftNode = (XmlNode) leftOutputNode.node;
        }
        if (rightOutputNode != null) {
            rightNode = (XmlNode) rightOutputNode.node;
        }

        StringBuilder textOutputLeft = null;
        StringBuilder textOutputRight = null;
        if (operationType == OperationEnum.CHANGE && leftNode != null && rightNode != null &&
                leftNode.tagName.equals(rightNode.tagName)) {
            textOutputLeft = new StringBuilder();
            textOutputRight = new StringBuilder();
            TextDiff.textDiffTextOutput(leftNode.textArr, rightNode.textArr, textOutputLeft, textOutputRight);
        } else if (operationType == OperationEnum.DELETE && leftNode != null) {
            textOutputLeft = new StringBuilder(leftNode.text);
        } else if (operationType == OperationEnum.INSERT && rightNode != null) {
            textOutputRight = new StringBuilder(rightNode.text);
        } else if (leftNode != null && rightNode != null) {
            textOutputLeft = new StringBuilder(leftNode.text);
            textOutputRight = new StringBuilder(rightNode.text);
        }
        elementOutput(leftOutput, leftOutputNode, textOutputLeft, operationType);
        elementOutput(rightOutput, rightOutputNode, textOutputRight, operationType);
    }

    public void resultOutput() throws IOException {
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
        output(leftTree.nodeOutputSequence, rightTree.nodeOutputSequence);
        leftOutput.append(OUTPUT_END);
        rightOutput.append(OUTPUT_END);
        FileWriter writer = new FileWriter("data/output11.html");
        writer.write(String.valueOf(leftOutput));
        writer.flush();
        writer.close();
        writer = new FileWriter("data/output22.html");
        writer.write(String.valueOf(rightOutput));
        writer.flush();
        writer.close();
    }
}
