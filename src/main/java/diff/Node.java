package diff;

import org.dom4j.Element;

import java.util.List;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class Node {
    public int id;
    private Element element;
    public List<Node> children;
    public Node leftMostNode;
    public boolean isKeyRoot;
    public int size;
    public Operation operation;


    public Node(Element element) {
        this.element = element;
        children = null;
        leftMostNode = null;
        isKeyRoot = false;
        operation = null;
        size = 1;
    }

}
