package maindiff.abs.output;

import java.util.List;

public abstract class Path {
    public List<PathNode> nodes;

    public Path(List<PathNode> nodes) {
        this.nodes = nodes;
    }
}
