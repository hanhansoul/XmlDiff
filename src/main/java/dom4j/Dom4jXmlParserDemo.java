package dom4j;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dom4jXmlParserDemo {
    public static void main(String[] args) {
        SAXReader reader = new SAXReader();
        File file = new File("data/input.xml");

        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
//            List<People> peopleList = new ArrayList<>();
            for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext(); ) {
//                People people = new People();
                Element element = iterator.next();
                for (Iterator<Element> childIterator = element.elementIterator(); childIterator.hasNext(); ) {
                    Element childElement = childIterator.next();
                    System.out.println(childElement.getName());
                }
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
            }
//            for (People p : peopleList) {
//                System.out.println(p);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
