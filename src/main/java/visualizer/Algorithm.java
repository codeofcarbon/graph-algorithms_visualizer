package visualizer;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Algorithm {
    LinkedList<Vertex> queue;
    Set<Edge> edgeSet;
    StringJoiner chainResult;
    String edgesResult;
    Map<Vertex, List<Edge>> paths;
    Vertex root;
    private final List<Vertex> vertices;
    private final List<Edge> edges;

    public Algorithm(List<Vertex> vertices, List<Edge> edges) {
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
                hideEdges();
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
                hideEdges();
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

    private void hideEdges() {
        edges.forEach(edge -> {
                            if (edgeSet.contains(edge)) edge.visited = true;
                            else {
                                edge.hidden = true;
                                edge.setVisible(false);                      // todo still visible!
                            }
                        });
    }

    protected void initAlgorithm(Vertex rootNode) {
        paths = new HashMap<>();
        edgeSet = new HashSet<>();
        queue = new LinkedList<>(vertices);
        rootNode.distance = 0;                                         // todo could it be here not in vertex field?
        rootNode.visited = true;
        root = rootNode;
        vertices.forEach(vertex -> paths.put(vertex, new ArrayList<>()));
        root.connectedEdges.forEach(edge -> {
            edge.target.distance = edge.weight;
            paths.get(edge.target).add(edge);
        });
        queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
    }

    protected void dijkstraAlgorithm() {
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)
                    .peek(edge -> {
                        if (edge.target.distance > edge.source.distance + edge.weight) {
                            edge.target.distance = edge.source.distance + edge.weight;
                            paths.get(edge.target).clear();
                            paths.get(edge.target).addAll(paths.get(current));
                            paths.get(edge.target).add(edge);
                        }
                    })
                    .min(Comparator.comparingInt(edge -> edge.target.distance))
                    .ifPresent(edge -> {
                        edge.visited = true;
                        edge.mirrorEdge.visited = true;
                    });
            current.visited = true;
            queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
        }

        if (vertices.stream().allMatch(v -> v.visited)) {
            paths.entrySet().stream()
                    .peek(entry -> {
                        System.err.println(entry.getKey().id);
                        entry.getValue().forEach(edge -> System.err.print(edge.getName() + " "));
                        System.err.println();
                    })
                    .map(Map.Entry::getValue)
                    .flatMap(Collection::stream)
                    .forEach(edge -> edgeSet.addAll(List.of(edge, edge.mirrorEdge)));
            hideEdges();
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
            hideEdges();
            edgesResult = edgeSet.stream()
                    .sorted(Comparator.comparing(edge -> edge.source.id))
                    .map(e -> String.format("<html><font size=+1 color=white><i><b> %s-%s </b></i></font>",
                            e.source.id, e.target.id))
                    .collect(Collectors.joining(","));
        }
    }
}