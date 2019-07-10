package maindiff.abs.work;

import maindiff.util.OperationEnum;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

public class Node {
    /** 节点ID号 **/
    public int id;

    /** XML树中当前节点的儿子节点 **/
    public List<Node> children;

    /** 递推方程中记录最左侧兄弟ID号 **/
    public int leftMostNodeId;

    /** 用于在输出时记录当前节点的操作类型 **/
    public OperationEnum operationType;

    /** 用于控制输出结果中不同层次标签的缩进 **/
    public int depth;

    /** 用于记录xml相关元素内容 **/
    public Element element;

    public Node() {
    }

    public Node(Element element) {
        this.element = element;
    }

}
