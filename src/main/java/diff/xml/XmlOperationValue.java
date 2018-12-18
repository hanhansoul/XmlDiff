package diff.xml;

import diff.Node;
import diff.OpValueElementNullException;
import diff.Operation;
import diff.OperationValue;
import org.dom4j.Element;

import java.util.Map;

public class XmlOperationValue extends OperationValue {
    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public int elementTextDiffValue;

    public static final XmlOperationValue ELEMENT_ZERO_DIFF_VAL = new XmlOperationValue(0, 0, 0);
    public static final XmlOperationValue ELEMENT_NAME_DIFF_VAL = new XmlOperationValue(1, 0, 0);
    public static final XmlOperationValue ELEMENT_ATTRIBUTE_DIFF_VAL = new XmlOperationValue(0, 1, 0);
    public static final XmlOperationValue ELEMENT_TEXT_DIFF_VAL = new XmlOperationValue(0, 0, 1);

    public XmlOperationValue() {
        this.elementNameDiffValue = 0;
        this.elementAttributesDiffValue = 0;
        this.elementTextDiffValue = 0;
    }

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

    public static OperationValue opValue(XmlNode leftNode, XmlNode rightNode) throws OpValueElementNullException {
        // TODO
        Element leftElement = leftNode.element;
        Element rightElement = rightNode.element;
        if (leftElement == null || rightElement == null) {
            throw new OpValueElementNullException();
        }
        if (!leftElement.getName().equals(rightElement.getName())) {
            return ELEMENT_NAME_DIFF_VAL;
        } else {
            int attributeDiffValue = 0;
            Map<String, String> leftAttributeMap = leftNode.attributesMap;
            Map<String, String> rightAttributeMap = rightNode.attributesMap;
            if (leftAttributeMap.size() < rightAttributeMap.size()) {
                for (Map.Entry entry : leftAttributeMap.entrySet()) {
                    if (!(rightAttributeMap.containsKey(entry.getKey()) &&
                            rightAttributeMap.get(entry.getKey()).equals(entry.getValue()))) {
                        attributeDiffValue++;
                    }
                }
            } else {
                for (Map.Entry entry : rightAttributeMap.entrySet()) {
                    if (!(leftAttributeMap.containsKey(entry.getKey()) &&
                            leftAttributeMap.get(entry.getKey()).equals(entry.getValue()))) {
                        attributeDiffValue++;
                    }
                }
            }

            String[] leftText = leftNode.text;
            String[] rightText = rightNode.text;
            int[][] D = new int[leftText.length + 1][rightText.length + 1];
            D[0][0] = 0;
            for (int i = 1; i < leftText.length; i++) {
                for (int j = 1; j < rightText.length; j++) {
                    if (leftText[i].equals(rightText[j])) {
                        D[i][j] = D[i - 1][j - 1] + 1;
                    } else {
                        D[i][j] = Math.max(D[i - 1][j], D[i][j - 1]);
                    }
                }
            }
            int textDiffValue = leftText.length + rightText.length - 2 * D[leftText.length - 1][leftText.length - 1];

            return new XmlOperationValue(0, attributeDiffValue, textDiffValue);
        }
    }

//    @Override
//    public OperationValue add(int px, int py, OperationValue opv) {
//        XmlOperationValue value = (XmlOperationValue) opv;
//        return new XmlOperationValue(this.elementNameDiffValue + value.elementNameDiffValue,
//                this.elementAttributesDiffValue + value.elementAttributesDiffValue,
//                this.elementTextDiffValue + value.elementTextDiffValue);
//    }

    @Override
    public OperationValue add(Operation op, int cx, int cy, boolean isFromPermanentArr) {
        return null;
    }

    @Override
    public OperationValue add(OperationValue opv, int cx, int cy, boolean isFromPermanentArr) {
        return null;
    }
}
