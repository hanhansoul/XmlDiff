package maindiff.simple.work;

import maindiff.abs.work.DerivedOperation;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;

public class SimpleDerivedOperation extends DerivedOperation {
    public SimpleOperationValue permanentArrValue;
    public int value;
    public SimpleDerivedOperation(OperationValue arrValue, SimpleOperationValue permanentArrValue,
                                  OperationEnum op, boolean isFromPermanentArr) {
        super(arrValue, op, isFromPermanentArr);
        this.permanentArrValue = permanentArrValue;
        this.value = -1;
    }
}
