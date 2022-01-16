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
    final String imageName;
    final String id;
    int distance;

    public Vertex(String id, Point center) {
        setName("Vertex " + id);
        this.id = id;
        this.imageName = id.matches("[a-z]") ? id.concat("_lower")
                : id.matches("[A-Z]") ? id.concat("_upper") : id;
        this.connectedEdges = new ArrayList<>();
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
}

enum VertexState {
    RAW() {
        public void draw(Graphics g, Vertex v) {
            g.drawImage(getNodeImage(v.imageName, "raw", 40, 40), v.getX() + 5, v.getY() + 5, null);
            if (v.marked) g.drawImage(dashedMark, v.getX() - 5, v.getY() - 5, null);
            else g.drawImage(raw, v.getX() - 5, v.getY() - 5, null);
        }
    },
    CONNECTED() {
        public void draw(Graphics g, Vertex v) {
            g.drawImage(getNodeImage(v.imageName, "connected", 40, 40), v.getX() + 5, v.getY() + 5, null);
            if (v.marked) g.drawImage(dashedMark, v.getX() - 5, v.getY() - 5, null);
            else g.drawImage(connected, v.getX() - 5, v.getY() - 5, null);
        }
    },
    VISITED() {
        public void draw(Graphics g, Vertex v) {
            g.drawImage(getNodeImage(v.imageName, "visited", 40, 40), v.getX() + 5, v.getY() + 5, null);
            g.drawImage(visited, v.getX() - 5, v.getY() - 5, null);
        }
    },
    PATH() {
        public void draw(Graphics g, Vertex v) {
            g.drawImage(getNodeImage(v.imageName, "path", 50, 50), v.getX(), v.getY(), null);
            g.drawImage(path, v.getX() - 10, v.getY() - 10, null);
        }
    },
    ROOT() {
        public void draw(Graphics g, Vertex v) {
            g.drawImage(rootNode, v.getX() - 21, v.getY() - 21, null);
            g.drawImage(getNodeImage(v.imageName, "path", 50, 50), v.getX(), v.getY(), null);
        }
    },
    TARGET() {
        public void draw(Graphics g, Vertex v) {
            g.drawImage(getNodeImage(v.imageName, "path", 50, 50), v.getX(), v.getY(), null);
            g.drawImage(targetMark, v.getX() - 15, v.getY() - 15, null);
        }
    };

    final Image raw = getSpecialImage("white fancy slim", 60, 60);
    final Image connected = getSpecialImage("green fancy slim", 60, 60);
    final Image visited = getSpecialImage("blue fancy slim", 60, 60);
    final Image path = getSpecialImage("orange fancy slim", 70, 70);
    final Image rootNode = getSpecialImage("root node", 100, 100);
    final Image targetMark = getSpecialImage("orange layered", 80, 80);
    final Image dashedMark = getSpecialImage("green dashed", 60, 60);

    abstract void draw(Graphics g, Vertex v);

     private static Image getNodeImage(String imageName, String currentState, int width, int height) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/nodes/%s/%s.png",
                currentState, imageName)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
    }

    private Image getSpecialImage(String imageName, int targetWidth, int targetHeight) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/special/%s.png", imageName))
                .getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)).getImage();
    }
}