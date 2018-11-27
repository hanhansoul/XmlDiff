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
    public int root;
    public int size;
    public List<Node> sequence;
    public List<Integer> keyRoots;

    public Tree(String fileName) throws DocumentException {
        initialization(fileName);
    }

    private void initialization(String fileName) throws DocumentException {
        document = xmlFileInput(fileName);
        sequence = new ArrayList<>();
        keyRoots = new ArrayList<>();
        size = 0;
        build(document.getRootElement());
    }

    private Document xmlFileInput(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        File file = new File(fileName);
        return reader.read(file);
    }

    private Node build(Element element) {
        Node node = new Node(element);
        List<Element> elementList = element.elements();
        if (elementList != null && elementList.size() > 0) {
            for (int i = 0; i < elementList.size(); i++) {
                Node childNode = build(elementList.get(i));
                if (i > 0) {
                    keyRoots.add(childNode.id);
                }
            }
        }
        node.id = ++size;
        sequence.add(node);
        return node;
    }
}
