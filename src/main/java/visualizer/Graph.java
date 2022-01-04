package visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Graph extends JPanel {
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    GraphMode graphMode = GraphMode.ADD_A_VERTEX;
    GraphService service;

    public Graph() {
        setName("Graph");
        setBackground(Color.BLACK);
        setSize(800, 600);
        setLayout(null);
        addListeners();
    }

    private void addListeners() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (graphMode == GraphMode.ADD_A_VERTEX) service.createNewVertex(e);
                if (graphMode == GraphMode.ADD_AN_EDGE) service.createNewEdge(e);
                if (graphMode == GraphMode.REMOVE_A_VERTEX) service.removeVertex(e);
                if (graphMode == GraphMode.REMOVE_AN_EDGE) service.removeEdge(e);
                if (graphMode == GraphMode.NONE) {
                    if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(e);
                }
            }
        });

        MouseAdapter mouseDrag = new MouseAdapter() {
            MoveCommand moveCommand;

            @Override
            public void mouseDragged(MouseEvent e) {
                if (moveCommand == null) {
                    service.checkIfVertex(e).ifPresent(vertex -> {
                        moveCommand = new MoveCommand(vertex);
                        moveCommand.start(e.getX(), e.getY());
                        moveCommand.move(e.getX(), e.getY());
                        repaint();
                    });
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || moveCommand == null) {
                    return;
                }
                moveCommand.stop(e.getX(), e.getY());
                moveCommand.execute();
                this.moveCommand = null;
                repaint();
            }
        };
        addMouseListener(mouseDrag);
        addMouseMotionListener(mouseDrag);
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