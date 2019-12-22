package maindiff.xml.v2.output;

import maindiff.abs.work.Node;
import maindiff.util.OperationEnum;
import org.dom4j.Element;

/**
 * Created by Administrator on 2019/7/15 0015.
 */
public class OutputElementNode {
    public int id;
    public Element element;
    public OperationEnum operationType;
    public int depth;

    public OutputElementNode(Node node) {
        this.id = node.id;
        this.element = node.element;
        this.depth = node.depth;
        this.operationType = node.operationType;
    }

    public OutputElementNode(Element element) {
        this.element = element;
    }
}
