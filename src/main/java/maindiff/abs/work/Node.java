package maindiff.abs.work;

import maindiff.util.OperationEnum;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

public abstract class Node {
    public int id;
    public int leftMostNodeId;
    public List<Node> children;

    /**
     * 用于记录xml相关元素内容
     */
    public Element element;
    public OperationEnum op;

    /**
     * 用于回溯路径
     */
    public int depth;
    public int rightMostNodeId;

    public Map<String, String> attributesMap;
    public String[] text;

    public Node() {
        element = null;
        id = leftMostNodeId = 0;
        children = null;
    }

    public Node(Element element) {
        this.element = element;
    }

}
