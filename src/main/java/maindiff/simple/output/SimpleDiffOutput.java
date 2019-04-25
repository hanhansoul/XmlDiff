package maindiff.simple.output;

import maindiff.abs.output.DiffOutput;
import maindiff.abs.output.OutputNode;
import maindiff.abs.work.Tree;
import maindiff.simple.work.SimpleTree;
import maindiff.util.OperationEnum;

import java.io.FileWriter;
import java.io.IOException;

public class SimpleDiffOutput extends DiffOutput {
    private SimpleTree leftTree;
    private SimpleTree rightTree;

    public SimpleDiffOutput(Tree leftTree, Tree rightTree) {
        this.leftTree = (SimpleTree) leftTree;
        this.rightTree = (SimpleTree) rightTree;
    }

    public void elementOutput(StringBuilder sb, OutputNode outputNode, OperationEnum op) {
        if (outputNode == null) {
            elementVoidOutput(sb, null, op);
            return;
        }
        if (!outputNode.isEndTag) {
            elementStartOutputWithoutAttributes(sb, outputNode.node, op);
        } else {
            elementEndOutput(sb, outputNode.node, op);
        }
    }

    public void nodesElementOutput(StringBuilder leftSb, StringBuilder rightSb,
                                   OutputNode leftOutputNode, OutputNode rightOutputNode,
                                   OperationEnum operationType) {
        elementOutput(leftOutput, leftOutputNode, operationType);
        elementOutput(rightOutput, rightOutputNode, operationType);
    }

    /**
     * public void output() {
     * List<SimpleOutputNode> leftList = leftTree.nodeOutputSequence;
     * List<SimpleOutputNode> rightList = rightTree.nodeOutputSequence;
     * <p>
     * Iterator<SimpleOutputNode> leftIterator = leftList.iterator();
     * Iterator<SimpleOutputNode> rightIterator = rightList.iterator();
     * <p>
     * SimpleOutputNode leftOutputNode = null;
     * SimpleOutputNode rightOutputNode = null;
     * Node leftNode = null;
     * Node rightNode = null;
     * boolean leftNext = true;
     * boolean rightNext = true;
     * int leftIndex = 0;
     * int rightIndex = 0;
     * int leftSize = leftList.size();
     * int rightSize = rightList.size();
     * <p>
     * while (true) {
     * if (leftIndex >= leftSize && rightIndex >= rightSize) {
     * break;
     * }
     * if (leftNext && leftIterator.hasNext()) {
     * leftOutputNode = leftIterator.next();
     * leftNode = leftOutputNode.node;
     * }
     * if (rightNext && rightIterator.hasNext()) {
     * rightOutputNode = rightIterator.next();
     * rightNode = rightOutputNode.node;
     * }
     * if ((leftNode.operationType == null || leftNode.operationType == OperationEnum.UNCHANGE) &&
     * (rightNode.operationType == null || rightNode.operationType == OperationEnum.UNCHANGE)) {
     * elementOutput(leftOutput, leftOutputNode, OperationEnum.UNCHANGE);
     * elementOutput(rightOutput, rightOutputNode, OperationEnum.UNCHANGE);
     * leftNext = rightNext = true;
     * leftIndex++;
     * rightIndex++;
     * } else if (leftNode.operationType == OperationEnum.DELETE) {
     * elementOutput(leftOutput, leftOutputNode, OperationEnum.DELETE);
     * elementOutput(rightOutput, null, OperationEnum.DELETE);
     * leftNext = true;
     * rightNext = false;
     * leftIndex++;
     * } else if (rightNode.operationType == OperationEnum.INSERT) {
     * elementOutput(leftOutput, null, OperationEnum.INSERT);
     * elementOutput(rightOutput, rightOutputNode, OperationEnum.INSERT);
     * leftNext = false;
     * rightNext = true;
     * rightIndex++;
     * } else if (leftNode.operationType == OperationEnum.CHANGE && rightNode.operationType == OperationEnum.CHANGE) {
     * elementOutput(leftOutput, leftOutputNode, OperationEnum.CHANGE);
     * elementOutput(rightOutput, rightOutputNode, OperationEnum.CHANGE);
     * leftNext = rightNext = true;
     * leftIndex++;
     * rightIndex++;
     * }
     * }
     * }
     **/

    public void resultOutput() throws IOException {
        leftOutput.append(OUTPUT_START);
        rightOutput.append(OUTPUT_START);
        output(leftTree.nodeOutputSequence, rightTree.nodeOutputSequence);
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
