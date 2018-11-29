package diff;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class Tree {
    private Document document;
    public int rootId;
    public int size;
    public Node[] sequence;
    public int sequenceIndex;
    public int[] keyRoots;
    public int keyRootsIndex;

    public Tree(String fileName) throws DocumentException {
        initialization(fileName);
    }

    private void initialization(String fileName) throws DocumentException {
        document = xmlFileInput(fileName);
        size = elementCount(document.getRootElement());
        sequence = new Node[size + 1];
        sequence[0] = new Node();
        keyRoots = new int[size + 1];
        sequenceIndex = keyRootsIndex = 0;
        Node node = build(document.getRootElement());
        rootId = keyRoots[++keyRootsIndex] = node.id;
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

    private Node build(Element element) {
        int leftMostNode = 0;
        Node node;
        List<Element> elementList = element.elements();
        if (elementList == null || elementList.size() == 0) {
            leftMostNode = ++sequenceIndex;
            node = new Node(sequenceIndex, element, leftMostNode);
            sequence[sequenceIndex] = node;
        } else {
            for (int i = 0; i < elementList.size(); i++) {
                Node childNode = build(elementList.get(i));
                if (i > 0) {
                    keyRoots[++keyRootsIndex] = childNode.id;
                } else {
                    leftMostNode = childNode.leftMostNode;
                }
            }
            node = new Node(++sequenceIndex, element, leftMostNode);
            sequence[sequenceIndex] = node;
        }
        return node;
    }

    public void sequenceTraversal() {
        for (int i = 1; i < sequence.length; i++) {
            System.out.println(sequence[i].id + ": " + sequence[i].getElement().getName());
        }
    }

    public void leftMostNodeTraversal() {
        for (int i = 1; i < sequence.length; i++) {
            System.out.println(sequence[i].id + ": " + sequence[i].leftMostNode);
        }
    }

    public void keyRootsTraversal() {
        for (int i = 1; i <= keyRootsIndex; i++) {
            System.out.println(keyRoots[i] + " " + sequence[keyRoots[i]].id);
        }
    }

    public void preorderTraversal(Node root) {

    }

    public void postorderTraversal(Node root) {

    }
}
