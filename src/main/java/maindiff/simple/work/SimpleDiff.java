package maindiff.simple.work;

import maindiff.abs.output.Path;
import maindiff.abs.work.AbstractDiff;
import maindiff.abs.work.Node;
import maindiff.abs.work.Operation;
import maindiff.abs.work.OperationValue;
import maindiff.simple.output.SimpleDiffOutput;
import maindiff.util.OperationEnum;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.IOException;

public class SimpleDiff extends AbstractDiff {


    @Override
    public void initialize(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new SimpleTree(leftFileName);
        rightTree = new SimpleTree(rightFileName);
        int left = leftTree.size;
        int right = rightTree.size;
        temporaryArr = new SimpleOperationValue[left + 1][right + 1];
        permanentArr = new SimpleOperationValue[left + 1][right + 1];
        for (int i = 0; i <= left; i++) {
            for (int j = 0; j <= right; j++) {
                temporaryArr[i][j] = new SimpleOperationValue();
                permanentArr[i][j] = new SimpleOperationValue();
            }
        }
        operationPaths = new Path[left + 1][right + 1];
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, Node leftNode, Node rightNode,
                                       OperationEnum operationType) {
        if (operationType == null) {
            if (leftNode == null) {
                operationType = OperationEnum.INSERT;
            } else if (rightNode == null) {
                operationType = OperationEnum.DELETE;
            } else {
                Element leftElement = leftNode.element;
                Element rightElement = rightNode.element;
                if (!leftElement.getName().equals(rightElement.getName())) {
                    operationType = OperationEnum.CHANGE;
                } else {
                    operationType = OperationEnum.UNCHANGE;
                }
            }
        }
        SimpleGenericOperation sgop = new SimpleGenericOperation(arrValue, leftNode, rightNode,
                operationType, false);
        sgop.value = ((SimpleOperationValue) arrValue).value + (operationType == OperationEnum.UNCHANGE ? 0 : 1);
        return sgop;
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, OperationValue permanentArrValue) {
        SimpleDerivedOperation sdop = new SimpleDerivedOperation(arrValue, (SimpleOperationValue) permanentArrValue,
                OperationEnum.UNCHANGE, true);
        sdop.value = ((SimpleOperationValue) arrValue).value + ((SimpleOperationValue) permanentArrValue).value;
        return sdop;
    }

    public static void main(String[] args) throws DocumentException, IOException {
        long beginTime = System.currentTimeMillis();
        SimpleDiff simpleDiff = new SimpleDiff();
//        simpleDiff.initialize("data/CSC1.xml", "data/CSC2.xml");
        simpleDiff.initialize("data/left3.xml", "data/right3.xml");
        simpleDiff.solve();
        long solveTime = System.currentTimeMillis();
        System.out.println((solveTime - beginTime) / 1000);
        new SimpleDiffOutput(simpleDiff.leftTree, simpleDiff.rightTree).resultOutput();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - solveTime) / 1000);
    }
}
