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
        if (this.marked) return VertexState.MARKED;
        else if (this.visited) return VertexState.VISITED;
        else if (this.connected) return VertexState.CONNECTED;
        else return VertexState.RAW;
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
    CONNECTED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.GREEN);
            g.drawOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.WHITE);
            g.fillOval(v.getX() - 19, v.getY() - 19, 40, 40);
            g.setColor(Color.BLACK);
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
    VISITED() {
        public void coloring(Graphics g, Vertex v) {
            g.setColor(Color.WHITE);
            g.drawOval(v.getX() - 24, v.getY() - 24, 50, 50);
            g.setColor(Color.GREEN);
            g.fillOval(v.getX() - 22, v.getY() - 22, 45, 45);
            g.setColor(Color.WHITE);
        }
    };

    abstract void coloring(Graphics g, Vertex v);
}