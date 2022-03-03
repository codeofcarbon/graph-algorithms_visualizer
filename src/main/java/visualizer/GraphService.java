package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.*;

@Getter
public class GraphService implements Serializable, StateEditable {
    private final UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);
    private final MouseHandler mouseHandler = new MouseHandler(this);
    private List<Node> nodes = new ArrayList<>();
    private final Algorithm algorithm;
    private final Graph graph;
    private Toolbar toolbar;
    private GraphMode graphMode = GraphMode.ADD_A_VERTEX;
    private AlgMode algorithmMode = AlgMode.NONE;
    private Node edgeSource, edgeTarget;
    private UndoManager manager;
    private Timer timer;
    StateEdit graphEdit;

    public GraphService(Graph graph) {
        this.graph = graph;
        this.manager = new UndoManager();
        this.mouseHandler.addComponent(graph);
        this.algorithm = new Algorithm(this);
        this.undoableEditSupport.addUndoableEditListener(manager);
    }

    public void storeState(Hashtable<Object, Object> state) {
        var edges = nodes.stream().flatMap(v -> v.connectedEdges.stream()).collect(Collectors.toList());
        state.put("nodes", new ArrayList<>(nodes));
        state.put("edges", new ArrayList<>(edges));
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state) {
        var nodesState = (List<Node>) state.get("nodes");
        nodes = nodesState != null ? nodesState : nodes;
        var edgesState = (List<Edge>) state.get("edges");
        if (edgesState != null) {
            nodes.forEach(v -> {
                v.connectedEdges.clear();
                v.connectedEdges.addAll(edgesState.stream()
                        .filter(e -> e.getSource().equals(v))
                        .collect(Collectors.toList()));
            });
        }
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        nodes.forEach(graph::add);
        nodes.forEach(v -> v.connectedEdges.forEach(graph::add));
        graph.repaint();
    }

    void startAlgorithm(MouseEvent e) {
        checkIfVertex(e).ifPresent(selectedNode -> {
            if (Algorithm.root != null && algorithmMode == AlgMode.DIJKSTRA_ALGORITHM) {
                var shortestPath = algorithm.getShortestPath(selectedNode);
                toolbar.getLeftInfoLabel().setText(shortestPath);
                graph.repaint();
            }
            if (Algorithm.root == null) {
                algorithm.initAlgorithm(selectedNode);
                toolbar.getLeftInfoLabel().setText("Please wait...");
                timer = new Timer(250, event -> {
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
            var input = JOptionPane.showInputDialog(graph, "Set vertex ID (alphanumeric char):",
                    "Vertex ID", JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input != null) {
                Node node;
                String id = input.toString();
                if (!id.isBlank() && id.length() == 1) {
                    graphEdit = new StateEdit(this);
                    node = new Node(id, point.getPoint(), graph, new ArrayList<>());
                    mouseHandler.addComponent(node);
                    nodes.add(node);
                    graph.add(node);
                    graphEdit.end();
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
                if (nodes.stream().flatMap(v -> v.connectedEdges.stream()).anyMatch(edge ->
                        edge.getSource().equals(edgeTarget) && edge.getTarget().equals(edgeSource)
                        || edge.getSource().equals(edgeSource) && edge.getTarget().equals(edgeTarget))
                    || edgeSource.equals(edgeTarget)) {
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
                        graphEdit = new StateEdit(this);
                        Edge edge = new Edge(edgeSource, edgeTarget, weight);
                        Edge reversedEdge = new Edge(edgeTarget, edgeSource, weight);
                        Stream.of(edge, reversedEdge).forEach(graph::add);
                        Stream.of(edgeSource, edgeTarget).forEach(v -> v.connected = true);
                        edge.getSource().connectedEdges.add(edge);
                        edge.getTarget().connectedEdges.add(reversedEdge);
                        edge.mirrorEdge = reversedEdge;
                        reversedEdge.mirrorEdge = edge;
                        graphEdit.end();
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
        checkIfVertex(point).ifPresent(node -> {
            graphEdit = new StateEdit(this);
            node.connectedEdges.forEach(edge -> {
                Stream.of(edge, edge.mirrorEdge).forEach(graph::remove);
                edge.getTarget().connectedEdges.remove(edge.mirrorEdge);
            });
            nodes.remove(node);
            graph.remove(node);
            graph.repaint();
            graphEdit.end();
        });
    }

    void removeEdge(MouseEvent point) {
        checkIfEdge(point).ifPresent(edge -> {
            graphEdit = new StateEdit(this);
            Stream.of(edge, edge.mirrorEdge)
                    .peek(e -> e.getSource().connectedEdges.remove(e))
                    .forEach(graph::remove);
            graph.repaint();
            graphEdit.end();
        });
    }

    void clearGraph() {
        undoableEditSupport.removeUndoableEditListener(manager);
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        setCurrentModes(AlgMode.NONE, GraphMode.ADD_A_VERTEX);
        toolbar.getLeftInfoLabel().setText("");
        algorithm.resetAlgorithmData();
        nodes.clear();
        graph.repaint();
        undoableEditSupport.addUndoableEditListener(manager = new UndoManager());
    }

    void setCurrentModes(AlgMode algorithmMode, GraphMode graphMode) {
        if (toolbar == null) toolbar = graph.getToolbar();
        toolbar.getButtonPanel().getAlgModeComboBox()
                .setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algorithmMode));
        toolbar.getButtonPanel().getGraphModeComboBox()
                .setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
        toolbar.getLeftInfoLabel().setText(graphMode == GraphMode.NONE && algorithmMode != AlgMode.NONE
                ? "Please choose a starting vertex" : "");
        toolbar.updateModeLabels(graphMode.current.toUpperCase(), algorithmMode.current.toUpperCase());
        toolbar.getButtonPanel().getButtonGroup().clearSelection();
        this.graphMode = graphMode;
        this.algorithmMode = algorithmMode;
        graph.setToolTipText(null);
        algorithm.resetAlgorithmData();
        resetComponentLists();
        resetMarkedNodes();
    }

    void resetComponentLists() {
        nodes.forEach(node -> {
            node.distance = Integer.MAX_VALUE;
            node.visited = false;
            node.path = false;
            node.connectedEdges.forEach(edge -> {
                edge.hidden = false;
                edge.visited = false;
                edge.path = false;
            });
        });
        Algorithm.root = null;
        Algorithm.target = null;
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

    private Optional<Node> checkIfVertex(MouseEvent event) {
        return event.getSource() instanceof Node ? Optional.of((Node) event.getSource()) : Optional.empty();
    }

    private Optional<Edge> checkIfEdge(MouseEvent event) {
        return nodes.stream().flatMap(v -> v.connectedEdges.stream())
                .filter(edge -> {
                    var r = edge.getSource().getRadius();
                    var source = edge.getSource().getLocation();
                    var target = edge.getTarget().getLocation();
                    var line = new Line2D.Double(source.x + r, source.y + r, target.x + r, target.y + r);
                    var midPoint = new Point((int) ((line.x1 + line.x2) / 2), (int) ((line.y1 + line.y2) / 2));
                    return line.ptSegDist(event.getPoint()) < 5 || midPoint.distance(event.getPoint()) < 12;
                }).findAny();
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