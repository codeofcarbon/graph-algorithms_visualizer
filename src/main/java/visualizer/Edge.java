package visualizer;

import javax.swing.*;

public class Edge extends JComponent {
    final JLabel edgeLabel;
    final Vertex first;
    final Vertex second;
//    final Edge mirrorEdge;
    boolean visited, hidden;
    int weight;

    public Edge(Vertex first, Vertex second, int weight) {      //}, Edge mirrorEdge) {
        this.first = first;
        this.second = second;
        this.weight = weight;
//        this.mirrorEdge = mirrorEdge;
        setName(String.format("Edge <%s -> %s>", first.id, second.id));
        this.edgeLabel = new JLabel(String.valueOf(weight));
        edgeLabel.setName(String.format("EdgeLabel <%s -> %s>", first.id, second.id));
    }
}