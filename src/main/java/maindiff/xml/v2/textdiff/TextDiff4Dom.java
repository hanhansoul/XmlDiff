package maindiff.xml.v2.textdiff;

import maindiff.util.OperationEnum;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static maindiff.xml.v2.textdiff.TextDiff4DomHelper.*;

public class TextDiff4Dom {

    static class Edit {
        int indexLeft, indexRight;
        Edit prev, next;
        OperationEnum operationType;
    }

    //    private static final String INSERT_TAG_BEGIN = "<span style='background-color:green;display:inline-block;'>";
//    private static final String DELETE_TAG_BEGIN = "<span style='background-color:red;display:inline-block;'>";
//    private static final String CHANGE_TAG_BEGIN = "<span style='background-color:blue;display:inline-block;'>";
//    private static final String TAG_END = "</span>";

    private static double textDiffCompute(final String[] textLeft, final String[] textRight,
                                          boolean generatingEditScript,
                                          Element elementOutputLeft, Element elementOutputRight) {
        int lengthLeft = textLeft.length;
        int lengthRight = textRight.length;
        int MAX_LINES, ORIGIN;
        Edit[] editScript = null;
        int row, col;

        ORIGIN = MAX_LINES = Math.max(lengthLeft, lengthRight);
        if (generatingEditScript) {
            editScript = new Edit[MAX_LINES * 2 + 1];
            editScript[ORIGIN] = null;
        }
        for (row = 0; row < lengthLeft && row < lengthRight && textLeft[row].equals(textRight[row]); row++)
            ;
        int[] lastD = new int[MAX_LINES * 2 + 1];
        lastD[ORIGIN] = row;
        int maxD = 2 * MAX_LINES;
        int lower = row == lengthLeft ? ORIGIN + 1 : ORIGIN - 1;
        int upper = row == lengthRight ? ORIGIN - 1 : ORIGIN + 1;

        if (lower > upper) {
//            if (generatingEditScript) {
//                editScriptOutput(null, textLeft, textRight, textOutputLeft, textOutputRight);
//            }
            return 1;
        }

        for (int d = 1; d < maxD; d++) {
            for (int k = lower; k <= upper; k += 2) {
                Edit edit = new Edit();
                if (k == ORIGIN - d || k != ORIGIN + d && lastD[k + 1] >= lastD[k - 1]) {
                    row = lastD[k + 1] + 1;
                    if (generatingEditScript) {
                        edit.prev = editScript[k + 1];
                        edit.operationType = OperationEnum.DELETE;
                    }
                } else {
                    row = lastD[k - 1];
                    if (generatingEditScript) {
                        edit.prev = editScript[k - 1];
                        edit.operationType = OperationEnum.INSERT;
                    }
                }

                col = row + k - ORIGIN;
                if (generatingEditScript) {
                    edit.indexLeft = row;
                    edit.indexRight = col;
                    editScript[k] = edit;
                }

                for (; row < lengthLeft && col < lengthRight && textLeft[row].equals(textRight[col]); row++, col++)
                    ;
                lastD[k] = row;

                if (row == lengthLeft && col == lengthRight) {
                    if (generatingEditScript) {
                        editScriptOutput(editScript[k], textLeft, textRight, elementOutputLeft, elementOutputRight);
                    }
                    return 1.0 * (lengthLeft + lengthRight - d) / (lengthLeft + lengthRight);
                }
                if (row == lengthLeft) {
                    lower = k + 2;
                }
                if (col == lengthRight) {
                    upper = k - 2;
                }
            }
            lower--;
            upper++;
        }
        return 0;
    }


