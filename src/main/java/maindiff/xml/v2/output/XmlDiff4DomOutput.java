package maindiff.xml.v2.output;

import maindiff.abs.output.DiffOutput;
import maindiff.abs.output.OutputNode;
import maindiff.abs.work.Node;
import maindiff.util.OperationEnum;
import maindiff.xml.v2.work.XmlTree;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class XmlDiff4DomOutput {
    private XmlTree leftTree;
    private XmlTree rightTree;

    public void buildDocuments(List<? extends OutputNode> leftList, List<? extends OutputNode> rightList) {
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
            if ((leftNode.operationType == null || leftNode.operationType == OperationEnum.UNCHANGE) &&
                    (rightNode.operationType == null || rightNode.operationType == OperationEnum.UNCHANGE)) {
                // nodesElementOutput(leftOutput, rightOutput, leftOutputNode, rightOutputNode, OperationEnum.UNCHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            } else if (leftNode.operationType == OperationEnum.DELETE) {
                // nodesElementOutput(leftOutput, rightOutput, leftOutputNode, null, OperationEnum.DELETE);
                leftNext = true;
                rightNext = false;
                leftIndex++;
            } else if (rightNode.operationType == OperationEnum.INSERT) {
                // nodesElementOutput(leftOutput, rightOutput, null, rightOutputNode, OperationEnum.INSERT);
                leftNext = false;
                rightNext = true;
                rightIndex++;
            } else if (leftNode.operationType == OperationEnum.CHANGE &&
                    rightNode.operationType == OperationEnum.CHANGE) {
                // nodesElementOutput(leftOutput, rightOutput, leftOutputNode, rightOutputNode, OperationEnum.CHANGE);
                leftNext = rightNext = true;
                leftIndex++;
                rightIndex++;
            }
        }
    }

    private void nodesElementOutput() {

    }

    private void documentFileOutput(String filename, Document document, OutputFormat format) throws IOException {
        File file = new File(filename);
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(document);
        writer.close();
    }

    public void resultOutput(String leftFileName, String rightFileName) throws IOException {
        Document leftDocument = DocumentHelper.createDocument();
        Document rightDocument = DocumentHelper.createDocument();



        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        documentFileOutput(leftFileName, leftDocument, format);
        documentFileOutput(rightFileName, rightDocument, format);
    }

}
