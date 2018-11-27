package diff;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public class XmlDiff {

    private Document leftDocument;
    private Document rightDocument;
    private Element leftRoot;
    private Element rightRoot;

    public Document getLeftDocument() {
        return leftDocument;
    }

    public void setLeftDocument(Document leftDocument) {
        this.leftDocument = leftDocument;
    }

    public Document getRightDocument() {
        return rightDocument;
    }

    public void setRightDocument(Document rightDocument) {
        this.rightDocument = rightDocument;
    }

    public Element getLeftRoot() {
        return leftRoot;
    }

    public void setLeftRoot(Element leftRoot) {
        this.leftRoot = leftRoot;
    }

    public Element getRightRoot() {
        return rightRoot;
    }

    public void setRightRoot(Element rightRoot) {
        this.rightRoot = rightRoot;
    }

    public void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftDocument = xmlFileInput(leftFileName);
        rightDocument = xmlFileInput(rightFileName);
        leftRoot = leftDocument.getRootElement();
        rightRoot = rightDocument.getRootElement();

    }

    public Document xmlFileInput(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        File file = new File(fileName);
        return reader.read(file);
    }

    public int elementCount(Element element) {
        List<Element> elementList = element.elements();
        if (elementList == null || elementList.size() == 0) {
            return 1;
        }
        int res = 0;
        for (int i = 0; i < elementList.size(); i++) {
            res += elementCount(elementList.get(i));
        }
        return res + 1;
    }

    public void solve() {

    }

    public static void main(String[] args) throws DocumentException {
        XmlDiff xmlDiff = new XmlDiff();
    }
}
