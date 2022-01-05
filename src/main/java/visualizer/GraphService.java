package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

@Getter
public class GraphService {
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Algorithm algorithm;
    private final Toolbar toolbar;
    private final Graph graph;
    private GraphMode graphMode = GraphMode.ADD_A_VERTEX;
    private AlgMode algorithmMode = AlgMode.NONE;
    private Vertex edgeSource, edgeTarget;
    private Timer timer;

    public GraphService(Graph graph, Toolbar toolbar) {
        this.graph = graph;
        this.toolbar = toolbar;
        toolbar.service = this;
        this.algorithm = new Algorithm(this);
    }

    void startAlgorithm(MouseEvent e) {
        checkIfVertex(e)
                .ifPresent(selectedNode -> {
                    if (Algorithm.root != null && algorithmMode == AlgMode.DIJKSTRA_ALGORITHM) {
                        var shortestPath = algorithm.getShortestPath(selectedNode);
                        toolbar.infoPanel.setText(shortestPath);
                        graph.repaint();
                    }
                    if (Algorithm.root == null) {
                        algorithm.initAlgorithm(selectedNode);
                        toolbar.infoPanel.setText("Please wait...");
                        timer = new Timer(500, event -> {
                            switch (algorithmMode) {
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
                            var algorithmResult = algorithm.getResultIfReady();
                            if (!algorithmResult.isBlank()) {
                                toolbar.infoPanel.setText(algorithmResult);
                                timer.stop();
                            }
                            graph.repaint();
                        });
                        timer.start();
                    }
                });
    }

    void createNewVertex(MouseEvent point) {
        if (checkIfVertex(point).isEmpty()) {
            var input = JOptionPane.showInputDialog(graph, "Enter the vertex ID (should be 1 char):", "Vertex ID",
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input == null) return;
            String id = input.toString();
            if (!id.isBlank() && id.length() == 1) {
                Vertex vertex = new Vertex(id, point.getPoint(), this);
                vertices.add(vertex);
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
            checkIfVertex(point).ifPresent(source -> {
                edgeSource = source;
                edgeSource.marked = true;
                graph.repaint();
            });
            return;
        }
        if (edgeTarget == null) {
            checkIfVertex(point).ifPresent(target -> {
                edgeTarget = target;
                edgeTarget.marked = true;
                graph.repaint();
                if (edgeSource.equals(edgeTarget) || edges.stream().anyMatch(edge ->
                        edge.source.equals(edgeTarget) && edge.target.equals(edgeSource)
                        || edge.source.equals(edgeSource) && edge.target.equals(edgeTarget))) {
                    resetMarkedNodes();
                    return;
                }

                while (true) {
                    var input = JOptionPane.showInputDialog(graph, "Enter weight", "Edge weight",
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
                            edges.add(e);
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
        checkIfVertex(point).ifPresent(vertex -> {
            vertex.connectedEdges.forEach(edge -> List.of(edge, edge.mirrorEdge).forEach(e -> {
                graph.remove(e);
                edges.remove(e);
                if (e.edgeLabel != null) graph.remove(e.edgeLabel);
                edge.target.connectedEdges.remove(edge.mirrorEdge);
            }));
            vertices.remove(vertex);
            graph.remove(vertex);
            graph.repaint();
        });
    }

    void removeEdge(MouseEvent point) {
        checkIfEdge(point).ifPresent(edge -> {
            List.of(edge, edge.mirrorEdge).forEach(e -> {
                graph.remove(e);
                edges.remove(e);
                if (e.edgeLabel != null) graph.remove(e.edgeLabel);
                edge.source.connectedEdges.remove(edge);
                edge.target.connectedEdges.remove(edge.mirrorEdge);
            });
            graph.repaint();
        });
    }

    void clearGraph() {
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        setCurrentModes(AlgMode.NONE, GraphMode.ADD_A_VERTEX);
        toolbar.infoPanel.setText("");
        algorithm.resetAlgorithm();
        vertices.clear();
        edges.clear();
        graph.repaint();
    }

    void switchMode(GraphMode graphMode) {
        setCurrentModes(AlgMode.NONE, graphMode);
        toolbar.infoPanel.setText("");
        graph.setToolTipText(null);
        algorithm.resetAlgorithm();
        resetComponentLists();
        resetMarkedNodes();
    }

    void switchAlgorithmMode(AlgMode algorithmMode) {
        setCurrentModes(algorithmMode, GraphMode.NONE);
        toolbar.infoPanel.setText("Please choose a starting vertex");
        graph.setToolTipText(null);
        algorithm.resetAlgorithm();
        resetComponentLists();
        resetMarkedNodes();
    }

    private void setCurrentModes(AlgMode algorithmMode, GraphMode graphMode) {
        this.graphMode = graphMode;
        this.algorithmMode = algorithmMode;
        toolbar.modeLabel.setText(String.format(
                "<html><font color=gray>GRAPH MODE - " +
                "<font size=+1 color=white><i>%s</i>", graphMode.current.toUpperCase()));
        toolbar.algorithmModeLabel.setText(String.format(
                "<html><font color=gray>ALGORITHM MODE - " +
                "<font size=+1 color=white><i>%s</i>", algorithmMode.current.toUpperCase()));
    }

    private void resetComponentLists() {
        vertices.forEach(vertex -> {
            vertex.distance = Integer.MAX_VALUE;
            vertex.visited = false;
            vertex.path = false;
        });
        edges.forEach(edge -> {
            edge.hidden = false;
            edge.visited = false;
            edge.path = false;
        });
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

    private Optional<Vertex> checkIfVertex(MouseEvent event) {
        return event.getSource() instanceof Vertex ? Optional.of((Vertex) event.getSource()) : Optional.empty();
    }

    private Optional<Edge> checkIfEdge(MouseEvent event) {
        return edges.stream()
                .filter(edge -> new Line2D.Double(edge.source.getX() + edge.source.radius,
                        edge.source.getY() + edge.source.radius, edge.target.getX() + edge.target.radius,
                        edge.target.getY() + edge.target.radius).ptLineDist(event.getPoint()) < 5)
                .findAny();
    }
}