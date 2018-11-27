package diff;

import org.dom4j.Element;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class Node {
    public int id;
    private Element element;
    public int leftMostNode;
    public Operation operation;

    public Node(Element element) {
        this.element = element;
        leftMostNode = 0;
        operation = null;
    }

}
