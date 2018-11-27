package diff;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class Tree {
    private Document document;
    public Node root;
    public int size;

    public Tree(String fileName) throws DocumentException {
        initialization(fileName);
    }

    private void initialization(String fileName) throws DocumentException {
        document = xmlFileInput(fileName);
        root = build(document.getRootElement());
        root.isKeyRoot = true;
        size = 0;
        nodeLabel(root);
    }

    private Document xmlFileInput(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        File file = new File(fileName);
        return reader.read(file);
    }

    private Node build(Element element) {
        Node node = new Node(element);
        List<Element> elementList = element.elements();
        if (elementList == null || elementList.size() == 0) {
            node.leftMostNode = node;
        } else {
            for (Element ele : elementList) {
                Node child = build(ele);
                if (node.children == null) {
                    node.children = new LinkedList<>();
                    node.leftMostNode = child.leftMostNode;
                    child.isKeyRoot = false;
                } else {
                    node.isKeyRoot = true;
                }
                node.size += child.size;
                node.children.add(child);
            }
        }
        return node;
    }

    private void nodeLabel(Node root) {
        for (Node node : root.children) {
            nodeLabel(node);
        }
        root.id = ++size;
    }
}
