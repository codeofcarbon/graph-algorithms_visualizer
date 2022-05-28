package com.codeofcarbon.visualizer;

import com.codeofcarbon.visualizer.view.AlgMode;
import com.codeofcarbon.visualizer.view.Edge;
import com.codeofcarbon.visualizer.view.Node;
import com.codeofcarbon.visualizer.view.TipManager;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Algorithm {
    protected static final LinkedList<Node> queue = new LinkedList<>();
    protected static final LinkedList<Node> nodeResult = new LinkedList<>();
    protected static final LinkedList<Edge> edgeResult = new LinkedList<>();
    protected static final Map<Node, List<Edge>> paths = new HashMap<>();
    private static String algorithmResult;
    private final GraphService service;
    public static Node root, target;

    public Algorithm(GraphService service) {
        this.service = service;
    }

    public abstract void execute();

    protected void initAlgorithm(Node rootNode) {
        resetAlgorithmData();
        service.resetComponentsLists();
        root = rootNode;
        rootNode.visited = true;
        switch (service.getAlgorithmMode()) {
            case DEPTH_FIRST_SEARCH, BREADTH_FIRST_SEARCH -> {
                queue.addLast(rootNode);
                nodeResult.add(rootNode);
            }
            case BELLMAN_FORD_ALGORITHM, DIJKSTRA_ALGORITHM, PRIM_ALGORITHM -> {
                queue.addAll(service.getNodes());
                rootNode.distance = 0;
                service.getNodes().forEach(node -> paths.put(node, new ArrayList<>()));
                root.getConnectedEdges().forEach(edge -> {
                    edge.getTarget().distance = edge.getWeight();
                    paths.get(edge.getTarget()).add(edge);
                });
                queue.sort(Comparator.comparingInt(Node::getDistance));
            }
        }
    }

    protected String getResultIfReady() {
        if (service.getNodes().stream().allMatch(Node::isVisited)) {
            service.getNodes().stream()
                    .flatMap(node -> node.getConnectedEdges().stream())
                    .forEach(edge -> edge.hidden = true);
            edgeResult.forEach(edge -> edge.visited = true);
            switch (service.getAlgorithmMode()) {
                case DEPTH_FIRST_SEARCH, BREADTH_FIRST_SEARCH -> algorithmResult =
                        String.format("<html><font color=gray>%s for node " +
                                      "<font size=+1 color=#5afa46><b>" + root.getId() + "</b></font>" +
                                      "<font color=gray>: ",
                                service.getAlgorithmMode() == AlgMode.DEPTH_FIRST_SEARCH ? "DFS" : "BFS") +
                        nodeResult.stream()
                                .map(node -> "<font color=#0062ff><b>" + node.getId() + "</b>")
                                .collect(Collectors.joining(" &rarr "));
                case DIJKSTRA_ALGORITHM -> {
                    var joinedResult = service.getNodes().stream()
                            .filter(node -> !node.equals(root))
                            .sorted(Comparator.comparing(Node::getId))
                            .map(node -> "<font color=#0062ff> " + node.getId() + "<font color=red> &#8680 " +
                                         (node.distance == Integer.MAX_VALUE ? "inf" : node.distance))
                            .collect(Collectors.joining("<font color=gray>,"));
                    algorithmResult = "<html><font color=gray>shortest distances from node " +
                                      "<font size=+1 color=#5afa46><b>" + root.getId() + "</b></font>" +
                                      "<font color=gray>: " + joinedResult;

                    TipManager.setToolTipText(service.getGraph(),
                            "<html><font color=rgb(128,128,128)>click on a node to see the shortest path");
                    service.getNodes().forEach(node -> TipManager.setToolTipText(node,
                            "<html><font color=rgb(128,128,128)>click to see the shortest path"));
                }
                case PRIM_ALGORITHM -> algorithmResult =
                        "<html><font color=gray>minimum spanning tree: " + edgeResult.stream()
                                .map(edge -> String.format("<font color=#0062ff><b> %s &#8644 %s</b>",
                                        edge.getSource().getId(), edge.getTarget().getId()))
                                .collect(Collectors.joining("<font color=gray>,"));
            }
        }
        return algorithmResult;
    }

    protected String getShortestPath(Node targetNode) {
        edgeResult.forEach(edge -> edge.path = false);
        paths.keySet().forEach(node -> node.path = false);
        var resultJoiner = new StringJoiner("<font color=gray> &rarr ");

        if ((target = targetNode).equals(root)) {
            target.path = false;
            edgeResult.forEach(edge -> edge.visited = true);
            return algorithmResult;
        }

        paths.get(target).forEach(edge -> {
            edge.path = true;
            edge.getSource().path = edge.getSource() != root;
            edge.getTarget().path = edge.getTarget() != root;
            resultJoiner.add(String.format("<font color=0062ff> %s &#8644 %s",
                    edge.getSource().getId(), edge.getTarget().getId()));
        });

        return "<html><font color=gray>shortest path from " +
               "<b><font size=+1 color=#5afa46>" + root.getId() + "</b><font color=gray> to " +
               "<b><font size=+1 color=#5afa46>" + target.getId() + "</b><font color=gray>: " +
               resultJoiner + "<font size=+1 color=red>   &#8680 total distance: " +
               (target.distance == Integer.MAX_VALUE ? "inf" : target.distance);
    }

    protected void resetAlgorithmData() {
        List.of(queue, edgeResult, nodeResult).forEach(Collection::clear);
        paths.clear();
        algorithmResult = "";
        target = null;
        root = null;
    }

    boolean checkIfGraphIsConnected(Node rootNode) {
        queue.addLast(rootNode);
        while (!queue.isEmpty()) {
            queue.peekLast().visited = true;
            queue.peekLast().getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .findAny().ifPresentOrElse(edge -> queue.addLast(edge.getTarget()), queue::pollLast);
        }
        return service.getNodes().stream().allMatch(Node::isVisited);
    }
}

class BreadthFirstSearch extends Algorithm {

    public BreadthFirstSearch(GraphService service) {
        super(service);
    }

    @Override
    public void execute() {
        if (!queue.isEmpty()) {
            queue.peekFirst().getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .min(Comparator.comparingInt(Edge::getWeight))
                    .ifPresentOrElse(edge -> {
                        Stream.of(edge, edge.getMirrorEdge())
                                .peek(e -> e.visited = true)
                                .forEach(edgeResult::add);
                        edge.getTarget().visited = true;
                        queue.addLast(edge.getTarget());
                        nodeResult.add(edge.getTarget());
                    }, queue::pollFirst);
        }
    }
}