package maindiff.xml.work;

import maindiff.abs.work.Operation;
import maindiff.util.OperationEnum;

public class XmlOperation extends Operation {
    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public int elementTextDiffValue;

    public XmlOperation(OperationEnum op,
                        int elementNameDiffValue,
                        int elementAttributesDiffValue,
                        int elementTextDiffValue) {
        this.op = op;
        this.elementNameDiffValue = elementNameDiffValue;
        this.elementAttributesDiffValue = elementAttributesDiffValue;
        this.elementTextDiffValue = elementTextDiffValue;
    }
}
