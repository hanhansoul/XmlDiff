package diff;

import org.dom4j.Element;

import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public abstract class Node {
    public int id;
    public int fatherId;    // father.id
    public int leftMostNodeId;

    public Element element;
    public Node father;
    public List<Node> children;

    public int counterpartId;
    public OperationEnum op;

    public int depth;

    public Node() {
        element = null;
        id = leftMostNodeId = 0;
        children = null;
        father = null;
    }

    public Node(Element element, Node father) {
        this.element = element;
        this.father = father;
    }

}
