package visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

@Getter
public class Graph extends JPanel {
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    GraphService service;

    public Graph() {
        setName("Graph");
        setBackground(Color.BLACK);
        setSize(800, 600);
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (service.getGraphMode() == GraphMode.ADD_A_VERTEX) service.createNewVertex(e);
                if (service.getGraphMode() == GraphMode.ADD_AN_EDGE) service.createNewEdge(e);
                if (service.getGraphMode() == GraphMode.REMOVE_A_VERTEX) service.removeVertex(e);
                if (service.getGraphMode() == GraphMode.REMOVE_AN_EDGE) service.removeEdge(e);
                if (service.getGraphMode() == GraphMode.NONE) {
                    if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(e);
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(3f));
        for (var e : edges) {
            e.getState().coloring(g, g2d, e);
            g.setFont(new Font("Courier", Font.PLAIN, 20));
            boolean align = Math.abs(e.source.getX() - e.target.getX()) > Math.abs(e.source.getY() - e.target.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.source.getX() + e.target.getX()) / 2 + (align ? -10 : 10),
                    (e.source.getY() + e.target.getY()) / 2 + (align ? 25 : 10));
        }

        g2d.setStroke(new BasicStroke(1f));
        for (var v : vertices) {
            v.getState().coloring(g, v);
            g.setFont(new Font("Courier", Font.ITALIC, 30));
            g.drawString(v.id, v.center.x - 8, v.center.y + 12);
        }
    }
}