package diff;

/**
 * isFromPermanent为true时，curX与curY指向当前PathNode数组下标，以及下一个permanent节点值。
 */
public abstract class PathNode {
    OperationEnum op;
    boolean isFromPermanent;
    int curX, curY;

    public PathNode(OperationValue opv) {
        this.isFromPermanent = opv.isFromPermanentArr;
        this.curX = opv.curX;
        this.curY = opv.curY;
        this.op = opv.operation.op;
    }
}
