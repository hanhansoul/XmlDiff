package diff;

public interface OperationValue extends Comparable {
    OperationValue add(Operation op, int cx, int cy);

    OperationValue add(OperationValue opv, int cx, int cy);
}
