package maindiff.xml.textdiff;

import maindiff.util.OperationEnum;

public class TextDiff {

    static class Edit {
        int indexLeft, indexRight;
        Edit prev, next;
        OperationEnum operationType;
    }

    private static final String INSERT_TAG_BEGIN = "<span style='background-color:green;display:inline-block;'>";
    private static final String DELETE_TAG_BEGIN = "<span style='background-color:red;display:inline-block;'>";
    private static final String CHANGE_TAG_BEGIN = "<span style='background-color:blue;display:inline-block;'>";
    private static final String TAG_END = "</span>";
    private static final String SPACE = " ";

    public static void textDiffTextOutput(final String[] textLeft, final String[] textRight,
                                          StringBuilder textOutputLeft, StringBuilder textOutputRight) {
        textDiffCompute(textLeft, textRight, true, textOutputLeft, textOutputRight);
    }

    public static double textDiffRatioCompute(final String[] textLeft, final String[] textRight) {
        return textDiffCompute(textLeft, textRight, false, null, null);
    }

    private static double textDiffCompute(final String[] textLeft, final String[] textRight,
                                          boolean generatingEditScript,
                                          StringBuilder textOutputLeft, StringBuilder textOutputRight) {
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
                        editScriptOutput(editScript[k], textLeft, textRight, textOutputLeft, textOutputRight);
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
                                         StringBuilder textOutputLeft, StringBuilder textOutputRight) {
        Edit head = start;
        Edit current = null;
//        StringBuilder textOutputLeft = new StringBuilder();
//        StringBuilder textOutputRight = new StringBuilder();
//        EnumMap<ResultIdentifier, StringBuilder> resultMap = new EnumMap<>(ResultIdentifier.class);
//        resultMap.put(ResultIdentifier.LEFT, textOutputLeft);
//        resultMap.put(ResultIdentifier.RIGHT, textOutputRight);
//        textOutputLeft.append("<pre>");
//        textOutputRight.append("<pre>");
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
        while (current != null) {
            Edit a, b = current;
            for (int i = previousLeft + 1;
                 current.operationType == OperationEnum.DELETE && i < current.indexLeft ||
                         current.operationType == OperationEnum.INSERT && i <= current.indexLeft; i++) {
                if (i > 0) {
                    textOutputLeft.append(SPACE);
                }
                textOutputLeft.append(textLeft[i - 1]);
                textPositionLeft++;
            }
            for (int i = previousRight + 1;
                 current.operationType == OperationEnum.INSERT && i < current.indexRight ||
                         current.operationType == OperationEnum.DELETE && i <= current.indexRight; i++) {
                if (i > 0) {
                    textOutputRight.append(SPACE);
                }
                textOutputRight.append(textRight[i - 1]);
                textPositionRight++;
            }
            if (current.operationType == OperationEnum.INSERT) {
                if (current.indexLeft - 1 > 0) {
                    textOutputLeft.append(SPACE);
                }
                textOutputLeft.append(INSERT_TAG_BEGIN);
                if (current.indexRight - 1 > 0) {
                    textOutputRight.append(SPACE);
                }
                textOutputRight.append(INSERT_TAG_BEGIN);
                boolean beginSpace = false;
                do {
                    if (beginSpace) {
                        textOutputLeft.append(SPACE);
                        textOutputRight.append(SPACE);
                    }
                    for (int i = 0; i < textRight[current.indexRight - 1].length(); i++) {
                        textOutputLeft.append(SPACE);
                    }
                    textOutputRight.append(textRight[current.indexRight - 1]);
                    textPositionRight++;
                    beginSpace = true;
                    previousLeft = current.indexLeft;
                    previousRight = current.indexRight;
                    current = current.next;
                }
                while (current != null && current.operationType == OperationEnum.INSERT && current.indexLeft == b.indexLeft);
                textOutputLeft.append(TAG_END);
                textOutputRight.append(TAG_END);
            } else {
                do {
                    a = b;
                    b = b.next;
                } while (b != null && b.operationType == OperationEnum.DELETE && b.indexLeft == a.indexLeft + 1);
                boolean change = (b != null && b.operationType == OperationEnum.INSERT && b.indexLeft == a.indexLeft);
                if (change) {
                    if (current.indexLeft - 1 > 0) {
                        textOutputLeft.append(SPACE);
                    }
                    textOutputLeft.append(CHANGE_TAG_BEGIN);
                    if (current.indexRight - 1 > 0) {
                        textOutputRight.append(SPACE);
                    }
                    textOutputRight.append(CHANGE_TAG_BEGIN);
                    boolean beginSpace = false;
                    do {
                        if (beginSpace) {
                            textOutputLeft.append(SPACE);
                        }
                        textOutputLeft.append(textLeft[current.indexLeft - 1]);
                        textPositionLeft++;
                        beginSpace = true;
                        previousLeft = current.indexLeft;
                        previousRight = current.indexRight;
                        current = current.next;
                    } while (current != b);

                    beginSpace = false;
                    do {
                        if (beginSpace) {
                            textOutputRight.append(SPACE);
                        }
                        textOutputRight.append(textRight[current.indexRight - 1]);
                        textPositionRight++;
                        beginSpace = true;
                        previousLeft = current.indexLeft;
                        previousRight = current.indexRight;
                        current = current.next;
                    }
                    while (current != null && current.operationType == OperationEnum.INSERT && current.indexLeft == b.indexLeft);
                    textOutputLeft.append(TAG_END);
                    textOutputRight.append(TAG_END);
                } else {
                    if (current.indexLeft - 1 > 0) {
                        textOutputLeft.append(SPACE);
                    }
                    textOutputLeft.append(DELETE_TAG_BEGIN);
                    if (current.indexRight - 1 > 0) {
                        textOutputRight.append(SPACE);
                    }
                    textOutputRight.append(DELETE_TAG_BEGIN);
                    boolean beginSpace = false;
                    do {
                        if (beginSpace) {
                            textOutputLeft.append(SPACE);
                            textOutputRight.append(SPACE);
                        }
                        textOutputLeft.append(textLeft[current.indexLeft - 1]);
                        textPositionLeft++;
                        for (int i = 0; i < textLeft[current.indexLeft - 1].length(); i++) {
                            textOutputRight.append(SPACE);
                        }
                        beginSpace = true;
                        previousLeft = current.indexLeft;
                        previousRight = current.indexRight;
                        current = current.next;
                    } while (current != b);
                    textOutputLeft.append(TAG_END);
                    textOutputRight.append(TAG_END);
                }
            }
        }
        for (int i = textPositionLeft; i < textLeft.length; i++) {
            if (i > 0) {
                textOutputLeft.append(SPACE);
            }
            textOutputLeft.append(textLeft[i]);
        }
        for (int i = textPositionRight; i < textRight.length; i++) {
            if (i > 0) {
                textOutputRight.append(SPACE);
            }
            textOutputRight.append(textRight[i]);
        }
//        textOutputLeft.append("</pre>");
//        textOutputRight.append("</pre>");
    }

