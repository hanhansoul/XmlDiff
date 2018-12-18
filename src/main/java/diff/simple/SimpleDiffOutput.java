package diff.simple;

import diff.Node;
import diff.OperationEnum;
import org.dom4j.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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
            if (node.element.nodeCount() <= 0) {
                sb.append("/&gt;");
            } else {
                sb.append("&gt;");
            }
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
        } else if (node.element.nodeCount() > 0) {
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

    private void outputDfs(StringBuilder sb, Node mainNode, Iterator<Node> auxIterator) {
        Node auxNode = auxIterator.next();
        if (mainNode.op == null && auxNode.op == null ||
                mainNode.op == OperationEnum.UNCHANGE && auxNode.op == OperationEnum.UNCHANGE) {
            elementStartOutput(sb, mainNode, OperationEnum.UNCHANGE);
            for (Node child : mainNode.children) {
                outputDfs(sb, child, auxIterator);
            }
            elementEndOutput(sb, mainNode, OperationEnum.UNCHANGE);
        } else if (mainNode.op == OperationEnum.DELETE) {
            elementStartOutput(sb, null, OperationEnum.DELETE);
            for (Node child : mainNode.children) {
                outputDfs(sb, child, auxIterator);
            }
            elementEndOutput(sb, null, OperationEnum.DELETE);
        } else if (auxNode.op == OperationEnum.INSERT) {
            elementStartOutput(sb, auxNode, OperationEnum.INSERT);
            for (Node child : mainNode.children) {
                outputDfs(sb, child, auxIterator);
            }
//            outputDfs(leftIndex, mainRoot, rightIndex + 1, auxNode);
            elementEndOutput(sb, auxNode, OperationEnum.INSERT);
        } else if (mainNode.op == OperationEnum.CHANGE && auxNode.op == OperationEnum.CHANGE) {
            elementStartOutput(sb, auxNode, OperationEnum.CHANGE);
            for (Node child : mainNode.children) {
                outputDfs(sb, child, auxIterator);
            }
//            outputDfs(leftIndex + 1, mainRoot, rightIndex + 1, auxNode);
            elementEndOutput(sb, auxNode, OperationEnum.CHANGE);
        }


    }

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

    public void resultOutput() throws IOException {
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
        outputDfs(1, leftTree.rootNode, 1, rightTree.rootNode);
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

}
