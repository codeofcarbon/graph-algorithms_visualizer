package visualizer;

import javax.swing.*;
import java.awt.*;

public class Vertex extends JPanel {
    final String id;
    final JLabel vertexID;
    final Point center;
    private final float radius;
    boolean marked, connected, visited;
    int distance;

    public Vertex(String id, Point center) {
        this.id = id;
        this.center = center;
        this.radius = 25f;
        setName("Vertex " + id);
        setLocation(center);
        this.vertexID = new JLabel(id);
        vertexID.setName("VertexLabel " + id);
        add(vertexID);
    }

    void midPoint(Graphics g) {                                     // todo do sth with that
        int x = 0, y = (int) radius;
        int d = (int) (1 - radius);
        int c1 = 3, c2 = (int) (5 - 2 * radius);
        drawEightPoints(g, x, y);
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
            drawEightPoints(g, x, y);
        }
    }

    void drawEightPoints(Graphics g, int a, int b) {
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