    /**
     * TEST
     *
     * @param start
     * @param textLeft
     * @param textRight
     */
    private static void editScriptPrint(Edit start, final String[] textLeft, final String[] textRight) {
        System.out.println("indexLeft\tindexRight");
        Edit head = start;
        Edit current = null;
        while (head != null) {
            current = head;
            System.out.println(current.indexLeft + "\t" + current.indexRight);
            head = head.prev;
            if (head != null) {
                head.next = current;
            }
        }
        while (current != null) {
            Edit a;
            Edit b = current;
            if (current.operationType == OperationEnum.INSERT) {
                System.out.println("inserted after line " + current.indexLeft + ": ");
            } else {
                do {
                    a = b;
                    b = b.next;
                } while (b != null && b.operationType == OperationEnum.DELETE && b.indexLeft == a.indexLeft + 1);
                boolean change = (b != null && b.operationType == OperationEnum.INSERT && b.indexLeft == a.indexLeft);
                if (change) {
                    System.out.print("Changed ");
                } else {
                    System.out.print("Deleted ");
                }
                if (a == current) {
                    System.out.println("line " + current.indexLeft + ": ");
                } else {
                    System.out.println("lines " + current.indexLeft + "-" + a.indexLeft + ": ");
                }
                do {
                    System.out.println("\t" + textLeft[current.indexLeft - 1]);
                    current = current.next;
                } while (current != b);
                if (!change) {
                    continue;
                }
                System.out.println("To:");
            }
            do {
                System.out.println("\t" + textRight[current.indexRight - 1]);
                current = current.next;
            }
            while (current != null && current.operationType == OperationEnum.INSERT && current.indexLeft == b.indexLeft);
        }
    }

    /**
     * 输入：textLeft和textRight字符串数组
     * 输出：匹配率的百分比： 2 * lcs.length / (textLeft.length + textRight.length)
     * <p>
     * 确定匹配的字符串后，在输出结果的时候再次计算出匹配项，并生成输出结果。
     */

    /**
     * a b c a b b a
     * 1 2 3 4 5 6 7
     * c a b b a
     * c b a b b a
     * c b a b a
     * c b a b a c
     */
    public static void main(String[] args) {
        String s1 = "Usable Oxygen Volume in 4.25 Cuft Oxygen Cylinder (L" +
                "            Normal-Temperature Pressure Dry)";
        String s2 = "Usable Oxygen Volume in 4.25 Cuft Oxygen" +
                "            Cylinder (Liter Normal-Temperature Pressure Dry)";
//        System.out.println(s1);
//        System.out.println(s2);
        StringBuilder output1 = new StringBuilder();
        StringBuilder output2 = new StringBuilder();
        TextDiff.textDiffCompute(s1.split("\\s+"), s2.split("\\s+"),
                true, output1, output2);
        System.out.println(output1);
        System.out.println(output2);
    }
}
