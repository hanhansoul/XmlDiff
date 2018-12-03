package diff;

public interface OperationValue extends Comparable {
    OperationValue add(int px, int py, OperationValue opv);
}
