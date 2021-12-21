import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Graph extends JPanel {
    protected Vertex edgeFrom, edgeTo;
    protected Mode mode = Mode.ADD_A_VERTEX;
    protected JLabel modeLabel;
    protected final List<Vertex> vertices = new ArrayList<>();
    protected final List<Edge> edges = new ArrayList<>();

    public Graph(JLabel modeLabel) {
        this.modeLabel = modeLabel;
        setName("Graph");
        setBackground(Color.BLACK);
        setSize(800, 600);
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (mode == Mode.ADD_A_VERTEX) createNewVertex(e);
                if (mode == Mode.ADD_AN_EDGE) createNewEdge(e);
            }
        });
    }

    protected void switchMode(Mode mode) {
        this.mode = mode;
        this.modeLabel.setText("Current Mode -> " + mode.current);
        modeLabel.repaint();
        edgeTo = null;
        edgeFrom = null;
    }

    private void resetVertices() {
        repaint();
        edgeFrom = null;
        edgeTo = null;
    }

    private void createNewEdge(MouseEvent e) {
        if (edgeFrom == null) {
            var vOne = vertices.stream().filter(v -> e.getPoint().distance(v.getX() + 25, v.getY() + 25) < 25).findAny();
            vOne.ifPresent(v -> {
                edgeFrom = v;
                edgeFrom.repaint();
            });
            return;
        }
        if (edgeTo == null) {
            var vTwo = vertices.stream().filter(v -> e.getPoint().distance(v.getX() + 25, v.getY() + 25) < 25).findAny();
            vTwo.ifPresent(v -> {
                edgeTo = v;
                edgeTo.repaint();
                if (edges.stream().anyMatch(edge -> edgeFrom.equals(edgeTo) ||
                                                    edge.first.equals(edgeFrom) && edge.second.equals(edgeTo)
                                                    || edge.first.equals(edgeTo) && edge.second.equals(edgeFrom))) {
                    resetVertices();
                    return;
                }
                while (true) {
                    var out = JOptionPane.showInputDialog(this, "Enter Weight", "Input",
                            JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (out == null) {
                        resetVertices();
                        return;
                    }
                    try {
                        int weight = Integer.parseInt(out.toString());
                        var edge = new Edge(edgeFrom, edgeTo, weight);
                        var reverseEdge = new Edge(edgeTo, edgeFrom, weight);
                        add(edge);
                        add(reverseEdge);
                        add(edge.edgeLabel);
                        edges.add(edge);
                        repaint();
                        resetVertices();
                        return;
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
        }
    }

    private void createNewVertex(MouseEvent e) {
        var taken = vertices.stream().filter(v -> e.getPoint().distance(v.getX() + 25, v.getY() + 25) < 25).findAny();
        if (taken.isEmpty()) {
            var out = JOptionPane.showInputDialog(this, "Enter the Vertex ID (Should be 1 char):", "Vertex",
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (out == null) return;
            String id = out.toString();
            if (id.length() == 1 && !id.isBlank()) {
                Vertex vertex = new Vertex(id);
                vertex.setLocation(e.getX() - 25, e.getY() - 25);
                vertices.add(vertex);
                add(vertex);
                repaint();
                revalidate();
            } else createNewVertex(e);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
//        modeLabel.repaint();
        Graphics2D g2d = (Graphics2D) g;
        for (var e : edges) {
            g.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(4f));
            g2d.drawLine(e.first.getX() + 25, e.first.getY() + 25, e.second.getX() + 25, e.second.getY() + 25);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier", Font.ITALIC, 20));
            boolean needCorrect = Math.abs(e.first.getX() - e.second.getX()) > Math.abs(e.first.getY() - e.second.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.first.getX() + 25 + e.second.getX() + 25) / 2 + (needCorrect ? -5 : 15),
                    (e.first.getY() + 25 + e.second.getY() + 25) / 2 + (needCorrect ? 20 : 10));
        }
        for (var v : vertices) {
            g.setColor(Color.DARK_GRAY.darker());
            g.fillOval(v.getX() - 1, v.getY() - 1, 51, 51);
            g.setColor(Color.CYAN.darker());
            g.setFont(new Font("Courier", Font.BOLD, 30));
            g.drawString(v.vertexID.getText(), v.getX() + 17, v.getY() + 36);
        }
    }
}

enum Mode {
    ADD_A_VERTEX("Add a Vertex"),
    ADD_AN_EDGE("Add an Edge"),
    NONE("None");

    final String current;
    private JLabel modeLabel;

    Mode(String current) {
        this.current = current;
        modeLabel = new JLabel("Current Mode -> " + current, SwingConstants.RIGHT);
    }
}