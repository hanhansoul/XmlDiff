package maindiff.abs.work;

import maindiff.abs.output.OutputNode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public abstract class Tree {
    protected Document document;

    public Node rootNode;

    public Node[] nodeSequence;
    public int sequenceIndex;

    public int keyRootsIndex;
    public int[] keyRoots;

//    public Node[] nodePreOrderSequence;
    public int preOrderSequenceIndex;

    public List<OutputNode> nodeOutputSequence;

    public int rootId;
    public int size;

    protected Document xmlFileInput(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        File file = new File(fileName);
        return reader.read(file);
    }

    protected int elementCount(Element element) {
        List<Element> elementList = element.elements();
        int res = 1;
        if (elementList != null && elementList.size() > 0) {
            for (Element anElementList : elementList) {
                res += elementCount(anElementList);
            }
        }
        return res;
    }

    protected abstract void buildTree(Node root);

    protected void buildPostOrderSequence(Node root) {
        List<Node> nodeList = root.children;
        if (nodeList == null || nodeList.size() == 0) {
            root.id = root.leftMostNodeId = ++sequenceIndex;
            nodeSequence[sequenceIndex] = root;
        } else {
            for (int i = 0; i < nodeList.size(); i++) {
                Node childNode = nodeList.get(i);
                buildPostOrderSequence(childNode);
                if (i > 0) {
                    keyRoots[++keyRootsIndex] = childNode.id;
                } else {
                    root.leftMostNodeId = childNode.leftMostNodeId;
                }
            }
            root.id = ++sequenceIndex;
            nodeSequence[sequenceIndex] = root;
        }
    }

    protected abstract void buildNodeOutputSequence(Node root, int depth);

}
