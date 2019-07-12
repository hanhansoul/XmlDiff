package maindiff.xml.v2.work;

import maindiff.abs.work.Node;
import maindiff.abs.work.Tree;
import maindiff.xml.v2.output.XmlOutputNode;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class XmlTree extends Tree {

    public List<XmlOutputNode> nodeOutputSequence;

    public XmlTree(String fileName) throws DocumentException {
        super(fileName);

        rootNode = new XmlNode(document.getRootElement(), 1);
        buildTree(rootNode, 1);

        buildPostOrderSequence(rootNode);
        rootId = keyRoots[++keyRootsIndex] = rootNode.id;
    }

    @Override
    protected void buildTree(Node root, int depth) {
        List<Element> elementList = root.element.elements();
        if (elementList == null || elementList.size() == 0) {
            root.children = null;
        } else {
            root.children = new ArrayList<>(elementList.size());
            for (Element element : elementList) {
                Node node = new XmlNode(element, depth + 1);
                root.children.add(node);
                buildTree(node, depth + 1);
            }
        }
    }

}
