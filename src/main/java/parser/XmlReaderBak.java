package parser;

import java.io.*;
import java.util.ArrayList;

public class XmlReaderBak {
    class CharArray {
        public int position;
        public int size;
        public char[] chars;

        public CharArray(int capacity) {
            position = size = 0;
            chars = new char[capacity];
        }

        private int load(Reader reader) {
            try {
                size = reader.read(chars);
                position = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return size;
        }

        private boolean isConsumed() {
            return position >= size;
        }

        private boolean isEmpty() {
            return size == 0;
        }

        private char next() throws IteratorStopException {
            if (isEmpty() || isConsumed()) {
                throw new IteratorStopException();
            }
            return chars[position++];
        }

        private char peek() throws IteratorStopException {
            if (isEmpty() || isConsumed()) {
                throw new IteratorStopException();
            }
            return chars[position];
        }
    }

    private final int DEFAULT_CHAR_SIZE = 8192;
    private int capacity;
    private Reader reader;
    private CharArray[] buffer;
    private int current;

    public XmlReaderBak(String filename) {
        try {
            this.reader = new FileReader(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.capacity = DEFAULT_CHAR_SIZE;
        initialization();
    }

    public XmlReaderBak(String filename, int capacity) {
        try {
            this.reader = new FileReader(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.capacity = capacity;
        initialization();
    }

    private void initialization() {
        this.buffer = new CharArray[2];
        buffer[0] = new CharArray(capacity);
        buffer[1] = new CharArray(capacity);
        current = 0;
    }

    private int load(int loadIndex) {
        return buffer[loadIndex].load(reader);
    }

    public char next() throws IteratorStopException {
        if (!buffer[current].isConsumed()) {
            // pass
        } else if (!buffer[1 - current].isConsumed()) {
            current = 1 - current;
        } else {
            int cnt = load(current);
            if (cnt == 0) {
                throw new IteratorStopException();
            }
        }
        return buffer[current].next();
    }

    public char peek() throws IteratorStopException {
        if (!buffer[current].isConsumed()) {
            // pass
        } else if (!buffer[1 - current].isConsumed()) {
            current = 1 - current;
        } else {
            int cnt = load(current);
            if (cnt == 0) {
                throw new IteratorStopException();
            }
        }
        return buffer[current].peek();
    }
}
