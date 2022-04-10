package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.undo.StateEditable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
    int distance = Integer.MAX_VALUE;

    private float alpha = 1.0f;
    final Timer timer;
//    private Long startTime;
    private long startTime = -1;
    private static final long RUNNING = 2000;
    private static final long duration = 2000;

    public Node(String id, Point center, List<Edge> connectedEdges) {
        setName("Node " + id);
        this.id = id;
        this.imageName = id.matches("[a-z]") ? id.concat("_lower")
                : id.matches("[A-Z]") ? id.concat("_upper") : id;
        this.connectedEdges = connectedEdges;
        setLocation(center.x - radius, center.y - radius);
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setOpaque(false);

//        final Timer
        timer = new Timer(40, e -> {
            if (startTime < 0) {
                startTime = System.currentTimeMillis();
            }
//            else {

                long time = System.currentTimeMillis();
                long duration = time - startTime;
                if (duration >= RUNNING) {
                    startTime = -1;
                    ((Timer) e.getSource()).stop();
                    alpha = 0f;
                } else {
                    alpha = 1f - ((float) duration / (float) RUNNING);
                }
                repaint();
//            }
        });
//        addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                timer.start();
//            }
//
//        });
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
        getParent().repaint();                                                                       // todo (remove?)
    }

//    public void fade() {
//        timer = new Timer(5, e -> {
//            if (startTime == null) startTime = System.currentTimeMillis();
//            var diff = System.currentTimeMillis() - startTime;
//            alpha = 1.0f - (float) diff / duration;
//            if (alpha < 0) {
//                timer.stop();
//                alpha = 0.0f;
//            }
//            repaint();
//        });
//        timer.start();
//    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g.create();
        g2D.setComposite(AlphaComposite.SrcOver.derive(alpha));
        getState().draw(g2D, this);
    }
}

enum NodeState {
    RAW() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "raw", 36, 36), 7, 7, null);
            if (node.marked) g2D.drawImage(dashedMark, 0, 0, null);
            else g2D.drawImage(raw, 0, 0, null);
        }
    },
    CONNECTED() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "connected", 36, 36), 7, 7, null);
            if (node.marked) g2D.drawImage(dashedMark, 0, 0, null);
            else g2D.drawImage(connected, 0, 0, null);
        }
    },
    VISITED() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "visited", 36, 36), 7, 7, null);
            g2D.drawImage(visited, 0, 0, null);
        }
    },
    PATH() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "path", 46, 46), 2, 2, null);
            g2D.drawImage(path, -5, -5, null);
        }
    },
    ROOT() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(rootNode, -16, -16, null);
            g2D.drawImage(getNodeImage(node.getImageName(), "path", 46, 46), 2, 2, null);
        }
    },
    TARGET() {
        public void draw(Graphics2D g2D, Node node) {
            g2D.drawImage(getNodeImage(node.getImageName(), "path", 46, 46), 2, 2, null);
            g2D.drawImage(targetMark, -10, -10, null);
        }
    };

    private static final Image raw = IconMaker.loadIcon("white slim", "special", 50, 50).getImage();
    private static final Image connected = IconMaker.loadIcon("green slim", "special", 50, 50).getImage();
    private static final Image visited = IconMaker.loadIcon("blue slim", "special", 50, 50).getImage();
    private static final Image path = IconMaker.loadIcon("orange slim", "special", 60, 60).getImage();
    private static final Image rootNode = IconMaker.loadIcon("root node", "special", 90, 90).getImage();
    private static final Image targetMark = IconMaker.loadIcon("orange layered", "special", 70, 70).getImage();
    private static final Image dashedMark = IconMaker.loadIcon("green dashed", "special", 50, 50).getImage();

    abstract void draw(Graphics2D g2D, Node node);

    private static Image getNodeImage(String imageName, String currentState, int width, int height) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/nodes/%s/%s.png",
                currentState, imageName)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
    }
}