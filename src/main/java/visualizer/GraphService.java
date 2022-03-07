package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.undo.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.*;

@Getter
public class GraphService implements Serializable, StateEditable {
    private final UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);
    private final MouseHandler mouseHandler = new MouseHandler(this);
    private final Algorithm algorithm = new Algorithm(this);
    private final Graph graph;
    private List<Node> nodes = new ArrayList<>();
    private GraphMode graphMode = GraphMode.ADD_NODE;
    private AlgMode algorithmMode = AlgMode.NONE;
    private UndoManager manager = new UndoManager();
    private static Node edgeSource, edgeTarget;
    private Toolbar toolbar;
    private Timer timer;
    StateEdit graphEdit;

    public GraphService(Graph graph) {
        this.graph = graph;
        mouseHandler.addComponent(graph);
        undoableEditSupport.addUndoableEditListener(manager);
    }

    public void storeState(Hashtable<Object, Object> state) {
        var edges = nodes.stream().flatMap(node -> node.getConnectedEdges().stream()).collect(Collectors.toList());
        state.put("nodes", new ArrayList<>(nodes));
        state.put("edges", new ArrayList<>(edges));
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state) {
        var nodesState = (List<Node>) state.get("nodes");
        nodes = nodesState != null ? nodesState : nodes;
        var edgesState = (List<Edge>) state.get("edges");
        if (edgesState != null) {
            nodes.forEach(node -> {
                node.getConnectedEdges().clear();
                node.getConnectedEdges().addAll(edgesState.stream()
                        .filter(e -> e.getSource().equals(node))
                        .collect(Collectors.toList()));
            });
        }
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        nodes.stream().peek(v -> v.getConnectedEdges().forEach(graph::add)).forEach(graph::add);
        graph.repaint();
    }

    void startAlgorithm(MouseEvent point) {
        checkIfVertex(point).ifPresent(selectedNode -> {
            if (!algorithm.checkIfGraphIsConnected(selectedNode)) {
                var messageLabel = new JLabel("<html><div align='center'>Unfortunately, this version of the program " +
                                              "supports connected graphs only.<br>Check back soon for updates");
                JOptionPane.showMessageDialog(graph, messageLabel, "Disconnected graph", JOptionPane.PLAIN_MESSAGE);
                resetComponentsLists();
                return;
            }
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
            var input = JOptionPane.showInputDialog(graph, "Set node ID (alphanumeric char):",
                    "Node ID", JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (input != null) {
                Node node;
                String id = input.toString();
                if (id.matches("[^_\\W]")) {
                    graphEdit = new StateEdit(this);
                    node = new Node(id, point.getPoint(), new ArrayList<>());
                    mouseHandler.addComponent(node);
                    nodes.add(node);
                    graph.add(node);
                    graphEdit.end();
                    graph.repaint();
                } else {
                    JOptionPane.showMessageDialog(graph, "Id must be one upper or lower case letter or digit. " +
                                                         "Try again", "Error", JOptionPane.ERROR_MESSAGE);
                    createNewVertex(point);
                }
            }
        }
    }

    void createNewEdge(MouseEvent point) {
        if (edgeSource == null) {
            checkIfVertex(point).ifPresent(sourceNode -> {
                edgeSource = sourceNode;
                edgeSource.marked = true;
                graph.repaint();
            });
            return;
        }
        if (edgeTarget == null) {
            checkIfVertex(point).ifPresent(targetNode -> {
                edgeTarget = targetNode;
                edgeTarget.marked = true;
                graph.repaint();
                if (nodes.stream().flatMap(node -> node.getConnectedEdges().stream()).anyMatch(edge ->
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
                        edge.setMirrorEdge(reversedEdge);
                        reversedEdge.setMirrorEdge(edge);
                        Stream.of(edge, reversedEdge).forEach(graph::add);
                        Stream.of(edgeSource, edgeTarget).forEach(node -> node.connected = true);
                        edge.getSource().getConnectedEdges().add(edge);
                        edge.getTarget().getConnectedEdges().add(reversedEdge);
                        graphEdit.end();
                        resetMarkedNodes();
                        return;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(graph, "Edge weight must be a number. Try again",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }

    void removeVertex(MouseEvent point) {
        checkIfVertex(point).ifPresent(node -> {
            graphEdit = new StateEdit(this);
            node.getConnectedEdges().forEach(edge -> {
                Stream.of(edge, edge.getMirrorEdge()).forEach(graph::remove);
                edge.getTarget().getConnectedEdges().remove(edge.getMirrorEdge());
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
            Stream.of(edge, edge.getMirrorEdge())
                    .peek(e -> e.getSource().getConnectedEdges().remove(e))
                    .forEach(graph::remove);
            graph.repaint();
            graphEdit.end();
        });
    }

    void clearGraph() {
        undoableEditSupport.removeUndoableEditListener(manager);
        Arrays.stream(graph.getComponents()).forEach(graph::remove);
        setCurrentModes(AlgMode.NONE, GraphMode.ADD_NODE);
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
        resetComponentsLists();
        resetMarkedNodes();
    }

    void resetComponentsLists() {
        nodes.forEach(node -> {
            node.distance = Integer.MAX_VALUE;
            node.visited = false;
            node.path = false;
            node.getConnectedEdges().forEach(edge -> {
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
        return nodes.stream().flatMap(node -> node.getConnectedEdges().stream())
                .filter(edge -> {
                    var line = edge.getLine();
                    var midpoint = edge.getMidpoint(line);
                    return line.ptSegDist(event.getPoint()) < 5 || midpoint.distance(event.getPoint()) < 12;
                }).findAny();
    }
}

enum GraphMode {
    ADD_NODE("Add Node"),
    ADD_AN_EDGE("Add an Edge"),
    REMOVE_NODE("Remove Node"),
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
