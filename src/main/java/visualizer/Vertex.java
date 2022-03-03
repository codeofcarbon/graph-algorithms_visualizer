package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.undo.StateEditable;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

@Getter
public class Vertex extends JLabel implements Serializable, StateEditable {
    private static final long serialVersionUID = 12345L;
    private final String imageName;
    private final Graph graph;
    final List<Edge> connectedEdges;
    final int radius = 25;
    final String id;

    boolean visited, marked, connected, path;
    int distance;

    public Vertex(String id, Point center, Graph graph, List<Edge> connectedEdges) {
        setName("Vertex " + id);
        this.id = id;
        this.graph = graph;
        this.imageName = id.matches("[a-z]") ? id.concat("_lower")
                : id.matches("[A-Z]") ? id.concat("_upper") : id;
        this.connectedEdges = connectedEdges;
        setLocation(center.x - radius, center.y - radius);
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setOpaque(false);
    }

    protected VertexState getState() {
        return Algorithm.root == this ? VertexState.ROOT
                : Algorithm.target == this ? VertexState.TARGET
                : this.path ? VertexState.PATH
                : this.visited ? VertexState.VISITED
                : this.connected ? VertexState.CONNECTED
                : VertexState.RAW;
    }

    @Override
    public void storeState(Hashtable<Object, Object> state) {
        state.put("Location", getLocation());
    }

    @Override
    public void restoreState(Hashtable<?, ?> state) {
        var nodeLocation = (Point) state.get("Location");
        if (nodeLocation != null) setLocation(nodeLocation);
        getParent().repaint();
    }
}

enum VertexState {
    RAW() {
        public void draw(Graphics2D g2D, Vertex v) {
            g2D.drawImage(getNodeImage(v.getImageName(), "raw", 40, 40), v.getX() + 5, v.getY() + 5, null);
            if (v.marked) g2D.drawImage(dashedMark, v.getX() - 5, v.getY() - 5, null);
            else g2D.drawImage(raw, v.getX() - 5, v.getY() - 5, null);
        }
    },
    CONNECTED() {
        public void draw(Graphics2D g2D, Vertex v) {
            g2D.drawImage(getNodeImage(v.getImageName(), "connected", 40, 40), v.getX() + 5, v.getY() + 5, null);
            if (v.marked) g2D.drawImage(dashedMark, v.getX() - 5, v.getY() - 5, null);
            else g2D.drawImage(connected, v.getX() - 5, v.getY() - 5, null);
        }
    },
    VISITED() {
        public void draw(Graphics2D g2D, Vertex v) {
            g2D.drawImage(getNodeImage(v.getImageName(), "visited", 40, 40), v.getX() + 5, v.getY() + 5, null);
            g2D.drawImage(visited, v.getX() - 5, v.getY() - 5, null);
        }
    },
    PATH() {
        public void draw(Graphics2D g2D, Vertex v) {
            g2D.drawImage(getNodeImage(v.getImageName(), "path", 50, 50), v.getX(), v.getY(), null);
            g2D.drawImage(path, v.getX() - 10, v.getY() - 10, null);
        }
    },
    ROOT() {
        public void draw(Graphics2D g2D, Vertex v) {
            g2D.drawImage(rootNode, v.getX() - 21, v.getY() - 21, null);
            g2D.drawImage(getNodeImage(v.getImageName(), "path", 50, 50), v.getX(), v.getY(), null);
        }
    },
    TARGET() {
        public void draw(Graphics2D g2D, Vertex v) {
            g2D.drawImage(getNodeImage(v.getImageName(), "path", 50, 50), v.getX(), v.getY(), null);
            g2D.drawImage(targetMark, v.getX() - 15, v.getY() - 15, null);
        }
    };

    private static final Image raw = getSpecialImage("white fancy slim", 60, 60);
    private static final Image connected = getSpecialImage("green fancy slim", 60, 60);
    private static final Image visited = getSpecialImage("blue fancy slim", 60, 60);
    private static final Image path = getSpecialImage("orange fancy slim", 70, 70);
    private static final Image rootNode = getSpecialImage("root node", 100, 100);
    private static final Image targetMark = getSpecialImage("orange layered", 80, 80);
    private static final Image dashedMark = getSpecialImage("green dashed", 60, 60);

    abstract void draw(Graphics2D g2D, Vertex v);

    private static Image getNodeImage(String imageName, String currentState, int width, int height) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/nodes/%s/%s.png",
                currentState, imageName)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
    }

    private static Image getSpecialImage(String imageName, int width, int height) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/special/%s.png", imageName))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
    }
}