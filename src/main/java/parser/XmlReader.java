package parser;

import java.io.*;

public class XmlReader {
    private final int DEFAULT_CHAR_SIZE = 8192;
    private int capacity;
    private Reader reader;
    private char[] buffer;
    private int position;
    private int size;

    public XmlReader(String filename) {
        try {
            reader = new FileReader(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        capacity = DEFAULT_CHAR_SIZE;
        buffer = new char[capacity];
        size = position = 0;
    }

    public XmlReader(String filename, int capacity) {
        try {
            reader = new FileReader(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.capacity = capacity;
        buffer = new char[capacity];
        size = position = 0;
    }

    private int load() {
        try {
            size = reader.read(buffer);
            position = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public boolean isConsumed() {
        return position >= size;
    }

    public boolean isEmpty() {
        return size <= 0;
    }

    public char next() throws IteratorStopException {
        if (isConsumed() || isEmpty()) {
            load();
        }
        if (isEmpty()) {
            throw new IteratorStopException();
        }
        return buffer[position++];
    }

    public char peek() throws IteratorStopException {
        if (isConsumed() || isEmpty()) {
            load();
        }
        if (isEmpty()) {
            throw new IteratorStopException();
        }
        return buffer[position];
    }

    public boolean hasNext() {
        if (isEmpty() || isConsumed()) {
            load();
        }
        return !isEmpty();
    }

    public static void main(String[] args) throws IteratorStopException {
        XmlReader reader = new XmlReader("data/a");
        while (reader.hasNext()) {
            System.out.print(reader.next());
        }
    }
}
