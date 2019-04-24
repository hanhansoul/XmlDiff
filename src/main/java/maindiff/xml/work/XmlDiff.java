package maindiff.xml.work;

import maindiff.abs.output.Path;
import maindiff.abs.work.*;
import maindiff.util.OpValueElementNullException;
import maindiff.util.OperationEnum;
import maindiff.xml.textdiff.TextDiff;
import org.dom4j.DocumentException;

import static maindiff.util.Constant.RATIO_SHREHOLD;

public class XmlDiff extends AbstractDiff {

    @Override
    public Operation generateOperation(OperationValue arrValue, Node leftNode, Node rightNode,
                                       OperationEnum operationType) {
        XmlOperationValue xopv = (XmlOperationValue) arrValue;
        XmlNode xmlLeftNode = (XmlNode) leftNode;
        XmlNode xmlRightNode = (XmlNode) rightNode;
        int elementNameDiffValue;
        double elementTextDiffValue;
        if (xmlLeftNode == null) {
            elementNameDiffValue = xopv.elementNameDiffValue + 1;
            elementTextDiffValue = xopv.elementTextDiffValue + xmlRightNode.text.length;
            operationType = OperationEnum.INSERT;
        } else if (xmlRightNode == null) {
            elementNameDiffValue = xopv.elementNameDiffValue + 1;
            elementTextDiffValue = xopv.elementTextDiffValue + xmlLeftNode.text.length;
            operationType = OperationEnum.DELETE;
        } else {
            double diffRatio;
            int totalLength = 0;
            if (xmlLeftNode.tagName.equals(xmlRightNode.tagName)) {
                if (xmlLeftNode.text != null && xmlRightNode.text != null) {
                    double similarRatio = TextDiff.textDiffCompute(xmlLeftNode.text, xmlRightNode.text, false);
                    diffRatio = similarRatio <= RATIO_SHREHOLD ? 1 : 1 - similarRatio;
                    totalLength = xmlLeftNode.text.length + xmlRightNode.text.length;
                } else {
                    if (xmlLeftNode.text != null) {
                        totalLength += xmlLeftNode.text.length;
                    }
                    if (xmlRightNode.text != null) {
                        totalLength += xmlRightNode.text.length;
                    }
                    diffRatio = 1;
                }
                elementNameDiffValue = 0;
                elementTextDiffValue = xopv.elementTextDiffValue + 1.0 * totalLength * diffRatio / 2;
                operationType = diffRatio <= 0.001 ? OperationEnum.UNCHANGE : OperationEnum.CHANGE;
            } else {
                elementNameDiffValue = xopv.elementNameDiffValue + 1;
                if (xmlLeftNode.text != null) {
                    totalLength += xmlLeftNode.text.length;
                }
                if (xmlRightNode.text != null) {
                    totalLength += xmlRightNode.text.length;
                }
                elementTextDiffValue = xopv.elementTextDiffValue + 1.0 * totalLength / 2;
                operationType = OperationEnum.CHANGE;
            }
        }

        XmlGenericOperation xgop = new XmlGenericOperation(arrValue, leftNode, rightNode,
                operationType, false);
        xgop.xmlOperationTagNameDiffValue = elementNameDiffValue;
        xgop.xmlOperationTextDiffValue = elementTextDiffValue;
        return xgop;
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, OperationValue permanentArrValue) {
        XmlDerivedOperation xdop = new XmlDerivedOperation(arrValue, (XmlOperationValue) permanentArrValue,
                OperationEnum.UNCHANGE, true);
        XmlOperationValue xopv = (XmlOperationValue) arrValue;
        XmlOperationValue pxopv = (XmlOperationValue) permanentArrValue;
        xdop.xmlOperationTagNameDiffValue = xopv.elementNameDiffValue + pxopv.elementNameDiffValue;
        xdop.xmlOperationTextDiffValue = xopv.elementTextDiffValue + pxopv.elementTextDiffValue;
        return xdop;
    }

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
                temporaryArr[i][j] = new XmlOperationValue();
                permanentArr[i][j] = new XmlOperationValue();
            }
        }
        operationPaths = new Path[left + 1][right + 1];
    }

}
