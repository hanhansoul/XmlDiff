package maindiff.simple.work;

import maindiff.abs.work.Node;
import maindiff.abs.work.Operation;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;
import org.dom4j.Element;

public class SimpleOperationValue extends OperationValue {
    public int value;

    /**
     * 用于递推方程每次递推的初始化赋值
     */
    @Override
    public void assign(OperationValue opv, Node leftNode, Node rightNode, int cx, int cy, boolean isFromPermanentArr) {
        if (leftNode == null && rightNode == null) {
            return;
        }

        assign(cx, cy, opv);
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

    /**
     * public OperationValue arrValue;
     * public OperationEnum operationType;
     * public boolean isFromPermanentArr;
     * 用于在findMinAndAssign()将最优值赋值给当前对象
     */
    @Override
    public void assign(Operation op, int cx, int cy) {
        super.assign(cx, cy, op);
        this.value = ((SimpleOperationValue) op.arrValue).value + (op.operationType == OperationEnum.UNCHANGE ? 0 : 1);
    }

    @Override
    public void findMinAndAssign(int cx, int cy, Operation... operations) {
        Operation optimalOperation = null;
        for (Operation op : operations) {
            if (optimalOperation == null || compare(optimalOperation, op) > 0) {
                optimalOperation = op;
            }
        }
        assign(optimalOperation, cx, cy);
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

    @Override
    public int compare(Object o1, Object o2) {
        int value1 = computeValue(o1);
        int value2 = computeValue(o2);
//        int value1 = ((SimpleOperationValue) o1).value;
//        int value2 = ((SimpleOperationValue) o2).value;
        return value1 - value2;
    }

}
