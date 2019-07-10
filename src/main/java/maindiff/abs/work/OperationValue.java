package maindiff.abs.work;

import maindiff.util.OperationEnum;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 表示单次操作的结果
 * <p>
 * OperationValue
 * 属性：
 * 1. 前驱节点编号
 * 2. 当前递推值
 * 3. 当前操作类型
 * 4. 当前操作节点内容
 * 5. 当前节点编号
 * <p>
 * 方法：
 * 1. add()：在计算递推式时，前驱OperationValue对象添加一个操作得到并返回当前OperationValue对象
 * 3. compareTo()：比较两个OperationValue对象值大小，以便递推时取出最小值
 */
public abstract class OperationValue implements Comparator {
    public int prevX, prevY;
    public int curX, curY;
    public OperationEnum operationType;
    public boolean isFromPermanentArr;

    public abstract void assign(OperationValue opv);

    public abstract void assign(int cx, int cy, GenericOperation op);

    public abstract void assign(int cx, int cy, DerivedOperation op);

    public abstract void assign(OperationValue opv, Node leftNode, Node rightNode,
                                int cx, int cy, boolean isFromPermanentArr);

    public void findMinAndAssign(int cx, int cy, Operation... operations) {
        Operation optimalOperation = null;
        for (Operation op : operations) {
            if (optimalOperation == null || compare(optimalOperation, op) > 0) {
                optimalOperation = op;
            }
        }
        if (optimalOperation instanceof GenericOperation) {
            assign(cx, cy, (GenericOperation) optimalOperation);
        } else if (optimalOperation instanceof DerivedOperation) {
            assign(cx, cy, (DerivedOperation) optimalOperation);
        }
    }

    public void commonAssign(int cx, int cy, Operation op) {
        this.prevX = op.arrValue.curX;
        this.prevY = op.arrValue.curY;
        this.curX = cx;
        this.curY = cy;
        this.operationType = op.operationType;
        this.isFromPermanentArr = op.isFromPermanentArr;
    }

    public void commonAssign(int cx, int cy, OperationValue opv) {
        this.prevX = opv.curX;
        this.prevY = opv.curY;
        this.curX = cx;
        this.curY = cy;
        this.operationType = opv.operationType;
        this.isFromPermanentArr = opv.isFromPermanentArr;
    }

    /**
     * public int prevX, prevY;
     * public int curX, curY;
     * public OperationEnum operationType;
     * public boolean isFromPermanentArr;
     */
    public void commonAssign(OperationValue opv) {
        this.prevX = opv.prevX;
        this.prevY = opv.prevY;
        this.curX = opv.curX;
        this.curY = opv.curY;
        this.operationType = opv.operationType;
        this.isFromPermanentArr = opv.isFromPermanentArr;
    }

}
