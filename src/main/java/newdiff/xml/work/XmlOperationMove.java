package newdiff.xml.work;

import newdiff.abs.work.OperationMove;
import newdiff.abs.work.OperationMoveType;

public class XmlOperationMove extends OperationMove {

    private static XmlOperationMove INSTANCE = null;

    public long xmlOperationTagNameDiffValue;
    public long xmlOperationAttributeDiffValue;
    public double xmlOperationTextDiffValue;

    private XmlOperationMove() {
        xmlOperationTagNameDiffValue = 0;
        xmlOperationAttributeDiffValue = 0;
        xmlOperationTextDiffValue = 0;
    }

    public static XmlOperationMove getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XmlOperationMove();
        }
        return INSTANCE;
    }

    public static XmlOperationMove updateInstance(int prevX,
                                                  int prevY,
                                                  OperationMoveType operationMoveType,
                                                  boolean isFromPermanentArr,
                                                  long xmlOperationTagNameDiffValue,
                                                  long xmlOperationAttributeDiffValue,
                                                  double xmlOperationTextDiffValue) {
        XmlOperationMove xmlOperationMove = getInstance();
        xmlOperationMove.updateOperationMove(prevX, prevY, operationMoveType, isFromPermanentArr);
        xmlOperationMove.xmlOperationTagNameDiffValue = xmlOperationTagNameDiffValue;
        xmlOperationMove.xmlOperationAttributeDiffValue = xmlOperationAttributeDiffValue;
        xmlOperationMove.xmlOperationTextDiffValue = xmlOperationTextDiffValue;
        return xmlOperationMove;
    }
}
