package maindiff.abs.work;

import maindiff.util.OperationEnum;

public abstract class GenericOperation extends Operation {
    public Node leftNode;
    public Node rightNode;

    public GenericOperation(OperationValue arrValue, Node leftNode, Node rightNode, OperationEnum op, boolean isFromPermanentArr) {
        super(arrValue, op, isFromPermanentArr);
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }
}
