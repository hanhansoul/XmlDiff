package diff;

import org.dom4j.Element;

/**
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
 * 2. opValue()：给定两个节点，返回一个操作类型
 * 3. compareTo()：比较两个OperationValue对象值大小，以便递推时取出最小值
 */
public class SimpleOperationValue implements OperationValue {
    public int value;
    public int prevX, prevY;
    public int curX, curY;
    public Operation operation;

    static class SimpleOperation extends Operation {

        public SimpleOperation(OperationEnum op, Node opFirstNode, Node opSecondNode) {
            this.value = op == OperationEnum.UNCHANGE ? 0 : 1;
            this.op = op;
            this.opFirstNode = opFirstNode;
            this.opSecondNode = opSecondNode;
        }

        public SimpleOperation(int value, OperationEnum op, Node opFirstNode, Node opSecondNode) {
            this.value = value;
            this.op = op;
            this.opFirstNode = opFirstNode;
            this.opSecondNode = opSecondNode;
        }
    }

    public SimpleOperationValue(int value, int prevX, int prevY, int curX, int curY,
                                Operation operation) {
        this.value = value;
        this.prevX = prevX;
        this.prevY = prevY;
        this.curX = curX;
        this.curY = curY;
        this.operation = operation;
    }

    public SimpleOperationValue() {
        prevX = prevY = 0;
        curX = curY = 0;
        value = 0;
        operation = null;
    }

    @Override
    public OperationValue add(Operation op, int cx, int cy) {
        return new SimpleOperationValue(value + op.value, curX, curY, cx, cy, op);
    }

    @Override
    public OperationValue add(OperationValue opv, int cx, int cy) {
        SimpleOperationValue sopv = (SimpleOperationValue) opv;
        return add(new SimpleOperation(sopv.value, OperationEnum.UNCHANGE, null, null), cx, cy);
    }

    @Override
    public int compareTo(Object o) {
        return this.value - ((SimpleOperationValue) o).value;
    }

    public static Operation opValue(Node leftNode, Node rightNode) throws OpValueElementNullException {
        if (leftNode == null && rightNode == null) {
            throw new OpValueElementNullException();
        }
        if (leftNode == null) {
            return new SimpleOperation(OperationEnum.INSERT, null, rightNode);
        } else if (rightNode == null) {
            return new SimpleOperation(OperationEnum.DELETE, leftNode, null);
        }
        Element leftElement = leftNode.element;
        Element rightElement = rightNode.element;
        if (!leftElement.getName().equals(rightElement.getName())) {
            return new SimpleOperation(OperationEnum.CHANGE, leftNode, rightNode);
        } else {
            return new SimpleOperation(OperationEnum.UNCHANGE, null, null);
        }
    }

}
