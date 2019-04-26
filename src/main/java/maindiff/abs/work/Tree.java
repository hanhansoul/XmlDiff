package maindiff.abs.work;

import maindiff.abs.output.OutputNode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
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

    public int rootId;
    public int size;

    public Tree(String fileName) throws DocumentException {
        document = xmlFileInput(fileName);
        size = elementCount(document.getRootElement());
        nodeSequence = new Node[size + 1];
        keyRoots = new int[size + 1];
        sequenceIndex = keyRootsIndex = 0;
        preOrderSequenceIndex = 0;
    }

    private Document xmlFileInput(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        File file = new File(fileName);
        return reader.read(file);
    }

    private int elementCount(Element element) {
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


    public void sequenceTraversal() {
        for (int i = 1; i < nodeSequence.length; i++) {
            System.out.println(nodeSequence[i].id + ": " + nodeSequence[i].element.getName());
        }
    }

}
