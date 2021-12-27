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
        this.algorithm = new Algorithm(graph.vertices, graph.edges);
    }

    void startAlgorithm(MouseEvent e) {
        if (graph.algorithmMode != NONE) {
            checkIfTheClickPointIsOnTheVertex(e)
                    .ifPresent(start -> {
                    isGraphEnabled(false);
                        rootNode = start;
                        graph.displayLabel.setText("Please wait...");
                        switch (graph.algorithmMode) {
                            case DEPTH_FIRST_SEARCH: // todo - when started end clicked again somewhere then loop begins
                                timer = new Timer(1000, event -> {
                                    var nextNode = algorithm.dfsAlgorithm(rootNode);
                                    if (nextNode != null) rootNode = nextNode;
                                    else {
                                        graph.displayLabel.setText(
                                                "<html><font color=gray><i>DFS for </i></font>" +
                                                "<font size=+2 color=blue>" + algorithm.root.id +
                                                ":   </font>" + algorithm.chainResult.toString());
                                        timer.stop();
                                    }
                                    graph.repaint();
                                });
                                timer.start();
                                break;
                            case BREADTH_FIRST_SEARCH: // todo - when started end clicked again somewhere then loop begins
                                timer = new Timer(1000, event -> {
                                    var nextNode = algorithm.bfsAlgorithm(rootNode);
                                    if (nextNode != null) rootNode = nextNode;
                                    else {
                                        graph.displayLabel.setText(
                                                "<html><font color=gray><i>BFS for </i></font>" +
                                                "<font size=+2 color=blue>" + algorithm.root.id +
                                                ":   </font>" + algorithm.chainResult.toString());
                                        timer.stop();
                                    }
                                    graph.repaint();
                                });
                                timer.start();
                                break;
                            case DIJKSTRA_ALGORITHM: // todo - edges hiding is not working properly
                                algorithm.initAlgorithm(rootNode);
                                timer = new Timer(1000, event -> {
                                    if (graph.vertices.stream().anyMatch(v -> !v.visited)) {
                                        algorithm.dijkstraAlgorithm();
                                    } else {
                                        graph.displayLabel.setText(
                                                "<html><font color=gray><i>shortest distances from </i></font>" +
                                                "<font size=+2 color=blue>" + algorithm.root.id +
                                                ":   </font>" + algorithm.edgesResult);
                                        timer.stop();
                                    }
                                    graph.repaint();
                                });
                                timer.start();
                                break;
                            case PRIM_ALGORITHM:
                                algorithm.initAlgorithm(rootNode);
                                timer = new Timer(1000, event -> {
                                    if (graph.vertices.stream().anyMatch(v -> !v.visited)) {
                                        algorithm.primAlgorithm();
                                    } else {
                                        graph.displayLabel.setText(
                                                "<html><font color=gray><i>MST for </i></font>" +
                                                "<font size=+2 color=blue>" + algorithm.root.id +
                                                ":   </font>" + algorithm.edgesResult);
                                        timer.stop();
                                    }
                                    graph.repaint();
                                });
                                timer.start();
                                break;
                            default:
                                graph.algorithmMode = NONE;
                                isGraphEnabled(true);
                        }
                    });
        }
    }

    private void isGraphEnabled(boolean setting) {
        Arrays.stream(graph.getComponents()).forEach(c -> c.setEnabled(setting));
        graph.setEnabled(setting);
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
                    resetMarkedNodes();
                    return;
                }

                while (true) {
                    var input = JOptionPane.showInputDialog(graph, "Enter Weight", "Input",
                            JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (input == null) {
                        resetMarkedNodes();
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
                        resetMarkedNodes();
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
        setCurrentModes(NONE, Mode.ADD_A_VERTEX);
        graph.displayLabel.setVisible(false);
        graph.vertices.clear();
        graph.edges.clear();
        graph.repaint();
    }

    void switchMode(Mode mode) {
        setCurrentModes(NONE, mode);
        graph.displayLabel.setVisible(false);
        resetComponentLists();
        resetMarkedNodes();
    }

    void switchAlgorithmMode(AlgorithmMode algorithmMode) {
        setCurrentModes(algorithmMode, Mode.NONE);
        graph.displayLabel.setVisible(true);
        graph.displayLabel.setText("Please choose a starting vertex");
        algorithm.queue = new LinkedList<>();
        algorithm.edgeSet = new HashSet<>();
        algorithm.chainResult = new StringJoiner(" > ");
        algorithm.edgesResult = "";
        resetComponentLists();
        resetMarkedNodes();
    }

    private void setCurrentModes(AlgorithmMode algorithmMode, Mode mode) {
        graph.mode = mode;
        graph.algorithmMode = algorithmMode;
        graph.modeLabel.setText("Current Mode -> " + graph.mode.current);
        graph.algorithmModeLabel.setText("Algorithm Mode -> " + graph.algorithmMode.current);
    }

    private void resetComponentLists() {
        graph.vertices.stream()
                .peek(v -> v.distance = Integer.MAX_VALUE)
                .forEach(v -> v.visited = false);
        graph.edges.stream()
                .peek(e -> e.setVisible(true))
                .peek(e -> e.hidden = false)
                .forEach(e -> e.visited = false);
    }

    private void resetMarkedNodes() {
        if (edgeFrom != null) {
            edgeFrom.marked = false;
            edgeFrom = null;
        }
        if (edgeTo != null) {
            edgeTo.marked = false;
            edgeTo = null;
        }
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