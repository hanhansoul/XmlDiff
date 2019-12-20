package newdiff.xml;

import newdiff.abs.Node;
import org.dom4j.Element;

import java.util.Map;

public class XmlNode extends Node {

    public String tagName;
    public Map<String, String> attributesMap;
    public String[] textArr;
    public String text;

    public XmlNode(Element element) {
        super(element);
        tagName = element.getName();
//        if (element.attributes() != null) {
//            attributesMap = new HashMap<>();
//            for (Attribute attribute : element.attributes()) {
//                attributesMap.put(attribute.getName(), attribute.getValue());
//            }
//        }
        text = element.getTextTrim().replaceAll("\n", "").trim();
        if (text.length() > 0) {
            textArr = text.split("\\s+");
        } else {
            textArr = new String[0];
        }
    }
}
