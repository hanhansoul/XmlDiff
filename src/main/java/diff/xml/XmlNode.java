package diff.xml;

import diff.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class XmlNode extends Node {


    public XmlNode() {
        super();
        attributesMap = new HashMap<>(0);
        text = null;
    }

    public XmlNode(Element element, Node father) {
        super(element, father);
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
