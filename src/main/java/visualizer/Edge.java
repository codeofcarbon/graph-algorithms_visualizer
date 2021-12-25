package visualizer;

import javax.swing.*;

public class Edge extends JComponent {
    final JLabel edgeLabel;
    final Vertex first;
    final Vertex second;
    boolean connected, visited;
    int weight;

    public Edge(Vertex first, Vertex second, int weight) {
        this.first = first;
        this.second = second;
        this.weight = weight;
        setName(String.format("Edge <%s -> %s>", first.id, second.id));
        this.edgeLabel = new JLabel(String.valueOf(weight));
        edgeLabel.setName(String.format("EdgeLabel <%s -> %s>", first.id, second.id));
    }
}