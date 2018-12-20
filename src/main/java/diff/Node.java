package diff;

import org.dom4j.Element;

import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public abstract class Node {
    public int id;
    public int leftMostNodeId;

    /**
     * 用于记录xml相关元素内容
     */
    public Element element;
    public Map<String, String> attributesMap;
    public String[] text;

    /**
     * 树节点的子节点
     */
//    public Node father;
    public List<Node> children;

    public int counterpartId;
    public OperationEnum op;

    /**
     * 用于回溯路径
     */
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
