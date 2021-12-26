package visualizer;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.stream.Collectors;

import static visualizer.AlgorithmMode.*;

public class GraphService {
    private Vertex edgeFrom, edgeTo, rootNode;
    private final Graph graph;
    private final Algorithm algorithm;
    private Timer timer;

    public GraphService(Graph graph) {
        this.graph = graph;
        this.algorithm = new Algorithm(graph.vertices);
    }

    void startAlgorithm(MouseEvent e) {
        checkIfTheClickPointIsOnTheVertex(e)
                .ifPresent(start -> {
                    rootNode = start;
                    graph.displayLabel.setText("Please wait...");
                    isGraphEnabled(false);
                });
        switch (graph.algorithmMode) {
            case DEPTH_FIRST_SEARCH:
            case BREADTH_FIRST_SEARCH:           // todo visited edges changing color - now it is too much if them
                timer = new Timer(1000, event -> {
                    var nextNode = graph.algorithmMode == DEPTH_FIRST_SEARCH ?
                            algorithm.dfsAlgorithm(rootNode) : algorithm.bfsAlgorithm(rootNode);
                    if (nextNode == null) {
                        graph.displayLabel.setText(
                                (graph.algorithmMode == DEPTH_FIRST_SEARCH ? "DFS : " : "BFS : ")
                                + algorithm.chainResult.toString());
                        timer.stop();
                    } else {
                        graph.edges.stream()
                                .filter(edge -> edge.first.visited && edge.second.visited)
                                .forEach(edge -> edge.visited = true);
                        graph.repaint();
                        rootNode = nextNode;
                    }
                });
                timer.start();
                break;
            case DIJKSTRA_ALGORITHM:                                // todo visited edges changing color
                algorithm.initDijkstraAlgorithm(rootNode);
                timer = new Timer(1000, event -> {
                    if (!algorithm.queue.isEmpty()) {
                        var current = algorithm.queue.pollFirst();
                        algorithm.dijkstraAlgorithm(current);
                        graph.repaint();
                    } else {
                        graph.displayLabel.setText("<html><font color=blue><i>shortest distances from </i></font>" +
                                                   "<font size=+2 color=blue>" + rootNode.id +
                                                   ":   </font>" + algorithm.edgesResult);
                        graph.repaint();
                        timer.stop();
                    }
                });
                timer.start();
                break;
            case PRIM_ALGORITHM:
                algorithm.primAlgorithm(rootNode);
                timer = new Timer(3000, event -> {
                    graph.displayLabel.setText(algorithm.edgesResult);
                    timer.stop();
                });
                timer.setRepeats(false);
                timer.start();
        }
        isGraphEnabled(true);
    }

    private void isGraphEnabled(boolean setting) {
        Arrays.stream(graph.getComponents()).forEach(c -> c.setEnabled(setting));
        graph.setEnabled(setting);
    }

    private void markEdgeAsVisited(Vertex first, Vertex second) {
        graph.edges.stream()
                .filter(edge -> edge.first.equals(first) && edge.second.equals(second)
                                || edge.second.equals(first) && edge.first.equals(second))
                .forEach(edge -> edge.visited = true);
        graph.repaint();
    }

    void createNewVertex(MouseEvent e) {
        if (checkIfTheClickPointIsOnTheVertex(e).isEmpty()) {
            var input = JOptionPane.showInputDialog(graph, "Enter the Vertex ID (should be 1 char):", "Vertex",
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input == null) return;
            String id = input.toString();
            if (!id.isBlank() && id.length() == 1) {
                Vertex vertex = new Vertex(id, e.getPoint());
                graph.vertices.add(vertex);
                graph.add(vertex);
                graph.repaint();
            } else {
                JOptionPane.showMessageDialog(graph,
                        "Input must be one character long", "Error. Try again", JOptionPane.ERROR_MESSAGE);
                createNewVertex(e);
            }
        }
    }

