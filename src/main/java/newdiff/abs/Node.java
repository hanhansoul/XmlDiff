package newdiff.abs;

import org.dom4j.Element;

import java.util.List;

public abstract class Node {
    public int id;

    public List<Node> children;

    public int leftMostNodeId;

    public OperationMoveType operationMoveType;

    public int depth;

    public Element element;

    public Node() {
    }

    public Node(Element element) {
        this.element = element;
    }
}
