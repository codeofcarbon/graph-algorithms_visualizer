package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.undo.StateEditable;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

@Getter
public class Node extends JLabel implements Serializable, StateEditable {
    private static final long serialVersionUID = 12345L;
    private final String imageName;
    private final List<Edge> connectedEdges;
    private final int radius = 25;
    private final String id;
    boolean visited, marked, connected, path;
    int distance;

    public Node(String id, Point center, List<Edge> connectedEdges) {
        setName("Vertex " + id);
        this.id = id;
        this.imageName = id.matches("[a-z]") ? id.concat("_lower")
                : id.matches("[A-Z]") ? id.concat("_upper") : id;
        this.connectedEdges = connectedEdges;
        setLocation(center.x - radius, center.y - radius);
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setOpaque(false);
    }

    protected NodeState getState() {
        return Algorithm.root == this ? NodeState.ROOT
                : Algorithm.target == this ? NodeState.TARGET
                : this.path ? NodeState.PATH
                : this.visited ? NodeState.VISITED
                : this.connected ? NodeState.CONNECTED
                : NodeState.RAW;
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

enum NodeState {
    RAW() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "raw", 40, 40), node.getX() + 5, node.getY() + 5, null);
            if (node.marked) g2D.drawImage(dashedMark, node.getX() - 5, node.getY() - 5, null);
            else g2D.drawImage(raw, node.getX() - 5, node.getY() - 5, null);
        }
    },
    CONNECTED() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "connected", 40, 40), node.getX() + 5, node.getY() + 5, null);
            if (node.marked) g2D.drawImage(dashedMark, node.getX() - 5, node.getY() - 5, null);
            else g2D.drawImage(connected, node.getX() - 5, node.getY() - 5, null);
        }
    },
    VISITED() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "visited", 40, 40), node.getX() + 5, node.getY() + 5, null);
            g2D.drawImage(visited, node.getX() - 5, node.getY() - 5, null);
        }
    },
    PATH() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "path", 50, 50), node.getX(), node.getY(), null);
            g2D.drawImage(path, node.getX() - 10, node.getY() - 10, null);
        }
    },
    ROOT() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(rootNode, node.getX() - 21, node.getY() - 21, null);
            g2D.drawImage(getNodeImage(node.getImageName(), "path", 50, 50), node.getX(), node.getY(), null);
        }
    },
    TARGET() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "path", 50, 50), node.getX(), node.getY(), null);
            g2D.drawImage(targetMark, node.getX() - 15, node.getY() - 15, null);
        }
    };

    private static final Image raw = getSpecialImage("white slim", 60, 60);
    private static final Image connected = getSpecialImage("green slim", 60, 60);
    private static final Image visited = getSpecialImage("blue slim", 60, 60);
    private static final Image path = getSpecialImage("orange slim", 70, 70);
    private static final Image rootNode = getSpecialImage("root node", 100, 100);
    private static final Image targetMark = getSpecialImage("orange layered", 80, 80);
    private static final Image dashedMark = getSpecialImage("green dashed", 60, 60);

    abstract void draw(Graphics2D g2D, Node node);

    private static Image getNodeImage(String imageName, String currentState, int width, int height) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/nodes/%s/%s.png",
                currentState, imageName)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
    }

    private static Image getSpecialImage(String imageName, int width, int height) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/special/%s.png", imageName))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
    }
}