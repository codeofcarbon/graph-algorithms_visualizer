package visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    private Map<Vertex, List<Edge>> paths;
    private LinkedList<Vertex> queue;
    private Set<Edge> edgeSet;
    private final Graph graph;
    static List<Edge> shortestPath;
    static Vertex root;
    StringJoiner chainResult;
    String edgesResult;

    public Algorithm(Graph graph) {
        shortestPath = new LinkedList<>();
        this.graph = graph;
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
                        chainResult.add(String.format("<html><font size=+1><b>%s</b></font>", edge.target.id));
                    }, () -> queue.pollLast());
        }
        getResultIfReady();
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
                        chainResult.add(String.format("<html><font size=+1><b>%s</b></font>", edge.target.id));
                    }, () -> queue.pollFirst());
        }
        getResultIfReady();
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
        getResultIfReady();
    }

    protected void primAlgorithm() {
        graph.edges.stream()
                .filter(edge -> edge.source.visited && !edge.target.visited)
                .min(Comparator.comparingInt(edge -> edge.weight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edge.mirrorEdge.visited = true;
                    edge.target.visited = true;
                    edgeSet.add(edge);
                });
        getResultIfReady();
    }

    protected void initAlgorithm(Vertex rootNode) {
        root = rootNode;
        rootNode.visited = true;
        edgeSet = new HashSet<>();
        chainResult = new StringJoiner(" > ");
        edgesResult = "";
        switch (graph.algorithmMode) {
            case DEPTH_FIRST_SEARCH:
            case BREADTH_FIRST_SEARCH:
                queue = new LinkedList<>();
                queue.addLast(rootNode);
                chainResult.add(String.format("<html><font size=+1><b>%s</b></font>", rootNode.id));
                break;
            case DIJKSTRA_ALGORITHM:
            case PRIM_ALGORITHM:
                paths = new HashMap<>();
                queue = new LinkedList<>(graph.vertices);
                rootNode.distance = 0;
                graph.vertices.forEach(vertex -> paths.put(vertex, new ArrayList<>()));
                root.connectedEdges.forEach(edge -> {
                    edge.target.distance = edge.weight;
                    paths.get(edge.target).add(edge);
                });
                queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
        }
    }

    private void getResultIfReady() {
        if (graph.vertices.stream().allMatch(v -> v.visited)) {
            if (graph.algorithmMode == AlgorithmMode.DIJKSTRA_ALGORITHM) {
                paths.values().stream()
                        .flatMap(Collection::stream)
                        .forEach(edge -> edgeSet.addAll(List.of(edge, edge.mirrorEdge)));
            }
            hideUnnecessaryEdges();
            switch (graph.algorithmMode) {
                case DIJKSTRA_ALGORITHM:
                    edgesResult = graph.vertices.stream()
                            .filter(v -> !v.equals(root))
                            .sorted(Comparator.comparing(v -> v.id))
                            .map(v -> String.format(
                                    "<html><font size=+1 color=white><i><b> %s - </b></i></font>" +
                                    "<font size=+1 color=green><i><b>%d</b></i></font>",
                                    v.id, v.distance))
                            .collect(Collectors.joining(","));
                    break;
                case PRIM_ALGORITHM:
                    edgesResult = edgeSet.stream()
                            .sorted(Comparator.comparing(edge -> edge.source.id))
                            .map(e -> String.format(
                                    "<html><font size=+1 color=white><i><b> %s - %s</b></i></font>",
                                    e.source.id, e.target.id))
                            .collect(Collectors.joining(","));
            }
        }
    }

    private void hideUnnecessaryEdges() {
        graph.edges.stream()
                .filter(edge -> !edgeSet.contains(edge))
                .forEach(edge -> edge.hidden = true);
    }

    protected void resetAlgorithm() {
        queue = new LinkedList<>();
        edgeSet = new HashSet<>();
        shortestPath = new LinkedList<>();
        chainResult = new StringJoiner(" > ");
        edgesResult = "";
        root = null;
    }
}