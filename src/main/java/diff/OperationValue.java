package diff;

public abstract class OperationValue implements Comparable {
    public int prevX, prevY;
    public boolean isFromPermanentArr;
    public int curX, curY;
    public Operation operation;

    public OperationValue(int prevX, int prevY, boolean isFromPermanentArr, int curX, int curY, Operation operation) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.isFromPermanentArr = isFromPermanentArr;
        this.curX = curX;
        this.curY = curY;
        this.operation = operation;
    }

    public OperationValue() {
        prevX = prevY = 0;
        curX = curY = 0;
        operation = null;
        isFromPermanentArr = false;
    }

    public abstract OperationValue add(Operation op, int cx, int cy, boolean isFromPermanentArr);

    public abstract OperationValue add(OperationValue opv, int cx, int cy, boolean isFromPermanentArr);
}
