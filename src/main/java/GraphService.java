import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.stream.Collectors;

public class GraphService {
    private Vertex edgeFrom, edgeTo, vertexToRemove, rootNode;
    private Edge edgeToDelete, reversedEdgeToDelete;
    private LinkedHashSet<Vertex> nextLevel = new LinkedHashSet<>();
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
                    if (graph.algorithmMode == AlgorithmMode.DEPTH_FIRST_SEARCH) rootNode = start;
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
                graph.vertices.add(vertex);
                graph.add(vertex);
                graph.repaint();
                graph.connects.put(vertex, new ArrayList<>());
                vertex.revalidate();
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
                edgeTo = second;
                edgeTo.repaint();
                if (graph.edges.stream().anyMatch(edge -> edgeFrom.equals(edgeTo)
                                                          || edge.first.equals(edgeFrom) && edge.second.equals(edgeTo)
                                                          || edge.first.equals(edgeTo) && edge.second.equals(edgeFrom))) {
                    resetVertices();
                    return;
                }
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
                        graph.edges.add(edge);
                        graph.edges.add(reverseEdge);
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
        checkIfTheClickPointIsOnTheVertex(e).ifPresent(vertexAtClickPoint -> {
            vertexToRemove = vertexAtClickPoint;
            graph.edges.stream().filter(edge -> edge.first.equals(vertexToRemove) || edge.second.equals(vertexToRemove))
                    .peek(edge -> { if (edge.edgeLabel != null) graph.remove(edge.edgeLabel); })
                    .forEach(graph::remove);
            graph.edges = graph.edges.stream()
                    .filter(edge -> !edge.first.equals(vertexToRemove) && !edge.second.equals(vertexToRemove))
                    .collect(Collectors.toList());
            graph.connects.remove(vertexToRemove);
            graph.vertices.remove(vertexToRemove);
            graph.remove(vertexToRemove);
            graph.repaint();
        });
    }

    protected void removeEdge(MouseEvent e) {
        checkIfTheClickPointIsOnTheEdge(e).ifPresent(edgeAtClickPoint -> {
            edgeToDelete = edgeAtClickPoint;
            graph.edges.stream().filter(reversedEdge ->
                            reversedEdge.first.equals(edgeToDelete.second) && reversedEdge.second.equals(edgeToDelete.first))
                    .findAny().ifPresent(reversedEdge -> reversedEdgeToDelete = reversedEdge);
        });
        graph.remove(edgeToDelete);
        graph.remove(edgeToDelete.edgeLabel);
        graph.remove(reversedEdgeToDelete);
        graph.edges.remove(edgeToDelete);
        graph.edges.remove(reversedEdgeToDelete);
        graph.connects.values().stream().filter(list -> list.contains(edgeToDelete))
                .forEach(list -> list.remove(edgeToDelete));
        graph.connects.values().stream().filter(list -> list.contains(reversedEdgeToDelete))
                .forEach(list -> list.remove(reversedEdgeToDelete));
        graph.repaint();
    }

    protected void clearGraph() {
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        graph.mode = Mode.ADD_A_VERTEX;
        graph.algorithmMode = AlgorithmMode.NONE;
        graph.modeLabel.setText("Current Mode -> " + graph.mode.current);
        graph.connects.clear();
        graph.vertices.clear();
        graph.edges.clear();
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
        graph.displayLabel.setText("Please choose a starting vertex");
        graph.connects.keySet().forEach(v -> v.visited = false);
        graph.connects.values().stream().flatMap(Collection::stream).forEach(e -> e.visited = false);
        algorithm.nodesResult = new StringJoiner(" -> ");
        algorithm.edgesResult = new StringJoiner(" -> ");
        algorithm.queue.clear();
        nextLevel.clear();
        resetVertices();
    }

    private void resetVertices() {
        edgeFrom = null;
        edgeTo = null;
        vertexToRemove = null;
        rootNode = null;
        graph.repaint();
    }

    private Optional<Vertex> checkIfTheClickPointIsOnTheVertex(MouseEvent e) {
        return graph.vertices.stream()
                .filter(v -> e.getPoint().distance(v.getX() + 25, v.getY() + 25) < 25)
                .findAny();
    }

    private Optional<Edge> checkIfTheClickPointIsOnTheEdge(MouseEvent e) {
        return graph.edges.stream()
                .filter(edge -> new Line2D.Double(edge.first.getX() + 25, edge.first.getY() + 25,
                        edge.second.getX() + 25, edge.second.getY() + 25).ptSegDist(e.getPoint()) < 5)
                .findAny();
    }
}