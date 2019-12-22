package newdiff.abs.work;

public abstract class OperationValue implements Comparable<OperationValue> {

    public int prevX, prevY;
    public int curX, curY;
    public OperationMoveType operationMoveType;
    public boolean isFromPermanentArr;

    public OperationValue() {
    }

    public OperationValue(int curX, int curY) {
        this.curX = curX;
        this.curY = curY;
    }

    /**
     * thisOperationValue = operationValue
     */
    public abstract void assign(OperationValue operationValue);

    /**
     * thisOperationValue = operationValue + operationMove
     */
    public abstract void addAndAssign(OperationValue operationValue,
                                      OperationMove operationMove);

    /**
     * thisOperationValue = opt{operationValues}
     */
    public void findOptimalOperationValueAndAssign(OperationValue[] operationValues) {
        OperationValue optimalOperationValue = null;
        for (OperationValue value : operationValues) {
            if (optimalOperationValue == null || optimalOperationValue.compareTo(value) > 0) {
                optimalOperationValue = value;
            }
        }
        assign(optimalOperationValue);
    }

    public void commonAssign(OperationMove operationMove) {
        this.prevX = operationMove.prevX;
        this.prevY = operationMove.prevY;
        this.operationMoveType = operationMove.operationMoveType;
        this.isFromPermanentArr = operationMove.isFromPermanentArr;
    }

    public void commonAssign(OperationValue operationValue) {
        this.prevX = operationValue.prevX;
        this.prevY = operationValue.prevY;
        this.operationMoveType = operationValue.operationMoveType;
        this.isFromPermanentArr = operationValue.isFromPermanentArr;
    }
}
