package newdiff.abs.output;

import newdiff.abs.work.OperationMoveType;
import newdiff.abs.work.OperationValue;

public class PathNode {
    public OperationMoveType operationMoveType;
    public boolean isFromPermanent;
    public int curX, curY;

    public PathNode(OperationValue operationValue) {
        this.curX = operationValue.curX;
        this.curY = operationValue.curY;
        this.isFromPermanent = operationValue.isFromPermanentArr;
        this.operationMoveType = operationValue.operationMoveType;
    }
}
