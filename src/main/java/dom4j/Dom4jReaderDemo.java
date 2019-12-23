package dom4j;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by Administrator on 2019/12/12 0012.
 */
public class Dom4jReaderDemo {

    public static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(filename)); // 读取XML文件,获得document对象
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    public static Document load(URL url) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(url); // 读取XML文件,获得document对象
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    public static void domRead(Node root) {
//        Document document = load("data/left5.xml");
//        Element root = document.getRootElement();
//        Iterator<Node> iterator = root.nodeIterator();
//        while(iterator.hasNext()) {
//            Node node = iterator.next();
//
//        }
    }

    public static void main(String[] args) {
        Document document = load("data/left5.xml");
        Element root = document.getRootElement();
//        System.out.println();
//        System.out.println(root.getName());
//        System.out.println(root.getTextTrim());
//        for (Element element : root.elements()) {
//            System.out.println(element.getData());
//        }
        Iterator<Node> iterator = root.nodeIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
//            System.out.println(iterator.next());
//            System.out.println("------------");
            System.out.println(node.getName());
            System.out.println(node.getText());
            System.out.println("------------");
        }
    }
}
