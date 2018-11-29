package diff;

public class OperationValue implements Comparable {
    public int elementNameDiffValue;
    public int elementAttributesDiffValue;
    public int elementTextDiffValue;

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
