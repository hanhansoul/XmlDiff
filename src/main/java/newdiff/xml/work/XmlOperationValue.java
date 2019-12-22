package newdiff.xml.work;

import newdiff.abs.work.OperationMove;
import newdiff.abs.work.OperationValue;

public class XmlOperationValue extends OperationValue {

    public long elementNameDiffValue;
    public long elementAttributesDiffValue;
    public double elementTextDiffValue;

    public XmlOperationValue() {
    }

    public XmlOperationValue(int curX, int curY) {
        super(curX, curY);
    }

    @Override
    public void assign(OperationValue operationValue) {
        commonAssign(operationValue);
        XmlOperationValue xmlOperationValue = (XmlOperationValue) operationValue;
        this.elementNameDiffValue = xmlOperationValue.elementNameDiffValue;
        this.elementAttributesDiffValue = xmlOperationValue.elementAttributesDiffValue;
        this.elementTextDiffValue = xmlOperationValue.elementTextDiffValue;
    }

    @Override
    public void addAndAssign(OperationValue operationValue,
                             OperationMove operationMove) {
        commonAssign(operationMove);
        XmlOperationValue xmlOperationValue = (XmlOperationValue) operationValue;
        XmlOperationMove xmlOperationMove = (XmlOperationMove) operationMove;
        this.operationMoveType = xmlOperationMove.operationMoveType;
        this.elementNameDiffValue = xmlOperationValue.elementNameDiffValue + xmlOperationMove.xmlOperationTagNameDiffValue;
        this.elementAttributesDiffValue = xmlOperationValue.elementAttributesDiffValue + xmlOperationMove.xmlOperationAttributeDiffValue;
        this.elementTextDiffValue = xmlOperationValue.elementTextDiffValue + xmlOperationMove.xmlOperationTextDiffValue;
    }

    @Override
    public int compareTo(OperationValue o) {

        long anotherElementNameDiffValue = ((XmlOperationValue) o).elementNameDiffValue;
        long anotherElementAttributesDiffValue = ((XmlOperationValue) o).elementAttributesDiffValue;
        double anotherElementTextDiffValue = ((XmlOperationValue) o).elementTextDiffValue;

        if (elementNameDiffValue != anotherElementNameDiffValue) {
            return elementNameDiffValue > anotherElementNameDiffValue ? 1 : -1;
        } else {
            if (elementTextDiffValue - anotherElementTextDiffValue > 0.001) {
                return 1;
            } else if (elementTextDiffValue - anotherElementTextDiffValue < -0.001) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
