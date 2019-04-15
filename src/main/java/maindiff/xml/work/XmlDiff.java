package maindiff.xml.work;

import maindiff.abs.output.Path;
import maindiff.abs.work.*;
import maindiff.util.OpValueElementNullException;
import maindiff.util.OperationEnum;
import org.dom4j.DocumentException;

public class XmlDiff extends AbstractDiff {

    @Override
    public void initialize(int left, int right) {
        temporaryArr = new XmlOperationValue[left][right];
        permanentArr = new XmlOperationValue[left][right];
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, Node leftNode, Node rightNode, OperationEnum operationType) {
        return null;
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, OperationValue permanentArrValue) {
        return null;
    }

    @Override
    public void initialize(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new XmlTree(leftFileName);
        rightTree = new XmlTree(rightFileName);
        permanentArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr[0][0] = new XmlOperationValue();
        operationPaths = new Path[leftTree.size + 1][rightTree.size + 1];
    }

//    @Override
//    protected Operation opValue(Node leftNode, Node rightNode) throws OpValueElementNullException {
//        if (leftNode == null && rightNode == null) {
//            throw new OpValueElementNullException();
//        }
//        if (leftNode == null) {
//            return new XmlOperation();
//        } else if (rightNode == null) {
//            return new XmlOperation();
//        }
//    }
//
//    @Override
//    protected OperationValue min(OperationValue... values) {
//        OperationValue minValue = null;
//        for (OperationValue value : values) {
//            if (minValue == null) {
//                minValue = value;
//            } else if (minValue.compareTo(value) > 0) {
//                minValue = value;
//            }
//        }
//        return minValue;
//    }


}