    private static void editScriptOutput(Edit start, final String[] textLeft, final String[] textRight,
                                         Element elementOutputLeft, Element elementOutputRight) {
        Edit head = start;
        Edit current = null;
        while (head != null) {
            current = head;
            head = head.prev;
            if (head != null) {
                head.next = current;
            }
        }
        int previousLeft, previousRight;
        previousLeft = previousRight = 0;
        int textPositionLeft = 0;
        int textPositionRight = 0;
        StringBuilder sbLeft, sbRight;
        Element elementLeft, elementRight;

        while (current != null) {
            Edit a, b = current;

            sbLeft = new StringBuilder();
            for (int i = previousLeft + 1;
                 current.operationType == OperationEnum.DELETE && i < current.indexLeft ||
                         current.operationType == OperationEnum.INSERT && i <= current.indexLeft; i++) {
                if (i > 0) {
                    sbLeft.append(SPACE);
                }
                sbLeft.append(textLeft[i - 1]);
                textPositionLeft++;
            }
            elementOutputLeft.addText(sbLeft.toString());

            sbRight = new StringBuilder();
            for (int i = previousRight + 1;
                 current.operationType == OperationEnum.INSERT && i < current.indexRight ||
                         current.operationType == OperationEnum.DELETE && i <= current.indexRight; i++) {
                if (i > 0) {
                    sbRight.append(SPACE);
                }
                sbRight.append(textRight[i - 1]);
                textPositionRight++;
            }
            elementOutputRight.addText(sbRight.toString());

            if (current.operationType == OperationEnum.INSERT) {

                if (current.indexLeft - 1 > 0) {
                    elementOutputLeft.addText(SPACE);
                }
                elementLeft = addInsertSpanElement(elementOutputLeft);

                if (current.indexRight - 1 > 0) {
                    elementOutputRight.addText(SPACE);
                }
                elementRight = addInsertSpanElement(elementOutputRight);

                boolean beginSpace = false;
                sbLeft = new StringBuilder();
                sbRight = new StringBuilder();
                do {
                    if (beginSpace) {
                        sbLeft.append(SPACE);
                        sbRight.append(SPACE);
                    }
//                    // 插入操作，left补齐空白项
//                    for (int i = 0; i < textRight[current.indexRight - 1].length(); i++) {
//                        sbLeft.append(SPACE);
//                    }
                    sbRight.append(textRight[current.indexRight - 1]);
                    textPositionRight++;
                    beginSpace = true;
                    previousLeft = current.indexLeft;
                    previousRight = current.indexRight;
                    current = current.next;
                }
                while (current != null && current.operationType == OperationEnum.INSERT && current.indexLeft == b.indexLeft);

                elementLeft.addText(sbLeft.toString());
                elementRight.addText(sbRight.toString());
            } else {
                do {
                    a = b;
                    b = b.next;
                } while (b != null && b.operationType == OperationEnum.DELETE && b.indexLeft == a.indexLeft + 1);
                boolean change = (b != null && b.operationType == OperationEnum.INSERT && b.indexLeft == a.indexLeft);
                if (change) {

                    if (current.indexLeft - 1 > 0) {
                        elementOutputLeft.addText(SPACE);
                    }
                    elementLeft = addChangeSpanElement(elementOutputLeft);

                    if (current.indexRight - 1 > 0) {
                        elementOutputRight.addText(SPACE);
                    }
                    elementRight = addChangeSpanElement(elementOutputRight);

                    boolean beginSpace = false;
                    sbLeft = new StringBuilder();
                    do {
                        if (beginSpace) {
                            sbLeft.append(SPACE);
                        }
                        sbLeft.append(textLeft[current.indexLeft - 1]);
                        textPositionLeft++;
                        beginSpace = true;
                        current = current.next;
                    } while (current != b);

                    beginSpace = false;
                    sbRight = new StringBuilder();
                    do {
                        if (beginSpace) {
                            sbRight.append(SPACE);
                        }
                        sbRight.append(textRight[current.indexRight - 1]);
                        textPositionRight++;
                        beginSpace = true;
                        previousLeft = current.indexLeft;
                        previousRight = current.indexRight;
                        current = current.next;
                    }
                    while (current != null && current.operationType == OperationEnum.INSERT && current.indexLeft == b.indexLeft);
                    elementLeft.addText(sbLeft.toString());
                    elementRight.addText(sbRight.toString());
                } else {
                    if (current.indexLeft - 1 > 0) {
                        elementOutputLeft.addText(SPACE);
                    }
                    elementLeft = addDeleteSpanElement(elementOutputLeft);

                    if (current.indexRight - 1 > 0) {
                        elementOutputRight.addText(SPACE);
                    }
                    elementRight = addDeleteSpanElement(elementOutputRight);

                    boolean beginSpace = false;
                    sbLeft = new StringBuilder();
                    sbRight = new StringBuilder();
                    do {
                        if (beginSpace) {
                            sbLeft.append(SPACE);
                            sbRight.append(SPACE);
                        }
                        sbLeft.append(textLeft[current.indexLeft - 1]);
                        textPositionLeft++;
                        for (int i = 0; i < textLeft[current.indexLeft - 1].length(); i++) {
                            sbRight.append(SPACE);
                        }
                        beginSpace = true;
                        previousLeft = current.indexLeft;
                        previousRight = current.indexRight;
                        current = current.next;
                    } while (current != b);
                    elementLeft.addText(sbLeft.toString());
                    elementRight.addText(sbRight.toString());
                }
            }
        }
        sbLeft = new StringBuilder();
        sbRight = new StringBuilder();
        for (int i = textPositionLeft; i < textLeft.length; i++) {
            if (i > 0) {
                sbLeft.append(SPACE);
            }
            sbLeft.append(textLeft[i]);
        }
        for (int i = textPositionRight; i < textRight.length; i++) {
            if (i > 0) {
                sbRight.append(SPACE);
            }
            sbRight.append(textRight[i]);
        }
        elementOutputLeft.addText(sbLeft.toString());
        elementOutputRight.addText(sbRight.toString());
    }

    public static void main(String[] args) throws IOException {
        String s1 = "Usable Oxygen Volume in 4.25 Cuft Oxygen Cylinder (L" +
                "            Normal-Temperature Pressure Dry)";
        String s2 = "Usable Oxygen Volumes in 4.55 Cuft Oxygen OZZZZZ" +
                "            Cylinder (Liter Normal-Temperature Dry)";
//        String s1 = "You can find the applicable pressure related to the local regulations in the" +
//                "            chart \"Filling Ratio as a Function of Portable Cylinder Pressure and Temperature" +
//                "            (Nominal Portable Cylinder Pressure 1850 psig)”.";
//        String s2 = "You can find the applicable pressure related to the local regulations in the" +
//                "            chart \"Filling Ratio as a Function of Portable Cylinder Pressure and Temperature" +
//                "            (Nominal Portable Cylinder Pressure 1850 psig)”.";
//        System.out.println(s1);
//        System.out.println(s2);
        Element output1 = DocumentHelper.createElement("root");
        Element output2 = DocumentHelper.createElement("root");
        TextDiff4Dom.textDiffCompute(s1.split("\\s+"), s2.split("\\s+"),
                true, output1, output2);

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file = new File("data/dom4j/diff1.html");
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(output1);
        writer.close();

        file = new File("data/dom4j/diff2.html");
        writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(output2);
        writer.close();
    }

}
