package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Graph extends JPanel {
    final List<Vertex> vertices = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();               // todo can it be final after removing a connects map ?
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
            if (e.visited) {
                g.setColor(Color.WHITE);
                g2d.drawLine(e.first.getX(), e.first.getY(), e.second.getX(), e.second.getY());
                g.setColor(Color.GREEN);
            } else if (e.hidden) {
                g.setColor(Color.BLACK);
                g2d.drawLine(e.first.getX(), e.first.getY(), e.second.getX(), e.second.getY());
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.DARK_GRAY);
                g2d.drawLine(e.first.getX(), e.first.getY(), e.second.getX(), e.second.getY());
                g.setColor(Color.LIGHT_GRAY);
            }
            g.setFont(new Font("Courier", Font.PLAIN, 20));
            boolean correct = Math.abs(e.first.getX() - e.second.getX()) > Math.abs(e.first.getY() - e.second.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.first.getX() + e.second.getX()) / 2 + (correct ? -10 : 10),
                    (e.first.getY() + e.second.getY()) / 2 + (correct ? 25 : 10));
        }

        g2d.setStroke(new BasicStroke(1f));
        for (var v : vertices) {
            if (v.marked) {
                g.setColor(Color.RED);
                v.midPoint(g);
                g.setColor(Color.DARK_GRAY);
                g.fillOval(v.getX() - 14, v.getY() - 14, 30, 30);
                g.setColor(Color.WHITE);
            } else if (v.visited) {
                g.setColor(Color.WHITE);
                v.midPoint(g);
                g.setColor(Color.GREEN);
                g.fillOval(v.getX() - 22, v.getY() - 22, 45, 45);
                g.setColor(Color.WHITE);
            } else if (v.connected) {
                g.setColor(Color.GREEN);
                v.midPoint(g);
                g.setColor(Color.WHITE);
                g.fillOval(v.getX() - 19, v.getY() - 19, 40, 40);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                v.midPoint(g);
                g.setColor(Color.DARK_GRAY);
                g.fillOval(v.getX() - 14, v.getY() - 14, 30, 30);
                g.setColor(Color.WHITE);
            }
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

enum AlgorithmMode {                                     // todo maybe implement command pattern ?
    DEPTH_FIRST_SEARCH("Depth First Search"),
//                      (){
//        @Override
//        public Algorithm algorithm() {
//            return new Algorithm();
//        }
//    },
    BREADTH_FIRST_SEARCH("Breadth First Search"),
    DIJKSTRA_ALGORITHM("Dijkstra's Algorithm"),
    PRIM_ALGORITHM("Prim's Algorithm"),
    NONE("None");

    final String current;
    //    Runnable algorithm;

    AlgorithmMode(String current/*, Runnable algorithm*/) {
        this.current = current;
        //        this.algorithm = algorithm;
    }

    //    public abstract Algorithm algorithm();
}

enum State {                                // todo states of node (and edges ?)
    UNUSED,
    CONNECTED,
    MARKED,
    VISITED,
    HIDDEN
}