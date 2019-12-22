package maindiff.xml.v2.output;

import maindiff.abs.work.Node;
import maindiff.util.OperationEnum;
import maindiff.xml.v2.textdiff.TextDiff4Dom;
import maindiff.xml.v2.work.XmlNode;
import maindiff.xml.v2.work.XmlTree;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class XmlDiffDomOutput {
    private XmlTree leftTree;
    private XmlTree rightTree;

    private List<XmlOutputNode> leftList;
    private List<XmlOutputNode> rightList;

    private Stack<OutputElementNode> leftStack;
    private Stack<OutputElementNode> rightStack;

    private Document leftDocument;
    private Document rightDocument;

    private void nodeElementOutput(OutputElementNode leftFather, OutputElementNode rightFather,
                                   XmlOutputNode leftOutputNode, XmlOutputNode rightOutputNode,
                                   OperationEnum operationType) {
        XmlNode leftNode = null;
        XmlNode rightNode = null;
        if (leftOutputNode != null) {
            leftNode = (XmlNode) leftOutputNode.node;
        }
        if (rightOutputNode != null) {
            rightNode = (XmlNode) rightOutputNode.node;
        }

        Element leftTextElement = DocumentHelper.createElement("div");
        Element rightTextElement = DocumentHelper.createElement("div");
        if (operationType == OperationEnum.CHANGE && leftNode != null && rightNode != null &&
                leftNode.tagName.equals(rightNode.tagName)) {
            TextDiff4Dom.textDiffTextOutput(leftNode.textArr, rightNode.textArr, leftTextElement, rightTextElement);
        } else if (operationType == OperationEnum.DELETE && leftNode != null) {
            leftTextElement.addText(leftNode.text);
        } else if (operationType == OperationEnum.INSERT && rightNode != null) {
            rightTextElement.addText(rightNode.text);
        } else if (leftNode != null && rightNode != null) {
            leftTextElement.addText(leftNode.text);
            rightTextElement.addText(rightNode.text);
        }
//        elementOutput(leftOutput, leftOutputNode, textOutputLeft, operationType);
//        elementOutput(rightOutput, rightOutputNode, textOutputRight, operationType);
    }

    private void elementOutput() {

    }

    public void buildDocuments() {
        Iterator<XmlOutputNode> leftIterator = leftList.iterator();
        Iterator<XmlOutputNode> rightIterator = rightList.iterator();

        XmlOutputNode leftOutputNode = null;
        XmlOutputNode rightOutputNode = null;
        OutputElementNode leftFather;
        OutputElementNode rightFather;

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
            leftFather = leftStack.peek();
            rightFather = rightStack.peek();
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
                nodeElementOutput(leftFather, rightFather, leftOutputNode, rightOutputNode, OperationEnum.UNCHANGE);
                if (!leftOutputNode.isEndTag) {
                    leftStack.push(new OutputElementNode(leftNode));
                } else {
                    leftStack.pop();
                }

                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            } else if (leftNode.operationType == OperationEnum.DELETE) {
                nodeElementOutput(leftFather, rightFather, leftOutputNode, null, OperationEnum.DELETE);
                leftNext = true;
                rightNext = false;
                leftIndex++;
            } else if (rightNode.operationType == OperationEnum.INSERT) {
                nodeElementOutput(leftFather, rightFather, null, rightOutputNode, OperationEnum.INSERT);
                leftNext = false;
                rightNext = true;
                rightIndex++;
            } else if (leftNode.operationType == OperationEnum.CHANGE &&
                    rightNode.operationType == OperationEnum.CHANGE) {
                nodeElementOutput(leftFather, rightFather, leftOutputNode, rightOutputNode, OperationEnum.CHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            }
        }
    }

    public void documentFileOutput(String leftFileName, String rightFileName) throws IOException {
        leftDocument = DocumentHelper.createDocument();
        rightDocument = DocumentHelper.createDocument();

        Element leftRootElement = DocumentHelper.createElement("pre");
        Element rightRootElement = DocumentHelper.createElement("pre");

        leftStack.push(new OutputElementNode(leftRootElement));
        leftStack.push(new OutputElementNode(rightRootElement));

        leftDocument.add(leftRootElement);
        rightDocument.add(rightRootElement);

        leftList = leftTree.nodeOutputSequence;
        rightList = rightTree.nodeOutputSequence;

        buildDocuments();

        // 输出结果document
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        documentFileOutput(leftFileName, leftDocument, format);
        documentFileOutput(rightFileName, rightDocument, format);
    }

    private void documentFileOutput(String filename, Document document, OutputFormat format) throws IOException {
        File file = new File(filename);
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(document);
        writer.close();
    }

}
