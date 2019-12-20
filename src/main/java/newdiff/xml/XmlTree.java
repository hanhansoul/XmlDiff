package newdiff.xml;

import maindiff.abs.work.Tree;
import org.dom4j.DocumentException;

public class XmlTree extends Tree {

    public XmlTree(String fileName) throws DocumentException {
        super(fileName);
    }

    public List<XmlOutputNode> nodeOutputSequence;

    public XmlTree(String fileName) throws DocumentException {
        super(fileName);

        rootNode = new XmlNode(document.getRootElement());
        buildTree(rootNode);

        buildPostOrderSequence(rootNode);
        rootId = keyRoots[++keyRootsIndex] = rootNode.id;

        nodeOutputSequence = new LinkedList<>();
        buildNodeOutputSequence(rootNode, 0);
    }

    @Override
    protected void buildTree(Node root) {
        List<Element> elementList = root.element.elements();
        if (elementList == null || elementList.size() == 0) {
            root.children = null;
        } else {
            root.children = new ArrayList<>(elementList.size());
            for (Element element : elementList) {
                Node node = new XmlNode(element);
                root.children.add(node);
                buildTree(node);
            }
        }
    }

    @Override
    protected void buildNodeOutputSequence(Node root, int depth) {
        root.depth = depth;
        nodeOutputSequence.add(new XmlOutputNode(root, false));
        if (root.children != null) {
            for (Node node : root.children) {
                buildNodeOutputSequence(node, depth + 1);
            }
        }
        nodeOutputSequence.add(new XmlOutputNode(root, true));
    }
}
