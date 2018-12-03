package diff;

import org.dom4j.Element;

public class SimpleOperationValue implements OperationValue {
    public int prevX;
    public int prevY;

    public int value;
    public static final SimpleOperationValue SIMPLE_OPERATION_DIFF_VALUE = new SimpleOperationValue(1);
    public static final SimpleOperationValue SIMPLE_OPERATION_EQUAL_VALUE = new SimpleOperationValue(0);

    public SimpleOperationValue() {
        value = 0;
    }

    public SimpleOperationValue(int value) {
        this.value = value;
    }

    public SimpleOperationValue(int prevX, int prevY, int value) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.value = value;
    }

//    @Override
//    public OperationValue add(OperationValue opv) {
//        return new SimpleOperationValue(value + ((SimpleOperationValue) opv).value);
////        return new SimpleOperationValue(value + ((SimpleOperationValue) opv).value);
//    }

    @Override
    public OperationValue add(int px, int py, OperationValue opv) {
        return new SimpleOperationValue(px, py, value + ((SimpleOperationValue) opv).value);
    }

    public static OperationValue opValue(Node leftNode, Node rightNode) throws OpValueElementNullException {
        if (leftNode == null && rightNode == null) {
            throw new OpValueElementNullException();
        }
        if (leftNode == null) {
            return new SimpleOperationValue(0, rightNode.id, 1);
        } else if (rightNode == null) {
            return new SimpleOperationValue(leftNode.id, 0, 1);
        }
        Element leftElement = leftNode.element;
        Element rightElement = rightNode.element;
        if (!leftElement.getName().equals(rightElement.getName())) {
            return new SimpleOperationValue(leftNode.id, rightNode.id, 1);
        } else {
            return new SimpleOperationValue(leftNode.id, rightNode.id, 0);
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
