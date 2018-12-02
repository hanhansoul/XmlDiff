package diff;

import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class Node {
    public int id;
    public Element element;
    public int leftMostNode;
    public Operation operation;
    public Map<String, String> attributesMap;
    public String[] text;

    public Node() {
        element = null;
        id = leftMostNode = 0;
        operation = null;
        attributesMap = new HashMap<>(0);
        text = null;
    }

    public Node(int id, Element element, int leftMostNode) {
        this.id = id;
        this.element = element;
        this.leftMostNode = leftMostNode;
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
        for(int i=1;st.hasMoreTokens();i++){
            this.text[i] = st.nextToken();
        }
    }

    public Element getElement() {
        return element;
    }
}
