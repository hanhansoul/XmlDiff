package maindiff.xml.work;

import maindiff.abs.work.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.Map;

public class XmlNode extends Node {

    /**
     * 标签名称
     **/
    public String tagName;
    /**
     * 属性列表
     **/
    public Map<String, String> attributesMap;
    /**
     * 标签文本内容
     **/
    public String[] text;

    public XmlNode(Element element) {
        super(element);
        tagName = element.getName();
        for (Attribute attribute : element.attributes()) {
            attributesMap.put(attribute.getName(), attribute.getValue());
        }
        text = element.getText().split("\\s+");
    }
}
