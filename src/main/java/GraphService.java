import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;

public class GraphService {
    private Vertex edgeFrom, edgeTo, rootNode;
    private LinkedList<Vertex> nextLevel = new LinkedList<>();

    private final Algorithm algorithm;
    private final Graph graph;
    private Timer timer;

    public GraphService(Graph graph) {
        this.graph = graph;
        this.algorithm = new Algorithm(graph.connects);
    }

    protected void startAlgorithm(MouseEvent e) {
        checkIfTheClickPointIsOnTheVertex(e)
                .ifPresent(start -> {
                    if (graph.algorithmMode == AlgorithmMode.DEPTH_FIRST_SEARCH
                        || graph.algorithmMode == AlgorithmMode.DIJKSTRA_ALGORITHM) rootNode = start;
                    if (graph.algorithmMode == AlgorithmMode.BREADTH_FIRST_SEARCH) nextLevel.add(start);
                    graph.displayLabel.setText("Please wait...");
                });
        if (graph.algorithmMode == AlgorithmMode.DEPTH_FIRST_SEARCH) {
            timer = new Timer(1000, event -> {
                var nextNode = algorithm.dfsAlgorithm(rootNode);
                if (nextNode == null) {
                    graph.displayLabel.setText("DFS : " + algorithm.nodesResult.toString());
                    timer.stop();
                } else rootNode = nextNode;
            });
            timer.start();
        }
        if (graph.algorithmMode == AlgorithmMode.BREADTH_FIRST_SEARCH) {
            timer = new Timer(1000, event -> {
                var nextLevelNodes = algorithm.bfsAlgorithm(nextLevel);
                if (nextLevelNodes == null) {
                    graph.displayLabel.setText("BFS : " + algorithm.nodesResult.toString());
                    timer.stop();
                } else nextLevel = nextLevelNodes;
            });
            timer.start();
        }
        if (graph.algorithmMode == AlgorithmMode.DIJKSTRA_ALGORITHM) {
            algorithm.dijkstraAlgorithm(rootNode);
            timer = new Timer(3000, event -> {
                graph.displayLabel.setText(algorithm.dijkstraResult);
                timer.stop();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    protected void createNewVertex(MouseEvent e) {
        if (checkIfTheClickPointIsOnTheVertex(e).isEmpty()) {
            var input = JOptionPane.showInputDialog(graph, "Enter the Vertex ID (Should be 1 char):", "Vertex",
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input == null) return;
            String id = input.toString();
            if (id.length() == 1 && !id.isBlank()) {
                Vertex vertex = new Vertex(id);
                vertex.setLocation(e.getX() - 25, e.getY() - 25);
                graph.add(vertex);
                graph.repaint();
                vertex.revalidate();
                graph.connects.put(vertex, new ArrayList<>());
            } else createNewVertex(e);
        }
    }

    protected void createNewEdge(MouseEvent e) {
        if (edgeFrom == null) {
            checkIfTheClickPointIsOnTheVertex(e).ifPresent(first -> {
                edgeFrom = first;
                edgeFrom.repaint();
            });
            return;
        }
        if (edgeTo == null) {
            checkIfTheClickPointIsOnTheVertex(e).ifPresent(second -> {
                if (edgeFrom.equals(edgeTo) || graph.connects.get(edgeFrom).stream()
                        .anyMatch(edge -> edge.second.equals(edgeTo))) {
                    resetVertices();
                    return;
                }
                edgeTo = second;
                edgeTo.repaint();
                while (true) {
                    var input = JOptionPane.showInputDialog(graph, "Enter Weight", "Input",
                            JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (input == null) {
                        resetVertices();
                        return;
                    }
                    try {
                        int weight = Integer.parseInt(input.toString());
                        var edge = new Edge(edgeFrom, edgeTo, weight);
                        var reverseEdge = new Edge(edgeTo, edgeFrom, weight);
                        graph.add(edge);
                        graph.add(reverseEdge);
                        graph.add(edge.edgeLabel);
                        graph.connects.get(edgeFrom).add(edge);
                        graph.connects.get(edgeTo).add(reverseEdge);
                        edge.repaint();
                        edge.edgeLabel.repaint();
                        resetVertices();
                        return;
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
        }
    }

    protected void removeVertex(MouseEvent e) {
        checkIfTheClickPointIsOnTheVertex(e).ifPresent(vertex -> {
            graph.connects.get(vertex).stream()
                    .peek(edge -> {
                        if (edge.edgeLabel != null) graph.remove(edge.edgeLabel);
                    })
                    .forEach(graph::remove);
            graph.connects.remove(vertex);
            graph.remove(vertex);
            graph.repaint();
        });
    }

    protected void removeEdge(MouseEvent e) {
        checkIfTheClickPointIsOnTheEdge(e).ifPresent(edge -> {
            graph.remove(edge);
            graph.remove(edge.edgeLabel);
            graph.connects.values().stream().flatMap(Collection::stream)
                    .filter(revEdge -> revEdge.first.equals(edge.second) && revEdge.second.equals(edge.first))
                    .findAny().ifPresent(revEdge -> {
                        graph.remove(revEdge);
                        graph.connects.values().stream().peek(list -> {
                            list.remove(edge);
                            list.remove(revEdge);
                        }).close();
                    });
            graph.repaint();
        });
    }

    protected void clearGraph() {
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        graph.mode = Mode.ADD_A_VERTEX;
        graph.algorithmMode = AlgorithmMode.NONE;
        graph.modeLabel.setText("Current Mode -> " + graph.mode.current);
        graph.displayLabel.setVisible(false);
        graph.connects.clear();
        graph.repaint();
    }

    protected void switchMode(Mode mode) {
        graph.mode = mode;
        graph.modeLabel.setText("Current Mode -> " + mode.current);
        resetVertices();
    }

    protected void switchAlgorithmMode(AlgorithmMode algorithmMode) {
        graph.mode = Mode.NONE;
        graph.algorithmMode = algorithmMode;
        graph.modeLabel.setText("Current Mode -> " + graph.mode.current);
        graph.displayLabel.setVisible(true);
        graph.displayLabel.setText("Please choose a starting vertex");
        graph.connects.keySet().stream().peek(v -> v.distance = Integer.MAX_VALUE).forEach(v -> v.visited = false);
        graph.connects.values().stream().flatMap(Collection::stream).forEach(e -> e.visited = false);
        algorithm.nodesResult = new StringJoiner(" -> ");
        algorithm.dijkstraResult = "";
        algorithm.queue.clear();
        nextLevel.clear();
        resetVertices();
    }

    private void resetVertices() {
        edgeFrom = null;
        edgeTo = null;
        rootNode = null;
        graph.repaint();
    }

    private Optional<Vertex> checkIfTheClickPointIsOnTheVertex(MouseEvent e) {
        return graph.connects.keySet().stream()
                .filter(v -> e.getPoint().distance(v.getX() + 25, v.getY() + 25) < 25)
                .findAny();
    }

    private Optional<Edge> checkIfTheClickPointIsOnTheEdge(MouseEvent e) {
        return graph.connects.values().stream().flatMap(Collection::stream)
                .filter(edge -> new Line2D.Double(edge.first.getX() + 25, edge.first.getY() + 25,
                        edge.second.getX() + 25, edge.second.getY() + 25).ptSegDist(e.getPoint()) < 5)
                .findAny();
    }
}