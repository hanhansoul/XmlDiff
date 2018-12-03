package diff;

public class SimpleOperationValue implements OperationValue {
    private int value;

    public SimpleOperationValue() {
    }

    public SimpleOperationValue(int value) {
        this.value = value;
    }

    @Override
    public OperationValue add(OperationValue opv) {
        return new SimpleOperationValue(value + ((SimpleOperationValue) opv).value);
    }

    @Override
    public int compareTo(Object o) {
        return this.value - ((SimpleOperationValue) o).value;
    }
}