    void createNewEdge(MouseEvent e) {
        if (edgeFrom == null) {
            checkIfTheClickPointIsOnTheVertex(e).ifPresent(first -> {
                edgeFrom = first;
                edgeFrom.marked = true;
                graph.repaint();
            });
            return;
        }
        if (edgeTo == null) {
            checkIfTheClickPointIsOnTheVertex(e).ifPresent(second -> {
                edgeTo = second;
                edgeTo.marked = true;
                graph.repaint();
                if (edgeFrom.equals(edgeTo) || graph.edges.stream().anyMatch(edge ->
                        edge.first.equals(edgeFrom) && edge.second.equals(edgeTo)
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
                        edgeFrom.connected = true;
                        edgeTo.connected = true;
                        graph.add(edge);
                        graph.add(reverseEdge);                      // todo can i simplify it??
                        graph.add(edge.edgeLabel);
                        graph.edges.add(edge);
                        graph.edges.add(reverseEdge);
                        edgeFrom.connectedEdges.add(edge);
                        edgeTo.connectedEdges.add(reverseEdge);
                        resetVertices();
                        return;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(graph,
                                "Edge weight must be a number", "Error. Try again", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }

    void removeVertex(MouseEvent e) {
        checkIfTheClickPointIsOnTheVertex(e).ifPresent(vertex -> {
            graph.edges.stream()
                    .filter(edge -> edge.first.equals(vertex) || edge.second.equals(vertex))
                    .peek(edge -> {
                        if (edge.edgeLabel != null) graph.remove(edge.edgeLabel);
                    })
                    .forEach(graph::remove);
            graph.edges = graph.edges.stream()
                    .filter(edge -> !edge.first.equals(vertex) && !edge.second.equals(vertex))
                    .collect(Collectors.toList());
            graph.vertices.remove(vertex);
            graph.remove(vertex);
            graph.repaint();
        });
    }

    void removeEdge(MouseEvent e) {
        checkIfTheClickPointIsOnTheEdge(e).ifPresent(edge -> {
            graph.edges.stream()
                    .filter(reversedEdge ->
                            reversedEdge.first.equals(edge.second) && reversedEdge.second.equals(edge.first))
                    .findAny().ifPresent(revLine -> {
                        graph.remove(revLine);
                        graph.edges.remove(revLine);
                        graph.remove(edge.edgeLabel != null ? edge.edgeLabel : revLine.edgeLabel);
                        graph.vertices.stream().map(v -> v.connectedEdges).peek(list -> {
                            list.remove(edge);
                            list.remove(revLine);                       // todo foreach?? or maybe by given vertex??
                        }).close();
                    });
            graph.remove(edge);
            graph.edges.remove(edge);
            graph.repaint();
        });
    }

    void clearGraph() {
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        graph.mode = Mode.ADD_A_VERTEX;
        graph.algorithmMode = NONE;
        graph.modeLabel.setText("Current Mode -> " + graph.mode.current);
        graph.displayLabel.setVisible(false);
        graph.vertices.clear();
        graph.edges.clear();
        graph.repaint();
    }

    void switchMode(Mode mode) {
        graph.mode = mode;
        graph.modeLabel.setText("Current Mode -> " + mode.current);
        graph.displayLabel.setVisible(false);
        graph.vertices.stream()
                .peek(v -> v.distance = Integer.MAX_VALUE)
                .forEach(v -> v.visited = false);
        graph.edges.forEach(v -> v.visited = false);
        resetVertices();
    }

    void switchAlgorithmMode(AlgorithmMode algorithmMode) {
        graph.mode = Mode.NONE;
        graph.algorithmMode = algorithmMode;
        graph.modeLabel.setText("Current Mode -> " + graph.mode.current);
        graph.displayLabel.setVisible(true);
        graph.displayLabel.setText("Please choose a starting vertex");
        graph.vertices.stream()
                .peek(v -> v.distance = Integer.MAX_VALUE)
                .forEach(v -> v.visited = false);
        graph.edges.forEach(v -> v.visited = false);
        algorithm.queue = new LinkedList<>();
        algorithm.chainResult = new StringJoiner(" > ");
        algorithm.edgesResult = "";
        resetVertices();
    }

    private void resetVertices() {
        if (edgeFrom != null) {
            edgeFrom.marked = false;
            edgeFrom = null;
        }
        if (edgeTo != null) {
            edgeTo.marked = false;
            edgeTo = null;
        }
        rootNode = null;
        graph.repaint();
    }

    private Optional<Vertex> checkIfTheClickPointIsOnTheVertex(MouseEvent e) {
        return graph.vertices.stream()
                .filter(v -> e.getPoint().distance(v.center) < 25)
                .findAny();
    }

    private Optional<Edge> checkIfTheClickPointIsOnTheEdge(MouseEvent e) {
        return graph.edges.stream()
                .filter(edge -> new Line2D.Double(edge.first.center.x, edge.first.center.y,
                        edge.second.center.x, edge.second.center.y).ptLineDist(e.getPoint()) < 5)
                .findAny();
    }
}