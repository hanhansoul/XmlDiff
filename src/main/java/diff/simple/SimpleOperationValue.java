package diff.simple;

import diff.*;
import org.dom4j.Element;

public class SimpleOperationValue extends OperationValue {

    public int value;

    public SimpleOperationValue(
            int value, int prevX, int prevY, int curX, int curY,
            Operation operation, boolean isFromPermanentArr) {
        super(prevX, prevY, isFromPermanentArr, curX, curY, operation);
        this.value = value;
    }

    public SimpleOperationValue() {
        super();
        value = 0;
    }

    @Override
    public OperationValue add(Operation op, int cx, int cy, boolean isFromPermanentArr) {
        return new SimpleOperationValue(value + op.value, curX, curY, cx, cy, op, isFromPermanentArr);
    }

    @Override
    public OperationValue add(OperationValue opv, int cx, int cy, boolean isFromPermanentArr) {
        SimpleOperationValue sopv = (SimpleOperationValue) opv;
        return add(new SimpleOperation(sopv.value, OperationEnum.UNCHANGE), cx, cy, isFromPermanentArr);
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
            return new SimpleOperation(OperationEnum.INSERT);
        } else if (rightNode == null) {
            return new SimpleOperation(OperationEnum.DELETE);
        }
        Element leftElement = leftNode.element;
        Element rightElement = rightNode.element;
        if (!leftElement.getName().equals(rightElement.getName())) {
            return new SimpleOperation(OperationEnum.CHANGE);
        } else {
            return new SimpleOperation(OperationEnum.UNCHANGE);
        }
    }

}
