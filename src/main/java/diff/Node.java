package diff;

import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class Node {
    public int id;
    public int fatherId;    // father.id
    public int leftMostNodeId;

    public Element element;
    public Map<String, String> attributesMap;
    public String[] text;

    public Node father;
    public List<Node> children;

    public Operation operation;

    public Node() {
        element = null;
        id = leftMostNodeId = 0;
        operation = null;
        attributesMap = new HashMap<>(0);
        text = null;
        children = null;
        father = null;
    }

    public Node(Element element, Node father) {
        this.element = element;
        this.father = father;
        List<Attribute> attributes = element.attributes();
        if (attributes != null) {
            this.attributesMap = new HashMap<>();
            for (Attribute attribute : attributes) {
                attributesMap.put(attribute.getName(), attribute.getValue());
            }
        } else {
            this.attributesMap = new HashMap<>(0);
        }
        StringTokenizer st = new StringTokenizer(element.getText());
        this.text = new String[st.countTokens() + 1];
        for (int i = 1; st.hasMoreTokens(); i++) {
            this.text[i] = st.nextToken();
        }
    }

}
