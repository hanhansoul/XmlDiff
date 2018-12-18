package diff;

import org.dom4j.Element;

import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public abstract class Node {
    public int id;
    public int leftMostNodeId;

    public Element element;
    public Map<String, String> attributesMap;
    public String[] text;

//    public Node father;
    public List<Node> children;

    public int counterpartId;
    public OperationEnum op;

    public int depth;
    public int rightMostNodeId;

    public Node() {
        element = null;
        id = leftMostNodeId = 0;
        children = null;
    }

    public Node(Element element) {
        this.element = element;
    }

}
