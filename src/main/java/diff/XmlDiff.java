package diff;

import org.dom4j.DocumentException;

public class XmlDiff {

    private Tree leftTree;
    private Tree rightTree;
    private int[][] F;  // permanent array
    private int[][] T;  // temporary array

    private void initialization(String leftFileName, String rightFileName) throws DocumentException {
        leftTree = new Tree(leftFileName);
        rightTree = new Tree(rightFileName);
        F = new int[leftTree.size][rightTree.size];
        T = new int[leftTree.size][rightTree.size];
    }

    private void dfs() {

    }

    public void solve() throws DocumentException {
        initialization("data/left.xml", "data/right.xml");

    }

    public static void main(String[] args) throws DocumentException {
        XmlDiff xmlDiff = new XmlDiff();
        xmlDiff.solve();
    }
}
