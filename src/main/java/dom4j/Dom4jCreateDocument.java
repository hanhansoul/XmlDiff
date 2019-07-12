package dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.Iterator;

public class Dom4jCreateDocument {

    public final static Element BR;

    static {
        BR = DocumentHelper.createElement("br");
    }

    public static void main(String[] args) throws IOException {
//        Document document = DocumentHelper.createDocument();
//        Element rootElement = DocumentHelper.createElement("root");
//        for (char i = 'a'; i < 'f'; i++) {
//            Element ele = rootElement.addElement(String.valueOf(i));
//            ele.addAttribute("value", String.valueOf(i));
//            ele.addText("the text is " + String.valueOf(i));
//            rootElement.add(DocumentHelper.createElement("br"));
//        }
//
//        OutputFormat format = OutputFormat.createPrettyPrint();
//        format.setEncoding("UTF-8");
//
//        Element wrapperElement = DocumentHelper.createElement("wrapper");
//        wrapperElement.add(rootElement);
//
//        File file = new File("data/dom4j/input.xml");
//        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
//        writer.write(wrapperElement);
//        writer.close();
//
//        StringBuffer s = new StringBuffer("abcdef");
//        System.out.println(s.reverse());
        test2();
    }

    public static void test1() throws IOException {
        Document document = DocumentHelper.createDocument();
        Element rootElement = DocumentHelper.createElement("root");
        rootElement.addText("abcd aefd");
        rootElement.add(DocumentHelper.createElement("son").addText("ABDEF"));
        rootElement.addText("teet dfdf");
        rootElement.add(DocumentHelper.createElement("son").addText("YYFFFGG"));
        rootElement.addText("eddf");

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file = new File("data/dom4j/input.xml");
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(rootElement);
        writer.close();
    }

    public static void test2() throws IOException {
        Document document = DocumentHelper.createDocument();
        Element rootElement = DocumentHelper.createElement("root");
        rootElement.addText("abcd aefd");
        rootElement.addText(" teet dfdf");
        rootElement.addText(" eddf");

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file = new File("data/dom4j/input.xml");
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(rootElement);
        writer.close();
    }
}
