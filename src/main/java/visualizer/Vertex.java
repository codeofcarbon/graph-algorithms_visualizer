package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    public Vertex(String id, Point center, GraphService service) {
        setName("Vertex " + id);
        this.id = id;
        this.connectedEdges = new ArrayList<>();
        setLocation(center.x - radius, center.y - radius);
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setOpaque(true);

        var mouseAdapter = new MouseAdapter() {
            Point location, pressed;

            public void mouseClicked(MouseEvent e) {
                if (service.getGraphMode() == GraphMode.ADD_A_VERTEX) service.createNewVertex(e);
                if (service.getGraphMode() == GraphMode.ADD_AN_EDGE) service.createNewEdge(e);
                if (service.getGraphMode() == GraphMode.REMOVE_A_VERTEX) service.removeVertex(e);
                if (service.getGraphMode() == GraphMode.NONE) {
                    if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                pressed = e.getLocationOnScreen();
                location = getLocation();
            }

            public void mouseDragged(MouseEvent e) {
                Point dragged = e.getLocationOnScreen();
                int x = (int) (location.x + dragged.getX() - pressed.getX());
                int y = (int) (location.y + dragged.getY() - pressed.getY());
                setLocation(x, y);
                getParent().repaint();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
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
            g.drawOval(v.getX() + 1, v.getY() + 1, 48, 48);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(v.getX() + 10, v.getY() + 10, 30, 30);
            g.setColor(Color.WHITE);
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