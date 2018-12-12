package diff.simple;

import diff.Node;
import diff.simple.SimpleNode;
import diff.xml.XmlNode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class SimpleTree {
    private Document document;

    public Node rootNode;
    public Node[] nodeSequence;
    public Node[] nodePreOrderSequence;
    public int[] keyRoots;
    public int sequenceIndex;
    public int preOrderSequenceIndex;
    public int keyRootsIndex;

    public int rootId;
    public int size;

    public SimpleTree(String fileName) throws DocumentException {
        initialization(fileName);
    }

    private void initialization(String fileName) throws DocumentException {
        document = xmlFileInput(fileName);
        size = elementCount(document.getRootElement());
        nodeSequence = new Node[size + 1];
        keyRoots = new int[size + 1];
        sequenceIndex = keyRootsIndex = 0;

        nodeSequence = new Node[size + 1];
        preOrderSequenceIndex = 0;

        rootNode = new SimpleNode(document.getRootElement(), null);
        buildTree(rootNode);

        buildPostOrderSequence(rootNode);
        rootId = keyRoots[++keyRootsIndex] = rootNode.id;

        buildPreOrderSequence(rootNode);
//        sequenceTraversal();
//        leftMostNodeTraversal();
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
            for (int i = 0; i < elementList.size(); i++) {
                res += elementCount(elementList.get(i));
            }
        }
        return res;
    }

    private void buildTree(Node root) {
        List<Element> elementList = root.element.elements();
        if (elementList == null || elementList.size() == 0) {
            root.children = null;
        } else {
            root.children = new ArrayList<>(elementList.size());
//            root.children = new LinkedList<>();
            for (Element element : elementList) {
                Node node = new SimpleNode(element, root);
                root.children.add(node);
                buildTree(node);
            }
        }
    }

    private void buildPostOrderSequence(Node root) {
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

    public void buildPreOrderSequence(Node root) {
        nodePreOrderSequence[++preOrderSequenceIndex] = root;
        for (Node node : root.children) {
            buildPostOrderSequence(node);
        }
    }

    public void buildDiffOutput(Node leftNode, Node rightNode) {
        if (leftNode.op != null || rightNode != null) {

        } else {

        }
    }

    public void sequenceTraversal() {
        for (int i = 1; i < nodeSequence.length; i++) {
            System.out.println(nodeSequence[i].id + ": " + nodeSequence[i].element.getName());
        }
    }

    public void leftMostNodeTraversal() {
        for (int i = 1; i < nodeSequence.length; i++) {
            System.out.println(nodeSequence[i].id + ": " + nodeSequence[i].leftMostNodeId);
        }
    }

    public void keyRootsTraversal() {
        for (int i = 1; i <= keyRootsIndex; i++) {
            System.out.println(keyRoots[i] + " " + nodeSequence[keyRoots[i]].id);
        }
    }

    public void preorderTraversal(Node root) {

    }

    public void postorderTraversal(Node root) {

    }
}
