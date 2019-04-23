package maindiff.xml.work;

import maindiff.abs.work.GenericOperation;
import maindiff.abs.work.Node;
import maindiff.abs.work.OperationValue;
import maindiff.util.OperationEnum;

/**
 * Created by Administrator on 2019/4/17 0017.
 */
// TODO XmlGenericOperation
public class XmlGenericOperation extends GenericOperation {

    public int xmlOperationTagNameDiffValue;
    public int xmlOperationAttributeDiffValue;
    public int xmlOperationTextDiffValue;

    public XmlGenericOperation(OperationValue arrValue, Node leftNode, Node rightNode,
                               OperationEnum op, boolean isFromPermanentArr) {
        super(arrValue, leftNode, rightNode, op, isFromPermanentArr);
    }
}
