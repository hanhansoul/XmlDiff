package diff;

public class XmlOperationValue implements OperationValue {
    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public int elementTextDiffValue;

    public XmlOperationValue(int elementNameDiffValue, int elementAttributesDiffValue, int elementTextDiffValue) {
        this.elementNameDiffValue = elementNameDiffValue;
        this.elementAttributesDiffValue = elementAttributesDiffValue;
        this.elementTextDiffValue = elementTextDiffValue;
    }

    @Override
    public int compareTo(Object o) {
        XmlOperationValue opv = (XmlOperationValue) o;
        if (this.elementNameDiffValue != opv.elementNameDiffValue) {
            return this.elementNameDiffValue - opv.elementNameDiffValue;
        } else if (this.elementAttributesDiffValue != opv.elementAttributesDiffValue) {
            return this.elementAttributesDiffValue - opv.elementAttributesDiffValue;
        } else {
            return this.elementTextDiffValue - opv.elementTextDiffValue;
        }
    }

    @Override
    public OperationValue add(OperationValue opv) {
        XmlOperationValue value = (XmlOperationValue) opv;
        return new XmlOperationValue(this.elementNameDiffValue + value.elementNameDiffValue,
                this.elementAttributesDiffValue + value.elementAttributesDiffValue,
                this.elementTextDiffValue + value.elementTextDiffValue);
    }
}
