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

    public Node() {
        element = null;
        id = leftMostNode = 0;
        operation = null;
    }

    public Node(int id, Element element, int leftMostNode) {
        this.id = id;
        this.element = element;
        this.leftMostNode = leftMostNode;
    }

    public Element getElement() {
        return element;
    }
}
