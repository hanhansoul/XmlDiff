package dom4j;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Dom4jWriteXmlFileDemo {
    public static void test2() throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        Element author1 = root.addElement("author")
                .addAttribute("name", "James")
                .addAttribute("location", "UK")
                .addText("Title1");
        Element author2 = root.addElement("author")
                .addAttribute("name", "Bob")
                .addAttribute("location", "US")
                .addText("Title2");
//        System.out.println(author2.getDocument());
//        FileWriter fw = new FileWriter("data/output.xml");
////        document.write(fw);
////        fw.close();
//        OutputFormat format = OutputFormat.createPrettyPrint();
//        format.setEncoding("utf-8");
//        XMLWriter xw = new XMLWriter(fw, format);
//        xw.write(document);
        Attribute attribute = author1.attribute("name");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(format);
//        writer.write(root);
        writer.write(author1.getText());
        writer.close();
    }

    public static void test1() throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        File file = new File("data/CSCA350-353000-00M01-01-X_2_20180901.xml");
        Document root = reader.read(file);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(format);
        writer.write(root);
        writer.close();
    }

    public static void test3() throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        File file = new File("data/CSCA350-353000-00M01-01-X_2_20180901.xml");
        Document root = reader.read(file);
        Element element = root.getRootElement().element("smjc-header");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(format);
        writer.write(element);
        writer.close();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        test1();
    }
}
