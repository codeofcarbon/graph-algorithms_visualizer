package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.undo.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

@Getter
public class GraphService implements Serializable, StateEditable {
    private final UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);
    private final MouseHandler mouseHandler = new MouseHandler(this);
    private List<Vertex> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private final Algorithm algorithm;
    private final Graph graph;
    private Toolbar toolbar;
    private GraphMode graphMode = GraphMode.ADD_A_VERTEX;
    private AlgMode algorithmMode = AlgMode.NONE;
    private Vertex edgeSource, edgeTarget;
    private UndoManager manager;
    private Timer timer;

    public GraphService(Graph graph) {
        this.graph = graph;
        this.manager = new UndoManager();
        this.mouseHandler.addComponent(graph);
        this.algorithm = new Algorithm(this);
        this.undoableEditSupport.addUndoableEditListener(manager);
    }

    public void storeState(Hashtable<Object, Object> state) {
        state.put("Edges", new ArrayList<>(edges));
        state.put("Nodes", new ArrayList<>(nodes));
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state) {
        var edgesState = (List<Edge>) state.get("Edges");
        edges = edgesState != null ? edgesState : edges;
        var nodesState = (List<Vertex>) state.get("Nodes");
        nodes = nodesState != null ? nodesState : nodes;
        edges.forEach(edge -> {
            if (!edge.getTarget().connectedEdges.contains(edge.mirrorEdge)) {
                edge.getTarget().connectedEdges.add(edge.mirrorEdge);
            }
            if (!edge.getSource().connectedEdges.contains(edge)) {
                edge.getSource().connectedEdges.add(edge);
            }
        });
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        nodes.forEach(graph::add);
        edges.forEach(graph::add);
        graph.repaint();
    }

    void startAlgorithm(MouseEvent e) {
        checkIfVertex(e)
                .ifPresent(selectedNode -> {
                    if (Algorithm.root != null && algorithmMode == AlgMode.DIJKSTRA_ALGORITHM) {
                        var shortestPath = algorithm.getShortestPath(selectedNode);
                        toolbar.getLeftInfoLabel().setText(shortestPath);
                        graph.repaint();
                    }
                    if (Algorithm.root == null) {
                        algorithm.initAlgorithm(selectedNode);
                        toolbar.getLeftInfoLabel().setText("Please wait...");
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
                                toolbar.getLeftInfoLabel().setText(algorithmResult);
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
            var input = JOptionPane.showInputDialog(graph, "<html>Set vertex ID <br>(alphanumeric char):",
                    "Vertex ID", JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input != null) {
                Vertex vertex;
                String id = input.toString();
                if (!id.isBlank() && id.length() == 1) {
                    vertex = new Vertex(id, point.getPoint());
                    mouseHandler.addComponent(vertex);
                    nodes.add(vertex);
                    graph.add(vertex);
                    graph.repaint();
                } else {
                    JOptionPane.showMessageDialog(graph,
                            "Id must be one character long", "Error. Try again", JOptionPane.ERROR_MESSAGE);
                    createNewVertex(point);
                }
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
                        edge.getSource().equals(edgeTarget) && edge.getTarget().equals(edgeSource)
                        || edge.getSource().equals(edgeSource) && edge.getTarget().equals(edgeTarget))) {
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
                edge.getTarget().connectedEdges.remove(edge.mirrorEdge);
                graph.remove(e);
                edges.remove(e);
            }));
            nodes.remove(vertex);
            graph.remove(vertex);
            graph.repaint();
        });
    }

    void removeEdge(MouseEvent point) {
        checkIfEdge(point).ifPresent(edge -> {
            List.of(edge, edge.mirrorEdge).forEach(e -> {
                edge.getSource().connectedEdges.remove(edge);
                edge.getTarget().connectedEdges.remove(edge.mirrorEdge);
                graph.remove(e);
                edges.remove(e);
            });
            graph.repaint();
        });
    }

    void clearGraph() {
        undoableEditSupport.removeUndoableEditListener(manager);
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        setCurrentModes(AlgMode.NONE, GraphMode.ADD_A_VERTEX);
        toolbar.getLeftInfoLabel().setText("");
        algorithm.resetAlgorithmData();
        nodes.clear();
        edges.clear();
        graph.repaint();
        undoableEditSupport.addUndoableEditListener(manager = new UndoManager());
    }

    void setCurrentModes(AlgMode algorithmMode, GraphMode graphMode) {
        if (toolbar == null) toolbar = graph.getToolbar();
        toolbar.getButtonPanel().getAlgModeComboBox()
                .setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algorithmMode));
        toolbar.getButtonPanel().getGraphModeComboBox()
                .setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
        toolbar.getLeftInfoLabel().setText(graphMode == GraphMode.NONE ? "Please choose a starting vertex" : "");
        toolbar.updateModeLabels(graphMode.current.toUpperCase(), algorithmMode.current.toUpperCase());
        toolbar.getButtonPanel().getButtonGroup().clearSelection();
        this.graphMode = graphMode;
        this.algorithmMode = algorithmMode;
        graph.setToolTipText(null);
        algorithm.resetAlgorithmData();
        resetComponentLists();
        resetMarkedNodes();
    }

    private void resetComponentLists() {
        nodes.forEach(vertex -> {
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
                .filter(edge -> {
                    var source = edge.getSource();
                    var target = edge.getTarget();
                    return new Line2D.Double(
                            source.getX() + source.radius, source.getY() + source.radius,
                            target.getX() + target.radius, target.getY() + target.radius)
                                   .ptLineDist(event.getPoint()) < 5;
                })
                .findAny();
    }
}

enum GraphMode {
    ADD_A_VERTEX("Add a Vertex"),
    ADD_AN_EDGE("Add an Edge"),
    REMOVE_A_VERTEX("Remove a Vertex"),
    REMOVE_AN_EDGE("Remove an Edge"),
    NONE("None");

    final String current;

    GraphMode(String current) {
        this.current = current;
    }
}

enum AlgMode {
    DEPTH_FIRST_SEARCH("Depth-First Search"),
    BREADTH_FIRST_SEARCH("Breadth-First Search"),
    DIJKSTRA_ALGORITHM("Dijkstra's Algorithm"),
    PRIM_ALGORITHM("Prim's Algorithm"),
    NONE("None");

    final String current;

    AlgMode(String current) {
        this.current = current;
    }
}