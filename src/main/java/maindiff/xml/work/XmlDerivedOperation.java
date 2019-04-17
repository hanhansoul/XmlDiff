package maindiff.xml.work;

import maindiff.abs.work.DerivedOperation;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;

/**
 * Created by Administrator on 2019/4/17 0017.
 */
public class XmlDerivedOperation extends DerivedOperation {
    public XmlOperationValue permanentArrValue;

    public XmlDerivedOperation(OperationValue arrValue, XmlOperationValue permanentArrValue,
                               OperationEnum op, boolean isFromPermanentArr) {
        super(arrValue, op, isFromPermanentArr);
        this.permanentArrValue = permanentArrValue;
    }
}
