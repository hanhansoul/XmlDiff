package maindiff.abs.output;


import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;

public abstract class PathNode {
    public OperationEnum op;
    public boolean isFromPermanent;
    public int curX, curY;

    public PathNode(OperationValue opv) {
        this.isFromPermanent = opv.isFromPermanentArr;
        this.curX = opv.curX;
        this.curY = opv.curY;
        this.op = opv.operationType;
    }
}
