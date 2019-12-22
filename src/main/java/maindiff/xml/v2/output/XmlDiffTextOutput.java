package maindiff.xml.v2.output;

import maindiff.abs.v2.output.DiffOutput;
import maindiff.abs.output.OutputNode;
import maindiff.abs.work.Node;
import maindiff.abs.work.Tree;
import maindiff.util.OperationEnum;
import maindiff.xml.v2.textdiff.TextDiff;
import maindiff.xml.v2.work.XmlNode;
import maindiff.xml.v2.work.XmlTree;
import org.dom4j.Element;

import java.io.FileWriter;
import java.io.IOException;

public class XmlDiffTextOutput extends DiffOutput {
    private XmlTree leftTree;
    private XmlTree rightTree;

    public final String[] OP_SPAN_START = {
            "<span style='background-color:green;display:inline-block;'>",
            "<span style='background-color:red;display:inline-block;'>",
            "<span style='background-color:blue;display:inline-block;'>",
            "<span style='display:inline-block;'>"
    };
    public String SPAN_START = "<span style='display:inline-block;'>";

    public XmlDiffTextOutput(Tree leftTree, Tree rightTree) {
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
            sb.append(INDENTS[depth]);
            sb.append(OP_SPAN_START[opIndex]);
            sb.append(text);
            sb.append(SPAN_END);
        } else if (operationType == OperationEnum.CHANGE || operationType == OperationEnum.UNCHANGE) {
            sb.append(BR);
            sb.append(INDENTS[depth]);
            sb.append(SPAN_START);
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

    public void elementStartOutputWithoutAttributes(StringBuilder sb, Node node, OperationEnum op) {
        int opIndex = op != null ? op.ordinal() : 3;
        int depth = node.depth;
        Element element = node.element;
        sb.append(BR);
        sb.append(INDENTS[depth]);
        sb.append(OP_SPAN_START[opIndex]);
        sb.append("&lt;");
        sb.append(element.getQualifiedName());
        sb.append("&gt;");
        sb.append(SPAN_END);
    }

    public void elementEndOutput(StringBuilder sb, Node node, OperationEnum op) {
        int opIndex = op != null ? op.ordinal() : 3;
        int depth = node.depth;
        Element element = node.element;
        sb.append(BR);
        sb.append(INDENTS[depth]);
        sb.append(OP_SPAN_START[opIndex]);
        sb.append("&lt;/");
        sb.append(element.getQualifiedName());
        sb.append("&gt;");
        sb.append(SPAN_END);
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

    public void resultOutput(String leftFileName, String rightFileName) throws IOException {
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
        output(leftTree.nodeOutputSequence, rightTree.nodeOutputSequence);
        leftOutput.append(OUTPUT_END);
        rightOutput.append(OUTPUT_END);
        FileWriter writer = new FileWriter(leftFileName);
        writer.write(String.valueOf(leftOutput));
        writer.flush();
        writer.close();
        writer = new FileWriter(rightFileName);
        writer.write(String.valueOf(rightOutput));
        writer.flush();
        writer.close();
    }
}
