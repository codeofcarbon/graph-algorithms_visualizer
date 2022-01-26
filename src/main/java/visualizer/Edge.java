package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.undo.StateEditable;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Hashtable;

@Getter
public class Edge extends JLabel implements Serializable , StateEditable {
    private static final long serialVersionUID = 123L;
    private Vertex source, target;
    final int weight;
    boolean visited, hidden, path;
    Edge mirrorEdge;

    public Edge(Vertex source, Vertex target, int weight) {
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

    @Override
    public void storeState(Hashtable<Object, Object> state) {               // todo do i need that??
        state.put("SourceNode", source);
        state.put("TargetNode", target);
    }

    @Override
    public void restoreState(Hashtable<?, ?> state) {
        var sourceNode = (Vertex) state.get("SourceNode");
        if (sourceNode != null) this.source = sourceNode;
        var targetNode = (Vertex) state.get("TargetNode");
        if (targetNode != null) this.target = targetNode;
    }
}

enum EdgeState {
    RAW() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(60, 60, 60, 255));
            drawEdge(g, g2d, edge);
        }
    },
    VISITED() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(20, 80, 230, 255));
            drawEdge(g, g2d, edge);
        }
    },
    HIDDEN() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(0, 0, 0, 0));
            edge.setForeground(new Color(0, 0, 0, 0));
        }
    },
    PATH() {
        public void draw(Graphics g, Graphics2D g2d, Edge edge) {
            g.setColor(new Color(255, 87, 34, 255));
            drawEdge(g, g2d, edge);
        }
    };

    static final Image labelCircle = new ImageIcon(
            new ImageIcon("src/main/resources/icons/special/label circle.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)).getImage();

    abstract void draw(Graphics g, Graphics2D g2d, Edge edge);

    private static void drawEdge(Graphics g, Graphics2D g2d, Edge edge) {
        var line = getLine(edge);
        g2d.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
        var midpoint = getMidpoint(line);
        g.drawImage(labelCircle, midpoint.x - 15, midpoint.y - 15, null);
        edge.setForeground(Color.CYAN);
        edge.setLocation(midpoint.x - 15, midpoint.y - 15);
    }

    private static Line2D.Double getLine(Edge edge) {
        var source = edge.getSource();
        var target = edge.getTarget();
        return new Line2D.Double(
                source.getX() + source.radius, source.getY() + source.radius,
                target.getX() + target.radius, target.getY() + target.radius);
    }

    private static Point getMidpoint(Line2D.Double line) {
        return new Point((int) ((line.getX1() + line.getX2()) / 2), (int) ((line.getY1() + line.getY2()) / 2));
    }
}