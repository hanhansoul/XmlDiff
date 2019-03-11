package maindiff.xml.work;

import maindiff.abs.work.Node;
import org.dom4j.Element;

import java.util.Map;

public class XmlNode extends Node {

    public Map<String, String> attributesMap;
    public String[] text;

    public XmlNode() {
        super();
    }

    public XmlNode(Element element) {
        super(element);
    }
}
