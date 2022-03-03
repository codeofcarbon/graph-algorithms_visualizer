package visualizer;

import java.util.*;
import java.util.stream.*;

public class Algorithm {
    private static LinkedList<Node> queue;
    private static Set<Edge> edgeSet;
    private static LinkedList<Node> nodeList;
    private static String algorithmResult;
    private static Map<Node, List<Edge>> paths;
    private final GraphService service;
    static Node root, target;

    public Algorithm(GraphService service) {
        this.service = service;
    }

    protected void dfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekLast().connectedEdges.stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresentOrElse(edge -> {
                        Stream.of(edge, edge.mirrorEdge)
                                .peek(e -> e.visited = true)
                                .forEach(edgeSet::add);
                        edge.getTarget().visited = true;
                        queue.addLast(edge.getTarget());
                        nodeList.add(edge.getTarget());
                    }, () -> queue.pollLast());
        }
    }

    protected void bfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekFirst().connectedEdges.stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresentOrElse(edge -> {
                        Stream.of(edge, edge.mirrorEdge)
                                .peek(e -> e.visited = true)
                                .forEach(edgeSet::add);
                        edge.getTarget().visited = true;
                        queue.addLast(edge.getTarget());
                        nodeList.add(edge.getTarget());
                    }, () -> queue.pollFirst());
        }
    }

    protected void dijkstraAlgorithm() {
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.connectedEdges.stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .forEach(edge -> {
                        if (edge.getTarget().distance > edge.getSource().distance + edge.weight) {
                            edge.getTarget().distance = edge.getSource().distance + edge.weight;
                            paths.get(edge.getTarget()).clear();
                            paths.get(edge.getTarget()).addAll(paths.get(current));
                            paths.get(edge.getTarget()).add(edge);
                        }
                    });
            current.visited = true;
            queue.sort(Comparator.comparingInt(node -> node.distance));
        }
        if (service.getNodes().stream().allMatch(node -> node.visited)) {
            paths.values().forEach(pathEdges -> edgeSet.addAll(pathEdges));
        }
    }

    protected void primAlgorithm() {
        service.getNodes().stream()
                .flatMap(node -> node.connectedEdges.stream())
                .filter(edge -> edge.getSource().visited && !edge.getTarget().visited)
                .min(Comparator.comparingInt(edge -> edge.weight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edge.mirrorEdge.visited = true;
                    edge.getTarget().visited = true;
                    edgeSet.add(edge);
                });
    }

    protected void initAlgorithm(Node rootNode) {
        resetAlgorithmData();
        root = rootNode;
        rootNode.visited = true;
        switch (service.getAlgorithmMode()) {
            case DEPTH_FIRST_SEARCH:
            case BREADTH_FIRST_SEARCH:
                queue.addLast(rootNode);
                nodeList.add(rootNode);
                break;
            case DIJKSTRA_ALGORITHM:
            case PRIM_ALGORITHM:
                queue.addAll(service.getNodes());
                rootNode.distance = 0;
                service.getNodes().forEach(node -> paths.put(node, new ArrayList<>()));
                root.connectedEdges.forEach(edge -> {
                    edge.getTarget().distance = edge.weight;
                    paths.get(edge.getTarget()).add(edge);
                });
                queue.sort(Comparator.comparingInt(node -> node.distance));
        }
    }

    protected String getResultIfReady() {
        if (service.getNodes().stream().allMatch(node -> node.visited)) {
            service.getNodes().stream()
                    .flatMap(node -> node.connectedEdges.stream())
                    .forEach(edge -> edge.hidden = true);
            edgeSet.forEach(edge -> edge.visited = true);
            switch (service.getAlgorithmMode()) {
                case DEPTH_FIRST_SEARCH:
                    algorithmResult = String.format("<html><font size=+1 color=gray>DFS for " +
                                                    "<font size=+2 color=#5afa46><b>%s</b>" +
                                                    "<font size=+1 color=gray>:   ", root.id) +
                                      nodeList.stream().map(node -> String.format(
                                                      "<font size=+1 color=#0062ff><b>%s</b>", node.id))
                                              .collect(Collectors.joining(" &rarr "));
                    break;
                case BREADTH_FIRST_SEARCH:
                    algorithmResult = String.format("<html><font size=+1 color=gray>BFS for " +
                                                    "<font size=+2 color=#5afa46><b>%s</b>" +
                                                    "<font size=+1 color=gray>:   ", root.id) +
                                      nodeList.stream().map(node -> String.format(
                                                      "<font size=+1 color=#0062ff><b>%s</b>", node.id))
                                              .collect(Collectors.joining(" &rarr "));
                    break;
                case DIJKSTRA_ALGORITHM:
                    algorithmResult = String.format("<html><font size=+1 color=gray>shortest distances from " +
                                                    "<font size=+2 color=#5afa46><b>%s</b>" +
                                                    "<font size=+1 color=gray>:   ", root.id) +
                                      service.getNodes().stream()
                                              .filter(node -> !node.equals(root))
                                              .sorted(Comparator.comparing(node -> node.id))
                                              .map(node -> String.format(
                                                      "<font size=+1 color=#0062ff> %s<font color=#eb4034> &#8680 %s",
                                                      node.id, node.distance == Integer.MAX_VALUE ||
                                                               node.distance == Integer.MIN_VALUE ?
                                                              "inf" : String.valueOf(node.distance)))
                                              .collect(Collectors.joining("<font color=gray>,"));

                    service.getGraph().setToolTipText("<html><font size=+1>click on a node to see the shortest path");
                    break;
                case PRIM_ALGORITHM:
                    algorithmResult = "<html><font size=+1 color=gray>minimum spanning tree:" +
                                      edgeSet.stream()
                                              .sorted(Comparator.comparing(edge -> edge.getSource().id))
                                              .map(edge -> String.format(
                                                      "<font size=+1 color=#0062ff><b> %s &#8644 %s</b>",
                                                      edge.getSource().id, edge.getTarget().id))
                                              .collect(Collectors.joining("<font color=gray>,"));
            }
        }
        return algorithmResult;
    }

    String getShortestPath(Node targetNode) {
        edgeSet.forEach(edge -> edge.path = false);
        paths.keySet().forEach(node -> node.path = false);

        if ((target = targetNode).equals(root)) {
            target.path = false;
            edgeSet.forEach(edge -> edge.visited = true);
            return algorithmResult;
        }
        paths.get(target).forEach(edge -> {
            edge.path = true;
            if (edge.getSource() != root) edge.getSource().path = true;
            if (edge.getTarget() != root) edge.getTarget().path = true;
        });
        return String.format("<html><font size=+1 color=gray>shortest path from " +
                             "<b><font size=+2 color=#5afa46>%s</b><font size=+1 color=gray> to " +
                             "<b><font size=+2 color=#5afa46>%s</b><font size=+1 color=gray>:   ", root.id, target.id) +
               paths.get(target).stream()
                       .map(edge -> String.format("<font size=+1 color=0062ff> %s &#8644 %s",
                               edge.getSource().id, edge.getTarget().id))
                       .collect(Collectors.joining("<font color=gray> &rarr ")) +
               String.format("<font size=+2 color=#eb4034>   &#8680 %s",
                       target.distance == Integer.MAX_VALUE || target.distance == Integer.MIN_VALUE ?
                               "inf" : String.valueOf(target.distance));
    }

    protected void resetAlgorithmData() {
        queue = new LinkedList<>();
        edgeSet = new HashSet<>();
        nodeList = new LinkedList<>();
        paths = new HashMap<>();
        algorithmResult = "";
        target = null;
        root = null;
    }
}