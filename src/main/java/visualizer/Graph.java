package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class Graph extends JPanel {
    final List<Vertex> vertices = new ArrayList<>();
    final List<Edge> edges = new ArrayList<>();
    AlgorithmMode algorithmMode = AlgorithmMode.NONE;
    Mode mode = Mode.ADD_A_VERTEX;
    final GraphService service;
    final JLabel displayLabel;
    final JLabel algorithmModeLabel;
    final JLabel modeLabel;

    public Graph(JLabel modeLabel, JLabel algorithmModeLabel, JLabel displayLabel) {
        this.modeLabel = modeLabel;
        this.algorithmModeLabel = algorithmModeLabel;
        this.displayLabel = displayLabel;
        this.service = new GraphService(this);
        setName("Graph");
        setBackground(Color.BLACK);
        setSize(800, 600);
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (mode == Mode.ADD_A_VERTEX) service.createNewVertex(e);
                if (mode == Mode.ADD_AN_EDGE) service.createNewEdge(e);
                if (mode == Mode.REMOVE_A_VERTEX) service.removeVertex(e);
                if (mode == Mode.REMOVE_AN_EDGE) service.removeEdge(e);
                if (mode == Mode.NONE) {
                    if (algorithmMode != AlgorithmMode.NONE) service.startAlgorithm(e);
                }
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3f));
        for (var e : edges) {
            e.getState().coloring(g, g2d, e);
            g.setFont(new Font("Courier", Font.PLAIN, 20));
            boolean correct = Math.abs(e.source.getX() - e.target.getX()) > Math.abs(e.source.getY() - e.target.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.source.getX() + e.target.getX()) / 2 + (correct ? -10 : 10),
                    (e.source.getY() + e.target.getY()) / 2 + (correct ? 25 : 10));
        }

        g2d.setStroke(new BasicStroke(1f));
        for (var v : vertices) {
            v.getState().coloring(g, v);
            g.setFont(new Font("Courier", Font.ITALIC, 30));
            g.drawString(v.vertexID.getText(), v.center.x - 9, v.center.y + 12);
        }
    }
}

enum Mode {
    ADD_A_VERTEX("Add a Vertex  "),
    ADD_AN_EDGE("Add an Edge  "),
    REMOVE_A_VERTEX("Remove a Vertex  "),
    REMOVE_AN_EDGE("Remove an Edge  "),
    NONE("None  ");

    final String current;

    Mode(String current) {
        this.current = current;
    }
}

enum AlgorithmMode {
    DEPTH_FIRST_SEARCH("Depth First Search"),
    BREADTH_FIRST_SEARCH("Breadth First Search"),
    DIJKSTRA_ALGORITHM("Dijkstra's Algorithm"),
    PRIM_ALGORITHM("Prim's Algorithm"),
    NONE("None");

    final String current;

    AlgorithmMode(String current) {
        this.current = current;
    }
// todo maybe implement command pattern ?
//                      (){
//        @Override
//        public Algorithm algorithm() {
//            return new Algorithm();
//        }
// }
}