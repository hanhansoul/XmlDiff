package maindiff.abs.v2.output;

import maindiff.abs.work.Node;

public class OutputNode {
    public Node node;
    public boolean isEndTag;

    public OutputNode() {
    }

    public OutputNode(Node node) {
        this.node = node;
    }

    public OutputNode(Node node, boolean isEndTag) {
        this.node = node;
        this.isEndTag = isEndTag;
    }
}
