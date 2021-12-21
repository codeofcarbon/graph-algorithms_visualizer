import javax.swing.*;

public class Edge extends JComponent {
    protected final JLabel edgeLabel;
    protected final Vertex first;
    protected final Vertex second;

    public Edge(Vertex first, Vertex second, int weight) {
        this.first = first;
        this.second = second;
        setName(String.format("Edge <%s -> %s>", first.id, second.id));
        this.edgeLabel = new JLabel(String.valueOf(weight));
        edgeLabel.setName(String.format("EdgeLabel <%s -> %s>", first.id, second.id));
    }
}
