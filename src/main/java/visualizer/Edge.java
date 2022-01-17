package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class Edge extends JLabel implements Serializable {
    private static final long serialVersionUID = 123L;
    final Vertex source;
    final Vertex target;
    final int weight;
    boolean visited, hidden, path;
    Edge mirrorEdge;
    Point midpoint;

    public Edge(Vertex source, Vertex target, int weight) {
        super(String.valueOf(weight), JLabel.CENTER);
        setName(String.format("Edge <%s -> %s>", source.id, target.id));
        this.source = source;
        this.target = target;
        this.weight = weight;
        midpoint = new Point((source.getX() + source.radius + target.getX() + target.radius) / 2,
                (source.getY() + source.radius + target.getY() + target.radius) / 2);
        setLocation(midpoint.x - 15, midpoint.y - 15);
        setPreferredSize(new Dimension(30, 30));
        setSize(getPreferredSize());
        setOpaque(false);
    }

    protected EdgeState getState() {
        return this.path ? EdgeState.PATH
                : this.visited ? EdgeState.VISITED
                : this.hidden ? EdgeState.HIDDEN
                : EdgeState.RAW;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setForeground(Color.WHITE);
        setFont(new Font("Courier", Font.PLAIN, 10));
    }
}

enum EdgeState {
    RAW() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(60, 60, 60, 255));
            var line = drawEdge(g2d, edge);
            edge.midpoint = getMidpoint(line);
            g.drawImage(labelCircle, edge.midpoint.x - 15, edge.midpoint.y - 15, null);
            edge.setLocation(edge.midpoint.x - 15, edge.midpoint.y - 15);
        }
    },
    VISITED() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(20, 80, 230, 255));
            var line = drawEdge(g2d, edge);
            edge.midpoint = getMidpoint(line);
            g.drawImage(labelCircle, edge.midpoint.x - 15, edge.midpoint.y - 15, null);
            edge.setLocation(edge.midpoint.x - 15, edge.midpoint.y - 15);
        }
    },
    HIDDEN() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(60, 60, 60, 0));
            edge.setForeground(new Color(60, 60, 60, 0));
        }
    },
    PATH() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(255, 87, 34, 255));
            var line = drawEdge(g2d, edge);
            edge.midpoint = getMidpoint(line);
            g.drawImage(labelCircle, edge.midpoint.x - 15, edge.midpoint.y - 15, null);
            edge.setLocation(edge.midpoint.x - 15, edge.midpoint.y - 15);
        }
    };

    abstract void draw(Graphics g, Graphics2D g2d, Edge edge);

    final Image labelCircle = getLabel("label circle", 30, 30);

    private static Line2D.Double drawEdge(Graphics2D g2d, Edge edge) {
        var line = new Line2D.Double(
                edge.source.getX() + edge.source.radius, edge.source.getY() + edge.source.radius,
                edge.target.getX() + edge.target.radius, edge.target.getY() + edge.target.radius);
        g2d.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
        return line;
    }

    private static Point getMidpoint(Line2D.Double line) {
        return new Point((int) ((line.getX1() + line.getX2()) / 2), (int) ((line.getY1() + line.getY2()) / 2));
    }

    private Image getLabel(String imageName, int targetWidth, int targetHeight) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/special/%s.png", imageName))
                .getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)).getImage();
    }
}