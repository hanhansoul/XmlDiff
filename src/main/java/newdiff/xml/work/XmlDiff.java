package newdiff.xml.work;

import newdiff.abs.output.Path;
import newdiff.abs.work.*;
import newdiff.xml.output.XmlDiffOutput;
import newdiff.xml.text.TextDiff;
import org.dom4j.DocumentException;

import java.io.IOException;

import static newdiff.abs.work.Constant.RATIO_THRESHOLD;

public class XmlDiff extends AbstractDiff {

    @Override
    public void initialize(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new XmlTree(leftFileName);
        rightTree = new XmlTree(rightFileName);
        int left = leftTree.size;
        int right = rightTree.size;
        permanentArr = new XmlOperationValue[left + 1][right + 1];
        temporaryArr = new XmlOperationValue[left + 1][right + 1];
        for (int i = 0; i <= left; i++) {
            for (int j = 0; j <= right; j++) {
                temporaryArr[i][j] = new XmlOperationValue(i, j);
                permanentArr[i][j] = new XmlOperationValue(i, j);
            }
        }
        operationValues = new XmlOperationValue[3];
        for (int i = 0; i < 3; i++) {
            operationValues[i] = new XmlOperationValue();
        }
        operationPaths = new Path[left + 1][right + 1];
    }

    @Override
    public OperationMove generateOperationMove(Node leftNode, Node rightNode, OperationValue operationValue) {
        XmlNode xmlLeftNode = (XmlNode) leftNode;
        XmlNode xmlRightNode = (XmlNode) rightNode;
        long elementNameDiffValue = 0;
        long elementAttributesDiffValue = 0;
        double elementTextDiffValue = 0;
        OperationMoveType operationMoveType = null;
        if (leftNode == null && rightNode == null) {
            return null;
        } else if (leftNode == null && rightNode != null) {
            elementNameDiffValue = 1;
            elementTextDiffValue = xmlRightNode.textArr.length;
            return XmlOperationMove.updateInstance(operationValue.curX,
                    operationValue.curY,
                    OperationMoveType.INSERT,
                    false,
                    elementNameDiffValue,
                    elementAttributesDiffValue,
                    elementTextDiffValue);
        } else if (leftNode != null && rightNode == null) {
            elementNameDiffValue = 1;
            elementTextDiffValue = xmlLeftNode.textArr.length;
            return XmlOperationMove.updateInstance(operationValue.curX,
                    operationValue.curY,
                    OperationMoveType.DELETE,
                    false,
                    elementNameDiffValue,
                    elementAttributesDiffValue,
                    elementTextDiffValue);
        } else {
            double diffRatio;
            int totalLength = 0;
            if (xmlLeftNode.tagName.equals(xmlRightNode.tagName)) {
                if (xmlLeftNode.textArr.length > 0 && xmlRightNode.textArr.length > 0) {
                    double similarRatio = TextDiff.textDiffRatioCompute(xmlLeftNode.textArr, xmlRightNode.textArr);
                    diffRatio = similarRatio <= RATIO_THRESHOLD ? 1 : 1 - similarRatio;
                    totalLength = xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                } else {
                    totalLength += xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                    if (xmlLeftNode.textArr.length == 0 && xmlRightNode.textArr.length == 0) {
                        diffRatio = 0;
                    } else {
                        diffRatio = 1;
                    }
                }
                elementNameDiffValue = 0;
                elementTextDiffValue = 1.0 * totalLength * diffRatio / 2;
                operationMoveType = diffRatio <= 0.001 ? OperationMoveType.UNCHANGE : OperationMoveType.CHANGE;
            } else {
                elementNameDiffValue = 1;
                totalLength += xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                elementTextDiffValue = 1.0 * totalLength / 2;
                operationMoveType = OperationMoveType.CHANGE;
            }
            return XmlOperationMove.updateInstance(operationValue.curX,
                    operationValue.curY,
                    operationMoveType,
                    false,
                    elementNameDiffValue,
                    elementAttributesDiffValue,
                    elementTextDiffValue);
        }
    }

    @Override
    public OperationMove generateOperationMove(OperationValue permanentOperationValue, OperationValue operationValue) {
        XmlOperationValue xmlPermanentOperationValue = (XmlOperationValue) permanentOperationValue;
        long elementNameDiffValue = xmlPermanentOperationValue.elementNameDiffValue;
        long elementAttributesDiffValue = 0;
        double elementTextDiffValue = xmlPermanentOperationValue.elementTextDiffValue;
        return XmlOperationMove.updateInstance(operationValue.curX,
                operationValue.curY,
                OperationMoveType.UNCHANGE,
                true,
                elementNameDiffValue,
                elementAttributesDiffValue,
                elementTextDiffValue);
    }

    @Override
    public void generateOperationValueForOperationValueArray(int index, OperationValue operationValue, OperationMove operationMove) {
        XmlOperationValue xmlOperationValue = (XmlOperationValue) operationValue;
        XmlOperationMove xmlOperationMove = (XmlOperationMove) operationMove;
        XmlOperationValue xmlOperationValueArr = (XmlOperationValue) operationValues[index];
        xmlOperationValueArr.commonAssign(operationMove);
        xmlOperationValueArr.elementNameDiffValue = xmlOperationValue.elementNameDiffValue + xmlOperationMove.xmlOperationTagNameDiffValue;
        xmlOperationValueArr.elementAttributesDiffValue = xmlOperationValue.elementAttributesDiffValue + xmlOperationMove.xmlOperationAttributeDiffValue;
        xmlOperationValueArr.elementTextDiffValue = xmlOperationValue.elementTextDiffValue + xmlOperationMove.xmlOperationTextDiffValue;
    }

    @Override
    public void generateOperationValueForOperationValueArray(int index, OperationValue operationValue, OperationValue permanentOperationValue) {
        XmlOperationValue xmlOperationValue = (XmlOperationValue) operationValue;
        XmlOperationValue xmlPermanentOperationValue = (XmlOperationValue) permanentOperationValue;
        XmlOperationValue xmlOperationValueArr = (XmlOperationValue) operationValues[index];
        xmlOperationValueArr.elementNameDiffValue = xmlOperationValue.elementNameDiffValue + xmlPermanentOperationValue.elementNameDiffValue;
        xmlOperationValueArr.elementAttributesDiffValue = xmlOperationValue.elementAttributesDiffValue + xmlPermanentOperationValue.elementAttributesDiffValue;
        xmlOperationValueArr.elementTextDiffValue = xmlOperationValue.elementTextDiffValue + xmlPermanentOperationValue.elementTextDiffValue;
    }

    public static void main(String[] args) throws DocumentException, IOException {
        long beginTime = System.currentTimeMillis();
        XmlDiff xmlDiff = new XmlDiff();
//        xmlDiff.initialize("data/CSC1.xml", "data/CSC2.xml");
        xmlDiff.initialize("data/left6.xml", "data/right6.xml");
        xmlDiff.solve();
        long solveTime = System.currentTimeMillis();
        System.out.println(solveTime - beginTime);
        new XmlDiffOutput(xmlDiff.leftTree, xmlDiff.rightTree)
                .resultOutput("data/newOutputLeft.html", "data/newOutputRight.html");
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - solveTime);
    }
}
