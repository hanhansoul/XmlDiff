package diff.simple;

import diff.Node;
import diff.Operation;
import diff.OperationEnum;

/**
 * Created by Administrator on 2018/12/10 0010.
 *
 * Operation表示执行的操作，用于状态转移。
 *
 * OperationValue中包含一个Operation类型成员，用于记录该状态执行的操作。
 *
 * OperationValue + Operation => OperationValue
 *
 */
public class SimpleOperation extends Operation {

    public SimpleOperation(OperationEnum op) {
        this.value = op == OperationEnum.UNCHANGE ? 0 : 1;
        this.op = op;
    }

    public SimpleOperation(int value, OperationEnum op) {
        this.value = value;
        this.op = op;
    }

}
