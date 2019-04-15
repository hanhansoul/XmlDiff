package maindiff.xml.work;

import maindiff.abs.work.Node;
import maindiff.abs.work.Operation;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;

// TODO
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

//    public XmlOperationValue(int prevX, int prevY, boolean isFromPermanentArr, int curX, int curY, Operation operation,
//                             int elementNameDiffValue, int elementAttributesDiffValue, int elementTextDiffValue) {
//        super(prevX, prevY, isFromPermanentArr, curX, curY, operation);
//        this.elementNameDiffValue = elementNameDiffValue;
//        this.elementAttributesDiffValue = elementAttributesDiffValue;
//        this.elementTextDiffValue = elementTextDiffValue;
//    }

    /**
     * 将opv内容赋值给当前XmlOperationValue
     * opv + opValue(leftNode, rightNode)
     */
    @Override
    public void assign(OperationValue opv, Node leftNode, Node rightNode,
                       int cx, int cy, boolean isFromPermanentArr) {

    }

    @Override
    public void assign(Operation op, int cx, int cy) {

    }

    @Override
    public void findMinAndAssign(int cx, int cy, Operation... operations) {

    }

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }

//    @Override
//    public int compareTo(Object o) {
//        XmlOperationValue other = (XmlOperationValue) o;
//        if (this.elementNameDiffValue == other.elementNameDiffValue) {
//            if (this.elementTextDiffValue == other.elementTextDiffValue) {
//                return this.elementAttributesDiffValue - other.elementAttributesDiffValue;
//            } else {
//                return this.elementTextDiffValue - other.elementTextDiffValue;
//            }
//        } else {
//            return this.elementNameDiffValue - other.elementNameDiffValue;
//        }
//    }


}
