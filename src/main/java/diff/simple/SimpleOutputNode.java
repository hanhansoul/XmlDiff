package diff.simple;

import diff.Node;

public class SimpleOutputNode {
    public Node node;
    public boolean isEndTag;

    public SimpleOutputNode(Node node, boolean isEndTag) {
        this.node = node;
        this.isEndTag = isEndTag;
    }
}
