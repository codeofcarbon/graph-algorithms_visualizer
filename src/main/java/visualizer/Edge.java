package visualizer;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Edge extends JComponent implements Serializable {
    private static final long serialVersionUID = 123L;
    final JLabel edgeLabel;
    final Vertex source;
    final Vertex target;
    boolean visited, hidden, path;
    Edge mirrorEdge;
    int weight;

    public Edge(Vertex source, Vertex target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        setName(String.format("Edge <%s -> %s>", source.id, target.id));
        this.edgeLabel = new JLabel(String.valueOf(weight));
        setOpaque(true);
    }

    protected EdgeState getState() {
        return this.path ? EdgeState.PATH
                : this.visited ? EdgeState.VISITED
                : this.hidden ? EdgeState.HIDDEN
                : EdgeState.RAW;
    }
}

enum EdgeState {
    RAW() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(60, 60, 60, 255));
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.LIGHT_GRAY);
        }
    },
    VISITED() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(20, 80, 230, 255));
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.WHITE);
        }
    },
    HIDDEN() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(60, 60, 60, 0));
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
        }
    },
    PATH() {
        public void coloring(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(90, 250, 70, 255));
            g2d.drawLine(edge.source.getX(), edge.source.getY(), edge.target.getX(), edge.target.getY());
            g.setColor(Color.WHITE);
        }
    };

    abstract void coloring(Graphics g, Graphics2D g2d, Edge edge);
}