package maindiff.xml.v2.work;

import maindiff.abs.work.DerivedOperation;
import maindiff.abs.work.GenericOperation;
import maindiff.abs.work.Node;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;
import maindiff.xml.v2.textdiff.TextDiff;

import static maindiff.util.Constant.RATIO_THRESHOLD;

public class XmlOperationValue extends OperationValue {

    static final double DELTA = 0.0000001;

    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public double elementTextDiffValue;

    @Override
    public void assign(OperationValue opv) {
        commonAssign(opv);
        XmlOperationValue xopv = (XmlOperationValue) opv;
        this.elementNameDiffValue = xopv.elementNameDiffValue;
        this.elementAttributesDiffValue = xopv.elementAttributesDiffValue;
        this.elementTextDiffValue = xopv.elementTextDiffValue;
    }

    @Override
    public void assign(int cx, int cy, GenericOperation op) {
        commonAssign(cx, cy, op);
        XmlGenericOperation xgop = (XmlGenericOperation) op;
//        XmlOperationValue xopv = (XmlOperationValue) xgop.arrValue;
//        this.elementNameDiffValue = xopv.elementNameDiffValue + xgop.xmlOperationTagNameDiffValue;
//        this.elementTextDiffValue = xopv.elementTextDiffValue + xgop.xmlOperationTextDiffValue;
        this.elementNameDiffValue = xgop.xmlOperationTagNameDiffValue;
        this.elementTextDiffValue = xgop.xmlOperationTextDiffValue;
    }

    @Override
    public void assign(int cx, int cy, DerivedOperation op) {
        commonAssign(cx, cy, op);
        XmlDerivedOperation xgop = (XmlDerivedOperation) op;
//        XmlOperationValue xopv = (XmlOperationValue) xgop.arrValue;
//        this.elementNameDiffValue = xopv.elementNameDiffValue + xgop.xmlOperationTagNameDiffValue;
//        this.elementTextDiffValue = xopv.elementTextDiffValue + xgop.xmlOperationTextDiffValue;
        this.elementNameDiffValue = xgop.xmlOperationTagNameDiffValue;
        this.elementTextDiffValue = xgop.xmlOperationTextDiffValue;
    }

    /**
     * 将opv内容赋值给当前XmlOperationValue
     * opv + opValue(leftNode, rightNode)
     * <p>
     * this.value = opv.value + computeValue(leftNode, rightNode)
     */
    @Override
    public void assign(OperationValue opv, Node leftNode, Node rightNode,
                       int cx, int cy, boolean isFromPermanentArr) {
        if (leftNode == null && rightNode == null) {
            return;
        }
        commonAssign(cx, cy, opv);
        XmlOperationValue xopv = (XmlOperationValue) opv;
        XmlNode xmlLeftNode = (XmlNode) leftNode;
        XmlNode xmlRightNode = (XmlNode) rightNode;
        if (xmlLeftNode == null) {
            this.elementNameDiffValue = xopv.elementNameDiffValue + 1;
            this.elementTextDiffValue = xopv.elementTextDiffValue + xmlRightNode.textArr.length;
            this.operationType = OperationEnum.INSERT;
        } else if (xmlRightNode == null) {
            this.elementNameDiffValue = xopv.elementNameDiffValue + 1;
            this.elementTextDiffValue = xopv.elementTextDiffValue + xmlLeftNode.textArr.length;
            this.operationType = OperationEnum.DELETE;
        } else {
            double diffRatio;
            int totalLength = 0;
            if (xmlLeftNode.depth == xmlRightNode.depth && xmlLeftNode.tagName.equals(xmlRightNode.tagName)) {
                if (xmlLeftNode.textArr.length > 0 && xmlRightNode.textArr.length > 0) {
                    double similarRatio = TextDiff.textDiffRatioCompute(xmlLeftNode.textArr, xmlRightNode.textArr);
                    diffRatio = similarRatio <= RATIO_THRESHOLD ? 1 : 1 - similarRatio;
                    totalLength = xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                } else {
                    totalLength += xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                    diffRatio = xmlLeftNode.textArr.length == 0 && xmlRightNode.textArr.length == 0 ? 0 : 1;
                }
                this.elementTextDiffValue = xopv.elementTextDiffValue + 1.0 * totalLength * diffRatio / 2;
                this.operationType = diffRatio <= DELTA ? OperationEnum.UNCHANGE : OperationEnum.CHANGE;
            } else {
                this.elementNameDiffValue = xopv.elementNameDiffValue + 1;
                totalLength += xmlLeftNode.textArr.length + xmlRightNode.textArr.length;
                this.elementTextDiffValue = xopv.elementTextDiffValue + 1.0 * totalLength / 2;
                this.operationType = OperationEnum.CHANGE;
            }
        }
    }

    @Override
    public int compare(Object o1, Object o2) {
        int elementNameDiffValue1, elementNameDiffValue2;
        double elementTextDiffValue1, elementTextDiffValue2;
        elementNameDiffValue1 = elementNameDiffValue2 = 0;
        elementTextDiffValue1 = elementTextDiffValue2 = 0;
        if (o1 instanceof XmlGenericOperation) {
            elementNameDiffValue1 = ((XmlGenericOperation) o1).xmlOperationTagNameDiffValue;
            elementTextDiffValue1 = ((XmlGenericOperation) o1).xmlOperationTextDiffValue;
        } else if (o1 instanceof XmlDerivedOperation) {
            elementNameDiffValue1 = ((XmlDerivedOperation) o1).xmlOperationTagNameDiffValue;
            elementTextDiffValue1 = ((XmlDerivedOperation) o1).xmlOperationTextDiffValue;
        }
        if (o2 instanceof XmlGenericOperation) {
            elementNameDiffValue2 = ((XmlGenericOperation) o2).xmlOperationTagNameDiffValue;
            elementTextDiffValue2 = ((XmlGenericOperation) o2).xmlOperationTextDiffValue;
        } else if (o2 instanceof XmlDerivedOperation) {
            elementNameDiffValue2 = ((XmlDerivedOperation) o2).xmlOperationTagNameDiffValue;
            elementTextDiffValue2 = ((XmlDerivedOperation) o2).xmlOperationTextDiffValue;
        }
        if (elementNameDiffValue1 != elementNameDiffValue2) {
            return elementNameDiffValue1 - elementNameDiffValue2;
        } else {
            if (elementTextDiffValue1 - elementTextDiffValue2 > DELTA) {
                return 1;
            } else if (elementTextDiffValue1 - elementTextDiffValue2 < -1 * DELTA) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
