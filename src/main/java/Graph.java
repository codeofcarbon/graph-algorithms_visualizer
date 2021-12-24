import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;

public class Graph extends JPanel {
    protected AlgorithmMode algorithmMode = AlgorithmMode.NONE;
    protected Mode mode = Mode.ADD_A_VERTEX;
    protected final GraphService service;
    protected final JLabel displayLabel;
    protected final JLabel modeLabel;

    public Graph(JLabel modeLabel, JLabel displayLabel) {
        this.modeLabel = modeLabel;
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
        var edges = service.connects.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        for (var e : edges) {
            g.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(4f));
            g2d.drawLine(e.first.getX() + 25, e.first.getY() + 25, e.second.getX() + 25, e.second.getY() + 25);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier", Font.ITALIC, 20));
            boolean correct = Math.abs(e.first.getX() - e.second.getX()) > Math.abs(e.first.getY() - e.second.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.first.getX() + 25 + e.second.getX() + 25) / 2 + (correct ? -10 : 10),
                    (e.first.getY() + 25 + e.second.getY() + 25) / 2 + (correct ? 25 : 10));
        }
        for (var v : service.connects.keySet()) {
            g.setColor(Color.DARK_GRAY.darker());
            g.fillOval(v.getX(), v.getY(), 50, 50);
            g.setColor(Color.CYAN.darker());
            g.setFont(new Font("Courier", Font.BOLD, 30));
            g.drawString(v.vertexID.getText(), v.getX() + 17, v.getY() + 36);
        }
    }
}

enum Mode {
    ADD_A_VERTEX("Add a Vertex"),
    ADD_AN_EDGE("Add an Edge"),
    REMOVE_A_VERTEX("Remove a Vertex"),
    REMOVE_AN_EDGE("Remove an Edge"),
    NONE("None");

    final String current;

    Mode(String current) {
        this.current = current;
    }
}

enum AlgorithmMode {
    DEPTH_FIRST_SEARCH,
    BREADTH_FIRST_SEARCH,
    DIJKSTRA_ALGORITHM,
    PRIM_ALGORITHM,
    NONE
}