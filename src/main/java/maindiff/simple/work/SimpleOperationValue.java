package maindiff.simple.work;

import maindiff.abs.work.*;
import maindiff.util.OperationEnum;
import org.dom4j.Element;

public class SimpleOperationValue extends OperationValue {
    public int value;

    @Override
    public void assign(OperationValue opv) {
        commonAssign(opv);
        SimpleOperationValue sopv = (SimpleOperationValue) opv;
        this.value = sopv.value;
    }

    /**
     * public OperationValue arrValue;
     * public OperationEnum operationType;
     * public boolean isFromPermanentArr;
     * 用于在findMinAndAssign()将最优值赋值给当前对象
     */
    @Override
    public void assign(int cx, int cy, GenericOperation op) {
        commonAssign(cx, cy, op);
        this.value = ((SimpleOperationValue) op.arrValue).value + (op.operationType == OperationEnum.UNCHANGE ? 0 : 1);
    }

    @Override
    public void assign(int cx, int cy, DerivedOperation op) {
        commonAssign(cx, cy, op);
        this.value = ((SimpleDerivedOperation) op).value + (op.operationType == OperationEnum.UNCHANGE ? 0 : 1);
    }

    /**
     * 用于递推方程每次递推的初始化赋值
     */
    @Override
    public void assign(OperationValue opv, Node leftNode, Node rightNode, int cx, int cy, boolean isFromPermanentArr) {
        if (leftNode == null && rightNode == null) {
            return;
        }

        commonAssign(cx, cy, opv);
        if (leftNode == null) {
            this.operationType = OperationEnum.INSERT;
        } else if (rightNode == null) {
            this.operationType = OperationEnum.DELETE;
        } else {
            Element leftElement = leftNode.element;
            Element rightElement = rightNode.element;
            if (!leftElement.getName().equals(rightElement.getName())) {
                this.operationType = OperationEnum.CHANGE;
            } else {
                this.operationType = OperationEnum.UNCHANGE;
            }
        }
        this.value = ((SimpleOperationValue) opv).value +
                (this.operationType == OperationEnum.UNCHANGE ? 0 : 1);
    }

//    @Override
//    public void findMinAndAssign(int cx, int cy, Operation... operations) {
//        Operation optimalOperation = null;
//        for (Operation op : operations) {
//            if (optimalOperation == null || compare(optimalOperation, op) > 0) {
//                optimalOperation = op;
//            }
//        }
//        if (optimalOperation instanceof GenericOperation) {
//            assign(cx, cy, (GenericOperation) optimalOperation);
//        } else if (optimalOperation instanceof DerivedOperation) {
//            assign(cx, cy, (DerivedOperation) optimalOperation);
//        }
//    }

    /**
     * findMinAndAssign()用于比较
     */
    @Override
    public int compare(Object o1, Object o2) {
        int value1 = computeValue(o1);
        int value2 = computeValue(o2);
        return value1 - value2;
    }

    private int computeValue(Object o) {
        if (o instanceof SimpleGenericOperation) {
            SimpleGenericOperation op = (SimpleGenericOperation) o;
            return op.value;
        } else if (o instanceof SimpleDerivedOperation) {
            SimpleDerivedOperation op = (SimpleDerivedOperation) o;
            return op.value;
        }
        return -1;
    }
}
