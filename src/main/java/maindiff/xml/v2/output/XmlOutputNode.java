package maindiff.xml.v2.output;

import maindiff.abs.v2.output.OutputNode;
import maindiff.abs.work.Node;

public class XmlOutputNode extends OutputNode {

    public XmlOutputNode() {
    }

    public XmlOutputNode(Node node, boolean isEndTag) {
        super(node, isEndTag);
    }

}
