package dom4j;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dom4jXmlParserDemo {


    public static void buildTree(Element element) {
        List<Element> elementList = element.elements();
        if (elementList == null || elementList.size() == 0) {

        } else {
            for (Element ele : elementList) {
//                System.out.println("<" + ele.getName() + "> " + ele.getText());
                ele.content().forEach(x -> {

                });
                buildTree(ele);
            }
        }
    }

    public static void main(String[] args) {
        SAXReader reader = new SAXReader();
        File file = new File("data/right2.xml");

        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
            buildTree(root);

//            List<People> peopleList = new ArrayList<>();
//            root.elements().forEach(x -> System.out.println(x.getName() + " " + x.getText()));
//            List<Node> nodes = document.selectNodes("/proceduralStep/para");
//                    .forEach(x->System.out.println(x.selectSingleNode("internalRef").getName()));
//            OutputFormat format = OutputFormat.createPrettyPrint();
            //输出到指定目录
//            XMLWriter writer = new XMLWriter(new FileOutputStream("data/testoutput.xml"), format);
//            writer.write(document);

//            for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext(); ) {
//                People people = new People();
//                Element element = iterator.next();
//                for (Iterator<Element> childIterator = element.elementIterator(); childIterator.hasNext(); ) {
//                    Element childElement = childIterator.next();
//                    System.out.println(childElement.getName());
//                }
//                for (Iterator<Attribute> attributeIterator = element.attributeIterator(); attributeIterator.hasNext(); ) {
//                    Attribute attribute = attributeIterator.next();
//                    System.out.println(attribute.getName());
//                    System.out.println(attribute.getValue());
//                }
//                people.setId(element.attribute("id").getValue());
//                for (Iterator<Element> childIterator = element.elementIterator(); childIterator.hasNext(); ) {
//                    Element childElement = childIterator.next();
//                    if (childElement.getName().equals("Name")) {
//                        people.setEnglishName(childElement.attribute("en").getValue());
//                    } else if (childElement.getName().equals("Age")) {
//                        people.setAge(childElement.getText());
//                    }
//                }
//                peopleList.add(people);
//            }
//            for (People p : peopleList) {
//                System.out.println(p);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
