package maindiff.xml.work;

import maindiff.abs.output.Path;
import maindiff.abs.work.*;
import maindiff.util.OpValueElementNullException;
import maindiff.util.OperationEnum;
import org.dom4j.DocumentException;

// TODO XmlDiff
public class XmlDiff extends AbstractDiff {

    @Override
    public Operation generateOperation(OperationValue arrValue, Node leftNode, Node rightNode,
                                       OperationEnum operationType) {
        return null;
    }

    @Override
    public Operation generateOperation(OperationValue arrValue, OperationValue permanentArrValue) {
        return null;
    }

    @Override
    public void initialize(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new XmlTree(leftFileName);
        rightTree = new XmlTree(rightFileName);
        int left = leftTree.size;
        int right = rightTree.size;
        permanentArr = new XmlOperationValue[left + 1][right + 1];
        temporaryArr = new XmlOperationValue[left + 1][right + 1];
        for (int i = 0; i <= left; i++) {
            for (int j = 0; j <= right; j++) {
                temporaryArr[i][j] = new XmlOperationValue();
                permanentArr[i][j] = new XmlOperationValue();
            }
        }
        operationPaths = new Path[left + 1][right + 1];
    }

}
