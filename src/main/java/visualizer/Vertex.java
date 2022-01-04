package visualizer;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex extends JPanel implements Serializable {
    private static final long serialVersionUID = 12345L;
    int x, y, distance, dx = 0, dy = 0;
    boolean visited, marked, connected, path;
    final List<Edge> connectedEdges;
    final float radius;
    final String id;
    Point center;

    public Vertex(String id, Point center) {
        this.id = id;
        this.center = center;
        this.x = center.x;
        this.y = center.y;
        this.radius = 25f;
        this.connectedEdges = new ArrayList<>();
        setName("Vertex " + id);
        setLocation(center);
        setOpaque(true);
    }

    public int getX() {
        return center.x;
    }

    public int getY() {
        return center.y;
    }

    public void drag() {
        dx = center.x;
        dy = center.y;
    }

    public void moveTo(int x, int y) {
        this.center.x = dx + x;
        this.center.y = dy + y;
    }

    public void moveBy(int x, int y) {
        this.center.x += x;
        this.center.y += y;
    }

    public void drop() {
        this.center.x = dx;
        this.center.y = dy;
        setLocation(center);
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
            g.setColor(Color.LIGHT_GRAY);
            g.drawOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(v.getX() - 14, v.getY() - 14, 30, 30);
            g.setColor(Color.WHITE);
        }
    },
    MARKED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.RED);
            g.drawOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(v.getX() - 14, v.getY() - 14, 30, 30);
            g.setColor(Color.WHITE);
        }
    },
    CONNECTED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.GREEN);
            g.drawOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.WHITE);
            g.fillOval(v.getX() - 17, v.getY() - 17, 36, 36);
            g.setColor(Color.BLACK);
        }
    },
    VISITED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            g.drawOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(new Color(20, 80, 230, 255));
            g.fillOval(v.getX() - 17, v.getY() - 17, 36, 36);
            g.setColor(Color.WHITE);
        }
    },
    ROOT() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(new Color(20, 80, 230, 255));
            g.drawOval(v.getX() - 34, v.getY() - 34, 70, 70);
            g.setColor(new Color(90, 250, 70, 255));
            g.fillOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.BLACK);
        }
    },
    PATH() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            g.drawOval(v.getX() - 34, v.getY() - 34, 70, 70);
            g.setColor(new Color(90, 250, 70, 255));
            g.fillOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.BLACK);
        }
    };

    abstract void coloring(Graphics g, Vertex v);
}