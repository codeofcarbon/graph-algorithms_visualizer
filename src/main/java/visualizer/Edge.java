package visualizer;

import javax.swing.*;
import java.awt.*;

public class Edge extends JComponent {
    final JLabel edgeLabel;
    final Vertex source;
    final Vertex target;
    Edge mirrorEdge;
    boolean visited, hidden;
    int weight;

    public Edge(Vertex source, Vertex target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        setName(String.format("Edge <%s -> %s>", source.id, target.id));
        this.edgeLabel = new JLabel(String.valueOf(weight));
        edgeLabel.setName(String.format("EdgeLabel <%s -> %s>", source.id, target.id));
    }

    EdgeState getState() {
        if (this.visited) return EdgeState.VISITED;
        else if (this.hidden) return EdgeState.HIDDEN;
        else return EdgeState.RAW;
    }
}

enum EdgeState {
    RAW() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(Color.DARK_GRAY);
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.LIGHT_GRAY);
        }
    },
    VISITED() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(Color.WHITE);
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.GREEN);
        }
    },
    HIDDEN() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(Color.BLACK);
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.BLACK);
        }
    };

    abstract void coloring(Graphics g, Graphics2D g2d, Edge edge);
}