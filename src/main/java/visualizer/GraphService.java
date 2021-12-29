package visualizer;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;

import static visualizer.AlgorithmMode.*;

public class GraphService {
    private Vertex edgeSource, edgeTarget;
    private final Algorithm algorithm;
    private final Graph graph;
    private Timer timer;

    public GraphService(Graph graph) {
        graph.displayLabel.setVisible(false);
        this.graph = graph;
        this.algorithm = new Algorithm(graph);
    }

    void startAlgorithm(MouseEvent e) {
        checkIfTheClickPointIsOnTheVertex(e)
                .ifPresent(rootNode -> {
                    if (!algorithm.chainResult.toString().isBlank() || !algorithm.edgesResult.isBlank()) return;
                    algorithm.initAlgorithm(rootNode);
                    graph.displayLabel.setText("Please wait...");

                    timer = new Timer(500, event -> {
                        switch (graph.algorithmMode) {
                            case DEPTH_FIRST_SEARCH:
                                algorithm.dfsAlgorithm();
                                break;
                            case BREADTH_FIRST_SEARCH:
                                algorithm.bfsAlgorithm();
                                break;
                            case DIJKSTRA_ALGORITHM:
                                algorithm.dijkstraAlgorithm();
                                break;
                            case PRIM_ALGORITHM:
                                algorithm.primAlgorithm();
                                break;
                        }
                        graph.repaint();
                        if (graph.vertices.stream().allMatch(v -> v.visited)) {
                            switch (graph.algorithmMode) {
                                case DEPTH_FIRST_SEARCH:
                                    graph.displayLabel.setText(
                                            "<html><font size=+1 color=gray><i>DFS for </i></font>" +
                                            "<font size=+2 color=blue><b>" + Algorithm.root.id +
                                            ":   </b></font>" + algorithm.chainResult.toString());
                                    break;
                                case BREADTH_FIRST_SEARCH:
                                    graph.displayLabel.setText(
                                            "<html><font size=+1 color=gray><i>BFS for </i></font>" +
                                            "<font size=+2 color=blue><b>" + Algorithm.root.id +
                                            ":   </b></font>" + algorithm.chainResult.toString());
                                    break;
                                case DIJKSTRA_ALGORITHM:
                                    graph.displayLabel.setText(
                                            "<html><font size=+1 color=gray><i>shortest paths from </i></font>" +
                                            "<font size=+2 color=blue><b>" + Algorithm.root.id +
                                            ":   </b></font>" + algorithm.edgesResult);
                                    break;
                                case PRIM_ALGORITHM:
                                    graph.displayLabel.setText(
                                            "<html><font size=+1 color=gray><i>minimum spanning tree for </i></font>" +
                                            "<font size=+2 color=blue><b>" + Algorithm.root.id +
                                            ":   </b></font>" + algorithm.edgesResult);
                                    break;
                            }
                            timer.stop();
                        }
                    });
                    timer.start();
                });
    }

    void createNewVertex(MouseEvent point) {
        if (checkIfTheClickPointIsOnTheVertex(point).isEmpty()) {
            var input = JOptionPane.showInputDialog(graph, "Enter the Vertex ID (should be 1 char):", "Vertex",
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input == null) return;
            String id = input.toString();
            if (!id.isBlank() && id.length() == 1) {
                Vertex vertex = new Vertex(id, point.getPoint());
                graph.vertices.add(vertex);
                graph.add(vertex);
                graph.repaint();
            } else {
                JOptionPane.showMessageDialog(graph,
                        "Input must be one character long", "Error. Try again", JOptionPane.ERROR_MESSAGE);
                createNewVertex(point);
            }
        }
    }

    void createNewEdge(MouseEvent point) {
        if (edgeSource == null) {
            checkIfTheClickPointIsOnTheVertex(point).ifPresent(source -> {
                edgeSource = source;
                edgeSource.marked = true;
                graph.repaint();
            });
            return;
        }
        if (edgeTarget == null) {
            checkIfTheClickPointIsOnTheVertex(point).ifPresent(target -> {
                edgeTarget = target;
                edgeTarget.marked = true;
                graph.repaint();
                if (edgeSource.equals(edgeTarget) || graph.edges.stream().anyMatch(edge ->
                        edge.source.equals(edgeTarget) && edge.target.equals(edgeSource)
                        || edge.source.equals(edgeSource) && edge.target.equals(edgeTarget))) {
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
                        Edge edge = new Edge(edgeSource, edgeTarget, weight);
                        Edge reversedEdge = new Edge(edgeTarget, edgeSource, weight);
                        List.of(edge, reversedEdge).forEach(e -> {
                            graph.add(e);
                            graph.edges.add(e);
                            graph.add(edge.edgeLabel);
                        });
                        edgeSource.connected = true;
                        edgeTarget.connected = true;
                        edgeSource.connectedEdges.add(edge);
                        edgeTarget.connectedEdges.add(reversedEdge);
                        edge.mirrorEdge = reversedEdge;
                        reversedEdge.mirrorEdge = edge;
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

    void removeVertex(MouseEvent point) {
        checkIfTheClickPointIsOnTheVertex(point).ifPresent(vertex -> {
            vertex.connectedEdges.forEach(edge -> List.of(edge, edge.mirrorEdge).forEach(e -> {
                graph.remove(e);
                graph.edges.remove(e);
                if (e.edgeLabel != null) graph.remove(e.edgeLabel);
                edge.target.connectedEdges.remove(edge.mirrorEdge);
            }));
            graph.vertices.remove(vertex);
            graph.remove(vertex);
            graph.repaint();
        });
    }

    void removeEdge(MouseEvent point) {
        checkIfTheClickPointIsOnTheEdge(point).ifPresent(edge -> {
            List.of(edge, edge.mirrorEdge).forEach(e -> {
                graph.remove(e);
                graph.edges.remove(e);
                if (e.edgeLabel != null) graph.remove(e.edgeLabel);
                edge.source.connectedEdges.remove(edge);
                edge.target.connectedEdges.remove(edge.mirrorEdge);
            });
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
        algorithm.resetAlgorithm();
        resetComponentLists();
        resetMarkedNodes();
    }

    private void setCurrentModes(AlgorithmMode algorithmMode, Mode mode) {
        graph.mode = mode;
        graph.algorithmMode = algorithmMode;
        graph.toolbar.modeLabel.setText(String.format(
                "<html><font color=gray>GRAPH MODE - " +
                "<font size=+1 color=white><i>%s</i>", graph.mode.current.toUpperCase()));
        graph.toolbar.algorithmModeLabel.setText(String.format(
                "<html><font color=gray>ALGORITHM MODE - " +
                "<font size=+1 color=white><i>%s</i>", graph.algorithmMode.current.toUpperCase()));
    }

    private void resetComponentLists() {
        graph.vertices.stream()
                .peek(v -> v.distance = Integer.MAX_VALUE)
                .forEach(v -> v.visited = false);
        graph.edges.stream()
                .peek(e -> e.hidden = false)
                .forEach(e -> e.visited = false);
    }

    private void resetMarkedNodes() {
        if (edgeSource != null) {
            edgeSource.marked = false;
            edgeSource = null;
        }
        if (edgeTarget != null) {
            edgeTarget.marked = false;
            edgeTarget = null;
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
                .filter(edge -> new Line2D.Double(edge.source.center.x, edge.source.center.y,
                        edge.target.center.x, edge.target.center.y).ptLineDist(e.getPoint()) < 5)
                .findAny();
    }
}