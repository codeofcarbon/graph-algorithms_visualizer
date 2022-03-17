package visualizer;

import java.util.*;
import java.util.stream.*;

public class Algorithm {
    private static final LinkedList<Node> queue = new LinkedList<>();
    private static final LinkedList<Node> nodeResult = new LinkedList<>();
    private static final LinkedList<Edge> edgeResult = new LinkedList<>();
    private static final Map<Node, List<Edge>> paths = new HashMap<>();
    private static String algorithmResult;
    private final GraphService service;
    static Node root, target;

    public Algorithm(GraphService service) {
        this.service = service;
    }

    protected void dfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekLast().getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .min(Comparator.comparingInt(Edge::getWeight))
                    .ifPresentOrElse(edge -> {
                        Stream.of(edge, edge.getMirrorEdge())
                                .peek(e -> e.visited = true)
                                .forEach(edgeResult::add);
                        edge.getTarget().visited = true;
                        queue.addLast(edge.getTarget());
                        nodeResult.add(edge.getTarget());
                    }, queue::pollLast);
        }
    }

    protected void bfsAlgorithm() {
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

    protected void dijkstraAlgorithm() {
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .forEach(edge -> {
                        if (edge.getTarget().distance > edge.getSource().distance + edge.getWeight()) {
                            edge.getTarget().distance = edge.getSource().distance + edge.getWeight();
                            paths.get(edge.getTarget()).clear();
                            paths.get(edge.getTarget()).addAll(paths.get(current));
                            paths.get(edge.getTarget()).add(edge);
                        }
                    });
            current.visited = true;
            queue.sort(Comparator.comparingInt(Node::getDistance));
        }
        if (service.getNodes().stream().allMatch(Node::isVisited)) {
            paths.values().forEach(edgeResult::addAll);
        }
    }

    protected void primAlgorithm() {
        service.getNodes().stream()
                .flatMap(node -> node.getConnectedEdges().stream())
                .filter(edge -> edge.getSource().visited && !edge.getTarget().visited)
                .min(Comparator.comparingInt(Edge::getWeight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edge.getMirrorEdge().visited = true;
                    edge.getTarget().visited = true;
                    edgeResult.add(edge);
                });
    }

    protected void initAlgorithm(Node rootNode) {
        service.resetComponentsLists();
        resetAlgorithmData();
        root = rootNode;
        rootNode.visited = true;
        switch (service.getAlgorithmMode()) {
            case DEPTH_FIRST_SEARCH:
            case BREADTH_FIRST_SEARCH:
                queue.addLast(rootNode);
                nodeResult.add(rootNode);
                break;
            case DIJKSTRA_ALGORITHM:
            case PRIM_ALGORITHM:
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

    protected String getResultIfReady() {
        if (service.getNodes().stream().allMatch(Node::isVisited)) {
            service.getNodes().stream()
                    .flatMap(node -> node.getConnectedEdges().stream())
                    .forEach(edge -> edge.hidden = true);
            edgeResult.forEach(edge -> edge.visited = true);
            switch (service.getAlgorithmMode()) {
                case DEPTH_FIRST_SEARCH:
                case BREADTH_FIRST_SEARCH:
                    algorithmResult = String.format("<html><font size=+1 color=gray>%s for " +
                                                    "<font size=+2 color=#5afa46><b>" + root.getId() + "</b>" +
                                                    "<font size=+1 color=gray>:   %s",
                            service.getAlgorithmMode() == AlgMode.DEPTH_FIRST_SEARCH ? "DFS" : "BFS",
                            nodeResult.stream()
                                    .map(node -> "<font size=+1 color=#0062ff><b>" + node.getId() + "</b>")
                                    .collect(Collectors.joining(" &rarr ")));
                    break;
                case DIJKSTRA_ALGORITHM:
                    var joinedResult = service.getNodes().stream()
                            .filter(node -> !node.equals(root))
                            .sorted(Comparator.comparing(Node::getId))
                            .map(node -> "<font size=+1 color=#0062ff> " + node.getId() + "<font color=red> &#8680 " +
                                         (node.distance == Integer.MAX_VALUE ? "inf" : node.distance))
                            .collect(Collectors.joining("<font color=gray>,"));

                    algorithmResult = "<html><font size=+1 color=gray>shortest distances from " +
                                      "<font size=+2 color=#5afa46><b>" + root.getId() +
                                      "</b><font size=+1 color=gray>:   " + joinedResult;

                    service.getGraph().setToolTipText("<html><div align=right><font color=rgb(128,128,128)>" +
                                                      "click on a node to see the shortest path");
                    break;
                case PRIM_ALGORITHM:
                    algorithmResult = "<html><font size=+1 color=gray>minimum spanning tree:" + edgeResult.stream()
                            .map(edge -> String.format("<font size=+1 color=#0062ff><b> %s &#8644 %s</b>",
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
            resultJoiner.add(String.format("<font size=+1 color=0062ff> %s &#8644 %s",
                    edge.getSource().getId(), edge.getTarget().getId()));
        });

        return "<html><font size=+1 color=gray>shortest path from " +
               "<b><font size=+2 color=#5afa46>" + root.getId() + "</b><font size=+1 color=gray> to " +
               "<b><font size=+2 color=#5afa46>" + target.getId() + "</b><font size=+1 color=gray>:   " +
               resultJoiner + "<font size=+2 color=#eb4034>   &#8680 " +
               (target.distance == Integer.MAX_VALUE || target.distance == Integer.MIN_VALUE ? "inf" : target.distance);
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