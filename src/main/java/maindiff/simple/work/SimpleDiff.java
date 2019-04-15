package maindiff.simple.work;

import maindiff.abs.output.Path;
import maindiff.abs.work.*;
import maindiff.simple.output.SimpleDiffOutput;
import maindiff.util.OperationEnum;
import maindiff.xml.work.XmlOperationValue;
import org.dom4j.DocumentException;

import java.io.IOException;

public class SimpleDiff extends AbstractDiff {
    @Override
    public void initialize(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new SimpleTree(leftFileName);
        rightTree = new SimpleTree(rightFileName);
        permanentArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr = new OperationValue[leftTree.size + 1][rightTree.size + 1];
        temporaryArr[0][0] = new XmlOperationValue();
        operationPaths = new Path[leftTree.size + 1][rightTree.size + 1];
    }

    @Override
    public void initialize(int left, int right) {
        temporaryArr = new SimpleOperationValue[left + 1][right + 1];
        permanentArr = new SimpleOperationValue[left + 1][right + 1];
        for (int i = 0; i <= left; i++) {
            for (int j = 0; j <= right; j++) {
                temporaryArr[i][j] = new SimpleOperationValue();
            }
        }
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, Node leftNode, Node rightNode, OperationEnum operationType) {
        return new SimpleGenericOperation(arrValue, leftNode, rightNode, operationType, false);
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, OperationValue permanentArrValue) {
        return new SimpleDerivedOperation(arrValue, (SimpleOperationValue) permanentArrValue,
                OperationEnum.UNCHANGE, true);
    }

    private void backtrace(SimpleOperationValue v) {
        if (v == null) {
            return;
        }
        System.out.println("current: " + v.curX + " " + v.curY + " " + v.value +
                ", prev: " + v.prevX + " " + v.prevY + (v.isFromPermanentArr ? " PermanentArr" : " TemporaryArr") +
                ", operationType: " + v.operationType);
        if (v == null || v.prevX == 0 && v.prevY == 0) {
            return;
        }
        if (v.operationType == OperationEnum.INSERT) {
            rightTree.nodeSequence[v.curY].op = OperationEnum.INSERT;
        } else if (v.operationType == OperationEnum.DELETE) {
            leftTree.nodeSequence[v.curX].op = OperationEnum.DELETE;
        } else if (v.operationType == OperationEnum.CHANGE) {
            leftTree.nodeSequence[v.curX].op = OperationEnum.CHANGE;
            rightTree.nodeSequence[v.curY].op = OperationEnum.CHANGE;
        }
        backtrace((SimpleOperationValue) temporaryArr[v.prevX][v.prevY]);
    }

    public static void main(String[] args) throws DocumentException, IOException {
        long beginTime = System.currentTimeMillis();
        SimpleDiff simpleDiff = new SimpleDiff();
        simpleDiff.initialize("data/left.xml", "data/right.xml");
//        xmlDiff.initialization("data/left3.xml", "data/right3.xml");
        simpleDiff.solve();
        simpleDiff.backtrace((SimpleOperationValue) simpleDiff.permanentArr[simpleDiff.leftTree.rootId][simpleDiff.rightTree.rootId]);
        long solveTime = System.currentTimeMillis();
        System.out.println((solveTime - beginTime) / 1000);
        new SimpleDiffOutput(simpleDiff.leftTree, simpleDiff.rightTree).resultOutput();
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - solveTime) / 1000);
    }
}
