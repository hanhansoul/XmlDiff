package maindiff.abs.work;

import maindiff.util.OperationEnum;

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

//    public OperationValue(int prevX, int prevY, boolean isFromPermanentArr,
//                          int curX, int curY, OperationEnum operationType) {
//        this.prevX = prevX;
//        this.prevY = prevY;
//        this.isFromPermanentArr = isFromPermanentArr;
//        this.curX = curX;
//        this.curY = curY;
//        this.operationType = operationType;
//    }

//    public OperationValue() {
//        prevX = prevY = 0;
//        curX = curY = 0;
//        operationType = null;
//        isFromPermanentArr = false;
//    }

    public void assign(int cx, int cy, Operation op) {
        this.prevX = op.arrValue.curX;
        this.prevY = op.arrValue.curY;
        this.curX = cx;
        this.curY = cy;
        this.operationType = op.operationType;
        this.isFromPermanentArr = op.isFromPermanentArr;
    }

    public void assign(int cx, int cy, OperationValue opv){
        this.prevX = opv.curX;
        this.prevY = opv.curY;
        this.curX = cx;
        this.curY = cy;
        this.operationType = opv.operationType;
        this.isFromPermanentArr = opv.isFromPermanentArr;
    }

    public abstract void assign(OperationValue opv, Node leftNode, Node rightNode,
                                int cx, int cy, boolean isFromPermanentArr);

    public abstract void assign(Operation op, int cx, int cy);

    public abstract void findMinAndAssign(int cx, int cy, Operation... operations);

}
