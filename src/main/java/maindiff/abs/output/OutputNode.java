package maindiff.abs.output;

import maindiff.abs.work.Node;

public abstract class OutputNode {
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
