package maindiff.abs.work;

import maindiff.util.OperationEnum;

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
public abstract class OperationValue implements Comparable {
    public int prevX, prevY;
    public boolean isFromPermanentArr;
    public int curX, curY;
    public Operation operation;

    public OperationValue(int prevX, int prevY, boolean isFromPermanentArr, int curX, int curY, Operation operation) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.isFromPermanentArr = isFromPermanentArr;
        this.curX = curX;
        this.curY = curY;
        this.operation = operation;
    }

    public OperationValue() {
        prevX = prevY = 0;
        curX = curY = 0;
        operation = null;
        isFromPermanentArr = false;
    }

//    public abstract void add(Operation op, int cx, int cy, boolean isFromPermanentArr);

//    public abstract void add(OperationValue opv, int cx, int cy, boolean isFromPermanentArr);

    public abstract void assign(OperationValue opv, Node leftNode, Node rightNode,
                                int cx, int cy,
                                boolean isFromPermanentArr);

    public abstract void findMinAndAssign(int ci, int cj, Node leftNode, Node rightNode,
                                          OperationValue ov1, OperationEnum op1,
                                          OperationValue ov2, OperationEnum op2,
                                          OperationValue ov3, OperationEnum op3,
                                          OperationValue pov);

}
