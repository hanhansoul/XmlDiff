package diff;

public abstract class OperationValue implements Comparable {
    public int prevX, prevY;
    public int curX, curY;
    public Operation operation;

    public abstract OperationValue add(Operation op, int cx, int cy);

    public abstract OperationValue add(OperationValue opv, int cx, int cy);
}
