package diff;

import org.dom4j.Element;

public class SimpleOperationValue implements OperationValue {
    public int prevX;
    public int prevY;
    public OperationEnum op;
    public Node opFirstNode, opSecondNode;

    public int value;
//    public static final SimpleOperationValue SIMPLE_OPERATION_DIFF_VALUE = new SimpleOperationValue(1);
//    public static final SimpleOperationValue SIMPLE_OPERATION_EQUAL_VALUE = new SimpleOperationValue(0);

    public SimpleOperationValue() {
        this.value = 0;
        this.prevX = 0;
        this.prevY = 0;
        this.op = OperationEnum.UNCHANGE;
    }

    public SimpleOperationValue(int value) {
        this.value = value;
        this.prevX = 0;
        this.prevY = 0;
        this.op = OperationEnum.UNCHANGE;
    }

//    public SimpleOperationValue(int prevX, int prevY, int value) {
//        this.prevX = prevX;
//        this.prevY = prevY;
//        this.value = value;
//    }

    public SimpleOperationValue(int prevX, int prevY, OperationEnum op, int value) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.op = op;
        this.value = value;
    }

    /**
     * 执行了op操作，操作变化的节点为opFirstNode与opSecondNode。
     */
    public SimpleOperationValue(Node opFirstNode, Node opSecondNode, OperationEnum op, int value) {
        this.prevX = 0;
        this.prevY = 0;
        this.op = op;
        this.opFirstNode = opFirstNode;
        this.opSecondNode = opSecondNode;
        this.value = value;
    }

    //    @Override
//    public OperationValue add(OperationValue opv) {
//        return new SimpleOperationValue(value + ((SimpleOperationValue) opv).value);
////        return new SimpleOperationValue(value + ((SimpleOperationValue) opv).value);
//    }

    @Override
    public OperationValue add(int px, int py, OperationValue opv) {
        return new SimpleOperationValue(px, py, ((SimpleOperationValue) opv).op, value + ((SimpleOperationValue) opv).value);
    }

    public static OperationValue opValue(Node leftNode, Node rightNode) throws OpValueElementNullException {
        if (leftNode == null && rightNode == null) {
            throw new OpValueElementNullException();
        }
        if (leftNode == null) {
            return new SimpleOperationValue(null, rightNode, OperationEnum.INSERT, 1);
        } else if (rightNode == null) {
            return new SimpleOperationValue(leftNode, null, OperationEnum.DELETE, 1);
        }
        Element leftElement = leftNode.element;
        Element rightElement = rightNode.element;
        if (!leftElement.getName().equals(rightElement.getName())) {
            return new SimpleOperationValue(leftNode, rightNode, OperationEnum.CHANGE, 1);
        } else {
            return new SimpleOperationValue(null, null, OperationEnum.UNCHANGE, 0);
        }
//        if (!leftElement.getName().equals(rightElement.getName()) ||
//                leftNode.attributesMap.size() != rightNode.attributesMap.size() ||
//                !leftElement.getText().equals(rightElement.getText())) {
//            return SIMPLE_OPERATION_DIFF_VALUE;
//        } else {
//            for (Map.Entry entry : leftNode.attributesMap.entrySet()) {
//                if (!rightNode.attributesMap.containsKey(entry.getKey()) ||
//                        !rightNode.attributesMap.get(entry.getKey()).equals(entry.getValue())) {
//                    return SIMPLE_OPERATION_DIFF_VALUE;
//                }
//            }
//        }
//        return SIMPLE_OPERATION_EQUAL_VALUE;
    }

    @Override
    public int compareTo(Object o) {
        return this.value - ((SimpleOperationValue) o).value;
    }
}
