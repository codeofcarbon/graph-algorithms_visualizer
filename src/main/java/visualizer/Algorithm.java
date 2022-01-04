package visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    private static LinkedList<Vertex> queue;
    private static Set<Edge> edgeSet;
    private static LinkedList<Vertex> nodeList;
    private static String algorithmResult;
    private static Map<Vertex, List<Edge>> paths;
    private final GraphService service;
    static Vertex root, target;

    public Algorithm(GraphService service) {
        this.service = service;
    }

    protected void dfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekLast().connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresentOrElse(edge -> {
                        List.of(edge, edge.mirrorEdge).forEach(e -> {
                            e.visited = true;
                            edgeSet.add(e);
                        });
                        edge.target.visited = true;
                        queue.addLast(edge.target);
                        nodeList.add(edge.target);
                    }, () -> queue.pollLast());
        }
    }

    protected void bfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekFirst().connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresentOrElse(edge -> {
                        List.of(edge, edge.mirrorEdge).forEach(e -> {
                            e.visited = true;
                            edgeSet.add(e);
                        });
                        edge.target.visited = true;
                        queue.addLast(edge.target);
                        nodeList.add(edge.target);
                    }, () -> queue.pollFirst());
        }
    }

    protected void dijkstraAlgorithm() {
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)
                    .forEach(edge -> {
                        if (edge.target.distance > edge.source.distance + edge.weight) {
                            edge.target.distance = edge.source.distance + edge.weight;
                            paths.get(edge.target).clear();
                            paths.get(edge.target).addAll(paths.get(current));
                            paths.get(edge.target).add(edge);
                        }
                    });
            current.visited = true;
            queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
        }
        if (service.getGraph().getVertices().stream().allMatch(vertex -> vertex.visited)) {
            paths.values().forEach(pathEdges -> edgeSet.addAll(pathEdges));
        }
    }

    protected void primAlgorithm() {
        service.getGraph().getEdges().stream()
                .filter(edge -> edge.source.visited && !edge.target.visited)
                .min(Comparator.comparingInt(edge -> edge.weight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edge.mirrorEdge.visited = true;
                    edge.target.visited = true;
                    edgeSet.add(edge);
                });
    }

    protected void initAlgorithm(Vertex rootNode) {
        resetAlgorithm();
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
                queue.addAll(service.getGraph().getVertices());
                rootNode.distance = 0;
                service.getGraph().getVertices().forEach(vertex -> paths.put(vertex, new ArrayList<>()));
                root.connectedEdges.forEach(edge -> {
                    edge.target.distance = edge.weight;
                    paths.get(edge.target).add(edge);
                });
                queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
        }
    }

    protected String getResultIfReady() {
        if (service.getGraph().getVertices().stream().allMatch(vertex -> vertex.visited)) {
            service.getGraph().getEdges().forEach(edge -> edge.hidden = true);
            edgeSet.forEach(edge -> edge.visited = true);
            switch (service.getAlgorithmMode()) {
                case DEPTH_FIRST_SEARCH:
                    algorithmResult = String.format("<html><font size=+1 color=white>DFS for " +
                                                    "<font size=+2 color=#5afa46><b>%s</b>" +
                                                    "<font size=+1 color=white>:   ", root.id) +
                                      nodeList.stream()
                                              .map(vertex -> String.format(
                                                      "<font size=+1 color=#0062ff><b>%s</b>", vertex.id))
                                              .collect(Collectors.joining(" > "));
                    break;
                case BREADTH_FIRST_SEARCH:
                    algorithmResult = String.format("<html><font size=+1 color=white>BFS for " +
                                                    "<font size=+2 color=#5afa46><b>%s</b>" +
                                                    "<font size=+1 color=white>:   ", root.id) +
                                      nodeList.stream()
                                              .map(vertex -> String.format(
                                                      "<font size=+1 color=#0062ff><b>%s</b>", vertex.id))
                                              .collect(Collectors.joining(" > "));
                    break;
                case DIJKSTRA_ALGORITHM:
                    algorithmResult = String.format("<html><font size=+1 color=white>shortest paths from " +
                                                    "<font size=+2 color=#5afa46><b>%s</b>" +
                                                    "<font size=+1 color=white>:   ", root.id) +
                                      service.getGraph().getVertices().stream()
                                              .filter(vertex -> !vertex.equals(root))
                                              .sorted(Comparator.comparing(vertex -> vertex.id))
                                              .map(vertex -> String.format(
                                                      "<font size=+1 color=#0062ff> %s" +
                                                      "<font color=#eb4034> -> %d", vertex.id, vertex.distance))
                                              .collect(Collectors.joining("<font color=white>,"));

                    service.getGraph().setToolTipText("<html><font size=+1>click on a node to see the shortest path");
                    break;
                case PRIM_ALGORITHM:
                    algorithmResult = "<html><font size=+1>minimum spanning tree:" +
                                      edgeSet.stream()
                                              .sorted(Comparator.comparing(edge -> edge.source.id))
                                              .map(edge -> String.format("<font size=+1 color=#0062ff><b> %s - %s</b>",
                                                      edge.source.id, edge.target.id))
                                              .collect(Collectors.joining("<font color=white>,"));
            }
        }
        return algorithmResult;
    }

    String getShortestPath(Vertex targetNode) {
        edgeSet.forEach(edge -> edge.path = false);
        paths.keySet().forEach(vertex -> vertex.path = false);

        if ((target = targetNode).equals(root)) {
            target.path = false;
            edgeSet.forEach(edge -> edge.visited = true);
            return algorithmResult;
        }
        paths.get(target).forEach(edge -> {
            edge.path = true;
            if (edge.source != root) edge.source.path = true;
            if (edge.target != root) edge.target.path = true;
        });
        return String.format("<html><font size=+1 color=gray>shortest path from " +
                             "<b><font size=+2 color=#5afa46>%s</b><font size=+1 color=gray> to " +
                             "<b><font size=+2 color=#5afa46>%s</b><font size=+1 color=gray>:   ", root.id, target.id) +
               paths.get(target).stream()
                       .map(edge -> String.format("<font size=+1 color=0062ff> %s - %s",
                               edge.source.id, edge.target.id))
                       .collect(Collectors.joining("<font color=white> > ")) +
               String.format("<font size=+2 color=#eb4034>   -> %d", target.distance);
    }

    protected void resetAlgorithm() {
        queue = new LinkedList<>();
        edgeSet = new HashSet<>();
        nodeList = new LinkedList<>();
        paths = new HashMap<>();
        algorithmResult = "";
        target = null;
        root = null;
    }
}