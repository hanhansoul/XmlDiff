package maindiff.xml.v2.textdiff;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TextDiff4DomHelper {

    public static final String SPACE = " ";

    public final static Element addInsertSpanElement(Element element) {
        return element.addElement("span")
                .addAttribute("style", "background-color:green;display:inline-block;");
    }

    public final static Element addDeleteSpanElement(Element element) {
        return element.addElement("span")
                .addAttribute("style", "background-color:red;display:inline-block;");
    }

    public final static Element addChangeSpanElement(Element element) {
        return element.addElement("span")
                .addAttribute("style", "background-color:blue;display:inline-block;");
    }

    public final static Element getInsertSpanElement() {
        Element element = DocumentHelper.createElement("span");
        element.addAttribute("style", "background-color:green;display:inline-block;");
        return element;
    }

    public final static Element getDeleteSpanElement() {
        Element element = DocumentHelper.createElement("span");
        element.addAttribute("style", "background-color:red;display:inline-block;");
        return element;
    }

    private final static Element getChangeSpanElement() {
        Element element = DocumentHelper.createElement("span");
        element.addAttribute("style", "background-color:blue;display:inline-block;");
        return element;
    }
}
