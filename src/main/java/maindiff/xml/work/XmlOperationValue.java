package maindiff.xml.work;

import maindiff.abs.work.*;
import maindiff.util.OperationEnum;

// TODO XmlOperationValue
public class XmlOperationValue extends OperationValue {

    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public int elementTextDiffValue;

    public XmlOperationValue() {
        super();
        elementNameDiffValue = 0;
        elementAttributesDiffValue = 0;
        elementTextDiffValue = 0;
    }

    @Override
    public void assign(OperationValue opv) {

    }

    @Override
    public void assign(int cx, int cy, GenericOperation op) {

    }

    @Override
    public void assign(int cx, int cy, DerivedOperation op) {

    }


    /**
     * 将opv内容赋值给当前XmlOperationValue
     * opv + opValue(leftNode, rightNode)
     */
    @Override
    public void assign(OperationValue opv, Node leftNode, Node rightNode,
                       int cx, int cy, boolean isFromPermanentArr) {

    }

    @Override
    public void findMinAndAssign(int cx, int cy, Operation... operations) {

    }

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }

}
