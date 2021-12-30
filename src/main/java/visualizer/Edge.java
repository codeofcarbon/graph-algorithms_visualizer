package visualizer;

import javax.swing.*;
import java.awt.*;

public class Edge extends JComponent {
    final JLabel edgeLabel;
    final Vertex source;
    final Vertex target;
    boolean visited, hidden;
    Edge mirrorEdge;
    int weight;

    public Edge(Vertex source, Vertex target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        setName(String.format("Edge <%s -> %s>", source.id, target.id));
        this.edgeLabel = new JLabel(String.valueOf(weight));
        edgeLabel.setName(String.format("EdgeLabel <%s -> %s>", source.id, target.id));
    }

    protected EdgeState getState() {
        return Algorithm.pathResult.contains(this) ? EdgeState.PATH
                : this.visited ? EdgeState.VISITED
                : this.hidden ? EdgeState.HIDDEN
                : EdgeState.RAW;
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
            edge.setOpaque(true);
            g.setColor(Color.BLUE);
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.WHITE);
        }
    },
    HIDDEN() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            edge.setOpaque(false);
            g.setColor(Color.BLACK);
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.BLACK);
        }
    },
    PATH() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            edge.setOpaque(true);
            g.setColor(Color.GREEN);
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.WHITE);
        }
    };

    abstract void coloring(Graphics g, Graphics2D g2d, Edge edge);
}