package visualizer;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex extends JPanel implements Serializable {
    private static final long serialVersionUID = 12345L;
    boolean visited, marked, connected, path;
    final List<Edge> connectedEdges;
    final int radius = 25;
    final String id;
    int distance;

    public Vertex(String id, Point center) {
        setName("Vertex " + id);
        this.id = id;
        this.connectedEdges = new ArrayList<>();
        setLocation(center.x - radius, center.y - radius);
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setOpaque(false);
    }

    protected VertexState getState() {
        return Algorithm.target == this || this.path ? VertexState.PATH
                : Algorithm.root == this ? VertexState.ROOT
                : this.marked ? VertexState.MARKED
                : this.visited ? VertexState.VISITED
                : this.connected ? VertexState.CONNECTED
                : VertexState.RAW;
    }
}

enum VertexState {
    RAW() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            g.drawOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(Color.WHITE);
            g.fillOval(v.getX() + 10, v.getY() + 10, 30, 30);
            g.setColor(Color.BLACK);
        }
    },
    MARKED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.RED);
            g.drawOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(v.getX() + 10, v.getY() + 10, 30, 30);
            g.setColor(Color.WHITE);
        }
    },
    CONNECTED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.GREEN);
            g.drawOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(Color.WHITE);
            g.fillOval(v.getX() + 7, v.getY() + 7, 36, 36);
            g.setColor(Color.BLACK);
        }
    },
    VISITED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            g.drawOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(new Color(20, 80, 230, 255));
            g.fillOval(v.getX() + 7, v.getY() + 7, 36, 36);
            g.setColor(Color.WHITE);
        }
    },
    ROOT() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(new Color(20, 80, 230, 255));
            g.drawOval(v.getX() - 9, v.getY() - 9, 68, 68);
            g.setColor(new Color(90, 250, 70, 255));
            g.fillOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(Color.BLACK);
        }
    },
    PATH() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            g.drawOval(v.getX() - 9, v.getY() - 9, 68, 68);
            g.setColor(new Color(90, 250, 70, 255));
            g.fillOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(Color.BLACK);
        }
    };

    abstract void coloring(Graphics g, Vertex v);
}