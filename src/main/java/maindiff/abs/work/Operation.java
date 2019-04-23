package maindiff.abs.work;

import maindiff.util.OperationEnum;

/**
 * 表示单次操作
 */
public abstract class Operation {
    public OperationValue arrValue;
    public OperationEnum operationType;
    public boolean isFromPermanentArr;

    public Operation(OperationValue arrValue, OperationEnum operationType, boolean isFromPermanentArr) {
        this.arrValue = arrValue;
        this.operationType = operationType;
        this.isFromPermanentArr = isFromPermanentArr;
    }

}
