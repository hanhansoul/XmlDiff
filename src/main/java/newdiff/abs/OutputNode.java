package newdiff.abs;

import newdiff.xml.XmlOutputNode;

public abstract class OutputNode {
    public Node node;
    public boolean isEndTag;

    public OutputNode(Node node, boolean isEndTag) {
        this.node = node;
        this.isEndTag = isEndTag;
    }
}
