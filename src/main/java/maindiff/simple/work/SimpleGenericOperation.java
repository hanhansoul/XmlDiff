package maindiff.simple.work;

import maindiff.abs.work.GenericOperation;
import maindiff.abs.work.Node;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;

public class SimpleGenericOperation extends GenericOperation {
    public int value;
    public SimpleGenericOperation(OperationValue arrValue, Node leftNode, Node rightNode, OperationEnum op, boolean isFromPermanentArr) {
        super(arrValue, leftNode, rightNode, op, isFromPermanentArr);
        this.value = -1;
    }
}
