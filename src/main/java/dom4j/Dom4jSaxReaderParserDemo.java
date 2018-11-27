package dom4j;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

import java.io.File;

public class Dom4jSaxReaderParserDemo {

    public static void main(String[] args) {
        new Dom4jSaxReaderParserDemo().solve();
    }

    public void solve() {
        SAXReader reader = new SAXReader();
        File file = new File("data/input.xml");
        reader.addHandler("/PeopleList/People", new PeopleHandler());
        try {
            reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    class PeopleHandler implements ElementHandler {

        @Override
        public void onStart(ElementPath elementPath) {
            Element element = elementPath.getCurrent();
            System.out.println(element.attributeValue("id"));
            elementPath.addHandler("Name", new NameHandler());
            elementPath.addHandler("Age", new AgeHandler());
        }

        @Override
        public void onEnd(ElementPath elementPath) {
            elementPath.removeHandler("Name");
        }
    }

    class NameHandler implements ElementHandler {

        @Override
        public void onStart(ElementPath elementPath) {
            System.out.println(elementPath.getPath());
        }

        @Override
        public void onEnd(ElementPath elementPath) {
            Element element = elementPath.getCurrent();
            System.out.println(element.getName() + ": " + element.getText());
        }
    }

    class AgeHandler implements ElementHandler {

        @Override
        public void onStart(ElementPath elementPath) {
            System.out.println(elementPath.getPath());
        }

        @Override
        public void onEnd(ElementPath elementPath) {
            Element element = elementPath.getCurrent();
            System.out.println(element.getName() + ": " + element.getText());
        }
    }
}
