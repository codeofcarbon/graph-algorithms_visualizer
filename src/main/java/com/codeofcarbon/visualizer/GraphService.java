package com.codeofcarbon.visualizer;

import com.codeofcarbon.visualizer.view.*;
import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.undo.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

@Getter
public class GraphService implements Serializable, StateEditable {
    @Serial
    private static final long serialVersionUID = 1234L;
    private final UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);
    private final MouseHandler mouseHandler = new MouseHandler(this);
    private final Algorithm algorithm = new Algorithm(this);
    private final Graph graph;
    private final Infobar infobar;
    private Toolbar toolbar;
    private List<Node> nodes = new ArrayList<>();
    private GraphMode graphMode = GraphMode.ADD_NODE;
    private AlgMode algorithmMode = AlgMode.NONE;
    private UndoManager manager = new UndoManager();
    private static Node edgeSource, edgeTarget;
    private Timer timer;
    public StateEdit graphEdit;

    public GraphService(Graph graph) {
        mouseHandler.addComponent(this.graph = graph);
        infobar = new Infobar(graph);
        undoableEditSupport.addUndoableEditListener(manager);
    }

    public void storeState(Hashtable<Object, Object> state) {
        var edges = nodes.stream()
                .map(Node::getConnectedEdges)
                .flatMap(Collection::stream)
                .toList();
        state.put("nodes", new ArrayList<>(nodes));
        state.put("edges", new ArrayList<>(edges));
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state) {
        var nodesState = (List<Node>) state.get("nodes");
        if (nodesState != null) {
            if (nodes.size() < nodesState.size())
                nodesState.stream()
                        .filter(node -> !nodes.contains(node))
                        .peek(Node::showNode)
                        .forEach(graph::add);
            else nodes.stream()
                    .filter(node -> !nodesState.contains(node))
                    .forEach(Node::fade);
            nodes = nodesState;
        }
        var edgesState = (List<Edge>) state.get("edges");
        if (edgesState != null) {
            Arrays.stream(graph.getComponents())
                    .filter(c -> c instanceof Edge)
                    .forEach(graph::remove);
            nodes.forEach(node -> {
                node.getConnectedEdges().clear();
                node.getConnectedEdges().addAll(edgesState.stream()
                        .filter(edge -> edge.getSource().equals(node))
                        .toList());
                node.getConnectedEdges().forEach(graph::add);
            });
        }
    }

    void modifyGraph(MouseEvent point) {
        switch (graphMode) {
            case ADD_NODE -> createNewNode(point);
            case ADD_AN_EDGE -> createNewEdge(point);
            case REMOVE_NODE -> removeNode(point);
            case REMOVE_AN_EDGE -> removeEdge(point);
            case NONE -> {
                if (algorithmMode != AlgMode.NONE) startAlgorithm(point);
            }
        }
    }

    void startAlgorithm(MouseEvent point) {
        checkIfNode(point).ifPresent(selectedNode -> {
            if (!algorithm.checkIfGraphIsConnected(selectedNode)) {
                var messageLabel = new JLabel("<html><div align='center'>Unfortunately, this version of the program " +
                                              "supports connected graphs only.<br>Check back soon for updates");
                JOptionPane.showMessageDialog(graph, messageLabel, "Disconnected graph", JOptionPane.PLAIN_MESSAGE);
                resetComponentsLists();
                return;
            }
            if (Algorithm.root == null) {
                algorithm.initAlgorithm(selectedNode);
                infobar.updateInfo("Please wait...", "");
                timer = new Timer(250, e -> {
                    switch (algorithmMode) {
                        case DEPTH_FIRST_SEARCH -> algorithm.dfsAlgorithm();
                        case BREADTH_FIRST_SEARCH -> algorithm.bfsAlgorithm();
                        case DIJKSTRA_ALGORITHM -> algorithm.dijkstraAlgorithm();
                        case PRIM_ALGORITHM -> algorithm.primAlgorithm();
                        case BELLMAN_FORD_ALGORITHM -> algorithm.bellmanFordAlgorithm();
                    }
                    var algorithmResult = algorithm.getResultIfReady();
                    if (!algorithmResult.isBlank()) {
                        infobar.updateInfo("", algorithmResult);
                        timer.stop();
                        graph.setEnabled(true);
                    }
                    graph.repaint();
                });
                graph.setEnabled(false);
                timer.start();
            } else if (algorithmMode == AlgMode.DIJKSTRA_ALGORITHM) {
                var shortestPath = algorithm.getShortestPath(selectedNode);
                infobar.updateInfo("", shortestPath);
                graph.repaint();
            }
        });
    }

    void createNewNode(MouseEvent point) {
        if (checkIfNode(point).isEmpty()) {
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
                    graph.repaint();
                    graphEdit.end();
                } else {
                    JOptionPane.showMessageDialog(graph, "Id must be one upper or lower case letter or digit. " +
                                                         "Try again", "Error", JOptionPane.ERROR_MESSAGE);
                    createNewNode(point);
                }
            }
        }
    }

    void createNewEdge(MouseEvent point) {
        if (edgeSource == null) {
            checkIfNode(point).ifPresent(sourceNode -> {
                edgeSource = sourceNode;
                edgeSource.marked = true;
                graph.repaint();
            });
            return;
        }
        if (edgeTarget == null) {
            checkIfNode(point).ifPresent(targetNode -> {
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

    void removeNode(MouseEvent point) {
        checkIfNode(point).ifPresent(node -> {
            graphEdit = new StateEdit(this);
            node.getConnectedEdges().forEach(edge -> {
                Stream.of(edge, edge.getMirrorEdge()).forEach(graph::remove);
                edge.getTarget().getConnectedEdges().remove(edge.getMirrorEdge());
                edge.getTarget().connected = edge.getTarget().getConnectedEdges().size() != 0;
            });
            node.fade();
            nodes.remove(node);
            graph.repaint();
            graphEdit.end();
        });
    }

    void removeEdge(MouseEvent point) {
        checkIfEdge(point).ifPresent(edge -> {
            graphEdit = new StateEdit(this);
            Stream.of(edge, edge.getMirrorEdge())
                    .peek(e -> {
                        var sourceEdges = e.getSource().getConnectedEdges();
                        sourceEdges.remove(e);
                        e.getSource().connected = sourceEdges.size() != 0;
                    })
                    .forEach(graph::remove);
            graph.repaint();
            graphEdit.end();
        });
    }

    void clearGraph() {
        undoableEditSupport.removeUndoableEditListener(manager);
        Arrays.stream(graph.getComponents())
                .filter(c -> !(c instanceof Infobar))
                .forEach(graph::remove);
        setCurrentModes(AlgMode.NONE, GraphMode.ADD_NODE);
        nodes.clear();
        graph.repaint();
        undoableEditSupport.addUndoableEditListener(manager = new UndoManager());
    }

    public void setCurrentModes(AlgMode algorithmMode, GraphMode graphMode) {
        if (toolbar == null) toolbar = graph.getToolbar();
        var buttonPanel = (ButtonPanel) toolbar.getButtonPanel();
        var buttonGroup = buttonPanel.getButtonGroup();
        var selected = buttonGroup.getSelection();

        buttonPanel.getAlgModeComboBox().setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algorithmMode));
        buttonPanel.getGraphModeComboBox().setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
        toolbar.updateModeLabels(graphMode.getCurrent().toUpperCase(), algorithmMode.getCurrent().toUpperCase());
        infobar.updateInfo(algorithmMode != AlgMode.NONE ? "Please choose a starting node" : "", "");
        buttonGroup.clearSelection();
        buttonGroup.getElements().asIterator().forEachRemaining(b -> {
            if (b.getModel().equals(selected)) b.getModel().setSelected(true);
        });
        this.graphMode = graphMode;
        this.algorithmMode = algorithmMode;
        algorithm.resetAlgorithmData();
        resetComponentsLists();
        resetMarkedNodes();
    }

    void resetComponentsLists() {
        nodes.forEach(node -> {
            TipManager.setToolTipText(node, "");
            node.distance = Integer.MAX_VALUE;
            node.visited = false;
            node.path = false;
            node.connected = node.getConnectedEdges().size() != 0;
            node.getConnectedEdges().forEach(edge -> {
                edge.hidden = false;
                edge.visited = false;
                edge.path = false;
            });
        });
        TipManager.setToolTipText(graph, "");
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

    private Optional<Node> checkIfNode(MouseEvent event) {
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