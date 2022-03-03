package visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

@Getter
public class Edge extends JLabel implements Serializable {
    private static final long serialVersionUID = 123L;
    private final Node source, target;
    final int weight;
    boolean visited, hidden, path;
    Edge mirrorEdge;

    public Edge(Node source, Node target, int weight) {
        super(String.valueOf(weight), JLabel.CENTER);
        setName(String.format("Edge <%s -> %s>", source.id, target.id));
        this.source = source;
        this.target = target;
        this.weight = weight;
        var midpoint = new Point((source.getX() + source.radius + target.getX() + target.radius) / 2,
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

    protected Line2D.Double getLine() {
        var r = getSource().getRadius();
        var source = getSource().getLocation();
        var target = getTarget().getLocation();
        return new Line2D.Double(source.x + r, source.y + r, target.x + r, target.y + r);
    }

    protected Point getMidpoint(Line2D.Double line) {
        return new Point((int) ((line.x1 + line.x2) / 2), (int) ((line.y1 + line.y2) / 2));
    }
}

enum EdgeState {
    RAW() {
        public void draw(Graphics2D g2D, Edge edge) {
            g2D.setColor(new Color(60, 60, 60, 255));
            drawEdge(g2D, edge);
        }
    },
    VISITED() {
        public void draw(Graphics2D g2D, Edge edge) {
            g2D.setColor(new Color(20, 80, 230, 255));
            drawEdge(g2D, edge);
        }
    },
    HIDDEN() {
        public void draw(Graphics2D g2D, Edge edge) {
            g2D.setColor(new Color(0, 0, 0, 0));
            edge.setForeground(new Color(0, 0, 0, 0));
        }
    },
    PATH() {
        public void draw(Graphics2D g2D, Edge edge) {
            g2D.setColor(new Color(255, 87, 34, 255));
            drawEdge(g2D, edge);
        }
    };

    private static final Image labelCircle = new ImageIcon(
            new ImageIcon("src/main/resources/icons/special/label circle.png")
                    .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)).getImage();

    abstract void draw(Graphics2D g2D, Edge edge);

    private static void drawEdge(Graphics2D g2D, Edge edge) {
        var line = edge.getLine();
        g2D.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);
        var midpoint = edge.getMidpoint(line);
        g2D.drawImage(labelCircle, midpoint.x - 15, midpoint.y - 15, null);
        edge.setForeground(Color.CYAN);
        edge.setLocation(midpoint.x - 15, midpoint.y - 15);
    }
}