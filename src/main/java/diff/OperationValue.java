package diff;

import org.dom4j.Element;

public class OperationValue implements Comparable {
    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public int elementTextDiffValue;

    public OperationValue() {
    }

    public OperationValue(int elementNameDiffValue, int elementAttributesDiffValue, int elementTextDiffValue) {
        this.elementNameDiffValue = elementNameDiffValue;
        this.elementAttributesDiffValue = elementAttributesDiffValue;
        this.elementTextDiffValue = elementTextDiffValue;
    }

    @Override
    public int compareTo(Object o) {
        OperationValue opv = (OperationValue) o;
        if (this.elementNameDiffValue != opv.elementNameDiffValue) {
            return this.elementNameDiffValue - opv.elementNameDiffValue;
        } else if (this.elementAttributesDiffValue != opv.elementAttributesDiffValue) {
            return this.elementAttributesDiffValue - opv.elementAttributesDiffValue;
        } else {
            return this.elementTextDiffValue - opv.elementTextDiffValue;
        }
    }

    public OperationValue add(OperationValue opv) {
        return new OperationValue(this.elementNameDiffValue + opv.elementNameDiffValue,
                this.elementAttributesDiffValue + opv.elementAttributesDiffValue,
                this.elementTextDiffValue + opv.elementTextDiffValue);
    }
}
