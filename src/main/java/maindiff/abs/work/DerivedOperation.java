package maindiff.abs.work;

import maindiff.util.OperationEnum;

public abstract class DerivedOperation extends Operation {
    public DerivedOperation(OperationValue arrValue, OperationEnum op, boolean isFromPermanentArr) {
        super(arrValue, op, isFromPermanentArr);
    }
}
