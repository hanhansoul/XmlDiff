package maindiff.xml.work;

import maindiff.abs.output.Path;
import maindiff.abs.work.*;
import maindiff.util.OperationEnum;
import maindiff.xml.output.XmlDiffOutput;
import maindiff.xml.textdiff.TextDiff;
import org.dom4j.DocumentException;

import java.io.IOException;

import static maindiff.util.Constant.RATIO_THRESHOLD;

public class XmlDiff extends AbstractDiff {

    @Override
    public Operation generateOperation(OperationValue arrValue, Node leftNode, Node rightNode,
                                       OperationEnum operationType) {
        XmlOperationValue xopv = (XmlOperationValue) arrValue;
        XmlNode xmlLeftNode = (XmlNode) leftNode;
        XmlNode xmlRightNode = (XmlNode) rightNode;
        int elementNameDiffValue = 0;
        double elementTextDiffValue = 0;
        if (operationType == null) {
            double diffRatio;
            int totalLength = 0;
            if (xmlLeftNode.tagName.equals(xmlRightNode.tagName)) {
                if (xmlLeftNode.textArr.length > 0 && xmlRightNode.textArr.length > 0) {
                    double similarRatio = TextDiff.textDiffRatioCompute(xmlLeftNode.textArr, xmlRightNode.textArr);
                    diffRatio = similarRatio <= RATIO_THRESHOLD ? 1 : 1 - similarRatio;
                    totalLength = xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                } else {
                    totalLength += xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                    if(xmlLeftNode.textArr.length == 0 && xmlRightNode.textArr.length == 0) {
                        diffRatio = 0;
                    } else {
                        diffRatio = 1;
                    }
                }
                elementNameDiffValue = xopv.elementNameDiffValue;
                elementTextDiffValue = xopv.elementTextDiffValue + 1.0 * totalLength * diffRatio / 2;
                operationType = diffRatio <= 0.001 ? OperationEnum.UNCHANGE : OperationEnum.CHANGE;
            } else {
                elementNameDiffValue = xopv.elementNameDiffValue + 1;
                totalLength += xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                elementTextDiffValue = xopv.elementTextDiffValue + 1.0 * totalLength / 2;
                operationType = OperationEnum.CHANGE;
            }
        } else if (operationType == OperationEnum.INSERT) {
            elementNameDiffValue = xopv.elementNameDiffValue + 1;
            elementTextDiffValue = xopv.elementTextDiffValue + xmlRightNode.textArr.length;
        } else if (operationType == OperationEnum.DELETE) {
            elementNameDiffValue = xopv.elementNameDiffValue + 1;
            elementTextDiffValue = xopv.elementTextDiffValue + xmlLeftNode.textArr.length;
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
    public void bugInspect(int number, int x, int y, OperationValue opv, boolean isFromPermanentArr) {
        if (!isFromPermanentArr) {
            System.out.println(number + ": temporaryArr[" + x + "][" + y + "] = " +
                    ((XmlOperationValue) opv).elementNameDiffValue + " from temporaryArr[" +
                    opv.prevX + "][" +
                    opv.prevY + "] through " +
                    opv.operationType);
        } else {
            System.out.println(number + ": temporaryArr[" + x + "][" + y + "] = " +
                    ((XmlOperationValue) opv).elementNameDiffValue + " from temporaryArr[" +
                    opv.prevX + "][" +
                    opv.prevY + "] through " +
                    opv.operationType +
                    " is from permanentArr[" + x + "][" + y + "] = " +
                    ((XmlOperationValue) opv).elementNameDiffValue);
        }
    }

    @Override
    public void initialize(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new XmlTree(leftFileName);
        rightTree = new XmlTree(rightFileName);
//        leftTree.sequenceTraversal();
//        System.out.println();
//        rightTree.sequenceTraversal();
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

    public static void main(String[] args) throws DocumentException, IOException {
        long beginTime = System.currentTimeMillis();
        XmlDiff simpleDiff = new XmlDiff();
        simpleDiff.initialize("data/CSC1.xml", "data/CSC2.xml");
//        simpleDiff.initialize("data/left1.xml", "data/right1.xml");
        simpleDiff.solve();
        long solveTime = System.currentTimeMillis();
        System.out.println((solveTime - beginTime) / 1000);
        new XmlDiffOutput(simpleDiff.leftTree, simpleDiff.rightTree).resultOutput();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - solveTime) / 1000);
    }

}
