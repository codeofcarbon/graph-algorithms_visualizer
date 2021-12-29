package visualizer;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Algorithm {
    private Map<Vertex, List<Edge>> paths;
    LinkedList<Vertex> queue;
    Set<Edge> edgeSet;
    private final AlgorithmMode algorithmMode;
    private final List<Vertex> vertices;
    private final List<Edge> edges;
    StringJoiner chainResult;
    String edgesResult;
    Vertex root;

    public Algorithm(List<Vertex> vertices, List<Edge> edges, AlgorithmMode algorithmMode) {
        this.algorithmMode = algorithmMode;
        this.vertices = vertices;
        this.edges = edges;
    }

    protected Vertex dfsAlgorithm(Vertex rootNode) {
        root = rootNode;
        rootNode.visited = true;
        chainResult.add(String.format("<html><font size=+1><b>%s</b></font>", rootNode.id));
        queue.addLast(rootNode);
        while (true) {
            if (queue.isEmpty()) {
                hideUnnecessaryEdges();
                return null;
            }
            var next = queue.peekLast().connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> {
                        edge.visited = true;
                        edgeSet.add(edge);
                        edge.mirrorEdge.visited = true;
                        edgeSet.add(edge.mirrorEdge);
                        return edge.target;
                    });
            if (next.isEmpty()) queue.pollLast();
            else return next.get();
        }
    }

    protected Vertex bfsAlgorithm(Vertex rootNode) {
        root = rootNode;
        rootNode.visited = true;
        chainResult.add(String.format("<html><font size=+1><b>%s</b></font>", rootNode.id));
        queue.addLast(rootNode);
        while (true) {
            if (queue.isEmpty()) {
                hideUnnecessaryEdges();
                return null;
            }
            var next = queue.peekFirst().connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> {
                        edge.visited = true;
                        edgeSet.add(edge);
                        edge.mirrorEdge.visited = true;
                        edgeSet.add(edge.mirrorEdge);
                        return edge.target;
                    });
            if (next.isEmpty()) queue.removeFirst();
            else return next.get();
        }
    }

    private void hideUnnecessaryEdges() {
        switch (algorithmMode) {
            case DIJKSTRA_ALGORITHM:
                edges.stream()
                        .filter(edge -> !edgeSet.contains(edge))
                        .forEach(edge -> edge.hidden = true);
                break;
            case DEPTH_FIRST_SEARCH:
            case BREADTH_FIRST_SEARCH:
            case PRIM_ALGORITHM:
                edges.forEach(edge -> {
                    if (edgeSet.contains(edge)) edge.visited = true;
                    else edge.hidden = true;
                });
        }
    }

    protected void initAlgorithm(Vertex rootNode) {
        paths = new HashMap<>();
        edgeSet = new HashSet<>();
        queue = new LinkedList<>(vertices);
        rootNode.distance = 0;
        rootNode.visited = true;
        root = rootNode;
        vertices.forEach(vertex -> paths.put(vertex, new ArrayList<>()));
        root.connectedEdges.forEach(edge -> {
            edge.target.distance = edge.weight;
            paths.get(edge.target).add(edge);
        });
        queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
    }

//    protected void initAlgorithm(Vertex rootNode) {
//        root = rootNode;
//        rootNode.visited = true;
////        edgeSet = new HashSet<>();
////        chainResult = new StringJoiner(" > ");
////        edgesResult = "";
//        if (algorithmMode == AlgorithmMode.DIJKSTRA_ALGORITHM || algorithmMode == AlgorithmMode.PRIM_ALGORITHM) {
//                    paths = new HashMap<>();
//                    queue = new LinkedList<>(vertices);
//                    rootNode.distance = 0;
//                    vertices.forEach(vertex -> paths.put(vertex, new ArrayList<>()));
//                    root.connectedEdges.forEach(edge -> {
//                        edge.target.distance = edge.weight;
//                        paths.get(edge.target).add(edge);
//                    });
//                    queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
//            }
//        }

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

        if (vertices.stream().allMatch(v -> v.visited)) {
            paths.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(edge -> edgeSet.addAll(List.of(edge, edge.mirrorEdge)));

            edgesResult = vertices.stream()
                    .filter(v -> !v.equals(root))
                    .sorted(Comparator.comparing(v -> v.id))
                    .map(v -> String.format("<html><font size=+1 color=white><i><b> %s - </b></i></font>" +
                                            "<font size=+1 color=green><i><b>%d</b></i></font>",
                            v.id, v.distance))
                    .collect(Collectors.joining(","));
        }
    }

    protected void primAlgorithm() {
        edges.stream()
                .filter(edge -> edge.source.visited && !edge.target.visited)
                .min(Comparator.comparingInt(edge -> edge.weight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edge.mirrorEdge.visited = true;
                    edge.target.visited = true;
                    edgeSet.add(edge);
                });

        if (vertices.stream().allMatch(v -> v.visited)) {
            hideUnnecessaryEdges();
            edgesResult = edgeSet.stream()
                    .sorted(Comparator.comparing(edge -> edge.source.id))
                    .map(e -> String.format("<html><font size=+1 color=white><i><b> %s-%s </b></i></font>",
                            e.source.id, e.target.id))
                    .collect(Collectors.joining(","));
        }
    }
}