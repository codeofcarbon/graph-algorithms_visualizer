package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Vertex extends JPanel {
    final String id;
    final JLabel vertexID;
    final Point center;
    final float radius;
    boolean visited, marked, connected;
    final List<Edge> connectedEdges;
    VertexState state;
    int distance;

    public Vertex(String id, Point center) {
        this.id = id;
        this.center = center;
        this.radius = 25f;
        this.connectedEdges = new ArrayList<>();
        setName("Vertex " + id);
        setLocation(center);
        this.vertexID = new JLabel(id);
        vertexID.setName("VertexLabel " + id);
        add(vertexID);
    }

    VertexState getState() {
        if (this.marked) return this.state = VertexState.MARKED;
        else if (this.visited) return this.state = VertexState.VISITED;
        else if (this.connected) return this.state = VertexState.CONNECTED;
        else return this.state = VertexState.RAW;
    }
}

enum VertexState {
    RAW() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.LIGHT_GRAY);
            midPoint(g, v);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(v.getX() - 14, v.getY() - 14, 30, 30);
            g.setColor(Color.WHITE);
        }
    },
    CONNECTED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.GREEN);
            midPoint(g, v);
            g.setColor(Color.WHITE);
            g.fillOval(v.getX() - 19, v.getY() - 19, 40, 40);
            g.setColor(Color.BLACK);
        }
    },
    MARKED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.RED);
            midPoint(g, v);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(v.getX() - 14, v.getY() - 14, 30, 30);
            g.setColor(Color.WHITE);
        }
    },
    VISITED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            midPoint(g, v);
            g.setColor(Color.GREEN);
            g.fillOval(v.getX() - 22, v.getY() - 22, 45, 45);
            g.setColor(Color.WHITE);
        }
    };

    abstract void coloring(Graphics g, Vertex vertex);

    void midPoint(Graphics g, Vertex vertex) {
        int x = 0, y = (int) vertex.radius;
        int d = (int) (1 - vertex.radius);
        int c1 = 3, c2 = (int) (5 - 2 * vertex.radius);
        drawEightPoints(g, x, y, vertex.center);
        while (x < y) {
            if (d < 0) {
                d += c1;
                c2 += 2;
            } else {
                d += c2;
                c2 += 4;
                y--;
            }
            c1 += 2;
            x++;
            drawEightPoints(g, x, y, vertex.center);
        }
    }

    void drawEightPoints(Graphics g, int a, int b, Point center) {
        g.drawOval(center.x + a, center.y + b, 1, 1);
        g.drawOval(center.x - a, center.y + b, 1, 1);
        g.drawOval(center.x - a, center.y - b, 1, 1);
        g.drawOval(center.x + a, center.y - b, 1, 1);
        g.drawOval(center.x + b, center.y + a, 1, 1);
        g.drawOval(center.x - b, center.y + a, 1, 1);
        g.drawOval(center.x - b, center.y - a, 1, 1);
        g.drawOval(center.x + b, center.y - a, 1, 1);
    }
}