package newdiff.abs.work;

public abstract class OperationMove {

    public int prevX;
    public int prevY;
    public OperationMoveType operationMoveType;
    public boolean isFromPermanentArr;

    protected OperationMove() {
    }

    protected void updateOperationMove(int prevX,
                                       int prevY,
                                       OperationMoveType operationMoveType,
                                       boolean isFromPermanentArr) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.operationMoveType = operationMoveType;
        this.isFromPermanentArr = isFromPermanentArr;
    }
}
