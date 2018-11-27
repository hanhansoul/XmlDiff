package parser;

import Entity.Element;

import java.util.Map;
import java.util.Set;

public class Parser {
    private XmlReader reader;
    private Set<String> specialMarks;

    {
        specialMarks.add("\"");
        specialMarks.add("=");
    }

    public Parser(XmlReader reader) {
        this.reader = reader;
    }

    public void parse() throws IteratorStopException {
        handleXmlHeader();
        skipSpaces();
        handleElement();
    }

    public void handleXmlHeader() {
        try {
            skipUntilMark("?>");
        } catch (IteratorStopException e) {
            System.err.println("over");
        }
    }

    public void handleElement() {

    }

    public void handleAttribute() {

    }

    public void handleContent() {

    }

    public void skipSpaces() throws IteratorStopException {
        while (reader.hasNext()) {
            char ch = reader.peek();
            if (!(ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t')) {
                return;
            }
            reader.next();
        }
    }

    public void skipUntilMark(String mark) throws IteratorStopException {
        int position = 0;
        int length = mark.length();
        while (reader.hasNext()) {
            char ch = reader.next();
            if (ch == mark.charAt(position)) {
                position++;
            } else {
                position = 0;
            }
            if (position == length) {
                return;
            }
        }
    }

    public Element readBeginTagName() throws IteratorStopException {
        Element element = new Element();

        StringBuffer result = new StringBuffer();
        char curChar = 0;
        char preChar;
        while (reader.hasNext()) {
            preChar = curChar;
            curChar = reader.next();
            if (curChar == '>') {
                if (preChar == '/') {
                    result.deleteCharAt(result.length() - 1);
                }
                element.setTagName(result.toString());
                return element;
            }
            if (curChar == ' ' || curChar == '\n' || curChar == '\r' || curChar == '\t') {
                element.setTagName(result.toString());
                break;
            }
            if (curChar == '<') {
                continue;
            }
            result.append(curChar);
        }
        skipSpaces();
        while (reader.hasNext() && reader.peek() != '/') {

        }
        System.err.println("format error");
        throw new IteratorStopException();
    }

    public StringBuffer readEndTagName() throws IteratorStopException {
        StringBuffer result = new StringBuffer();
        char curChar = 0;
        while (reader.hasNext()) {
            curChar = reader.next();
            if (curChar == '>') {
                return result;
            }
            result.append(curChar);
        }
        System.err.println("format error");
        throw new IteratorStopException();
    }

    public StringBuffer readAttributeName() throws IteratorStopException {
        StringBuffer result = new StringBuffer();
        char curChar = 0;
        char preChar;
        while (reader.hasNext()) {
            preChar = curChar;
            curChar = reader.next();
            if (curChar == ' ' || curChar == '\n' || curChar == '\r' || curChar == '\t' || curChar == '=') {
                break;
            }
            result.append(curChar);
        }
        // TODO
        while (reader.hasNext()) {
            preChar = curChar;
            do {
                curChar = reader.next();
            } while (curChar == ' ' || curChar == '\n' || curChar == '\r' || curChar == '\t');
            if (preChar == '=' && curChar == '"') {
                result.deleteCharAt(result.length() - 1);
                return result;
            } else {
                System.err.println("format error");
                throw new IteratorStopException();
            }
        }
        System.err.println("format error");
        throw new IteratorStopException();
    }

    public StringBuffer readAttributeValue() throws IteratorStopException {
        StringBuffer result = new StringBuffer();
        char curChar;
        while (reader.hasNext()) {
            curChar = reader.next();
            if (curChar == '"') {
                return result;
            }
            result.append(curChar);
        }
        System.err.println("format error");
        throw new IteratorStopException();
    }

    public StringBuffer readContent() throws IteratorStopException {
        StringBuffer result = new StringBuffer();
        char curChar = 0;
        char preChar;
        while (reader.hasNext()) {
            preChar = curChar;
            curChar = reader.next();
            if (preChar == '<' && curChar == '/') {
                result.deleteCharAt(result.length() - 1);
                return result;
            }
            result.append(curChar);
        }
        System.err.println("format error");
        throw new IteratorStopException();
    }
}
