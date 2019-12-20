package newdiff.xml;

import newdiff.abs.*;

public class XmlDiff extends AbstractDiff {

    @Override
    public void initialization(String leftFileName, String rightFileName) {
        operationValues = new XmlOperationValue[3];
    }

    @Override
    public OperationMove generateOperationMove(Node leftNode, Node rightNode) {
        OperationMoveType operationMoveType = null;
        XmlNode xmlLeftNode = (XmlNode) leftNode;
        XmlNode xmlRightNode = (XmlNode) rightNode;
        int elementNameDiffValue = 0;
        double elementTextDiffValue = 0;
        if (leftNode == null && rightNode == null) {
            return null;
        } else if (leftNode == null && rightNode != null) {
            operationMoveType = OperationMoveType.INSERT;
        } else if (leftNode != null && rightNode == null) {
            operationMoveType = OperationMoveType.DELETE;
        } else {

        }
        return null;
    }

    @Override
    public OperationMove generateOperationMove(OperationValue operationValue) {
        OperationMoveType operationMoveType = null;
        return null;
    }

    @Override
    public void generateOperationValueForOperationValueArray(int index, OperationValue operationValue, OperationMove operationMove) {

    }

    @Override
    public void generateOperationValueForOperationValueArray(int index, OperationValue operationValue, OperationValue permanentOperationValue) {

    }
}
