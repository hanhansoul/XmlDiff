package newdiff.abs;

public abstract class OperationValue {

    public int prevX, prevY;
    public int curX, curY;
    public OperationMoveType operationMoveType;
    public boolean isFromPermanentArr;

    /**
     * thisOperationValue = operationValue
     */
    public abstract void assign(OperationValue operationValue);

    /**
     * thisOperationValue = operationValue + operationMove
     */
    public abstract void addAndAssign(OperationValue operationValue, OperationMove operationMove, int cx, int cy);

    /**
     * thisOperationValue = opt{operationValues}
     */
    public abstract void findOptimalOperationValueAndAssign(OperationValue[] operationValues, int cx, int cy);

    public void commonAssign() {

    }
}
