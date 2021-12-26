package visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    LinkedList<Vertex> queue;
    Set<Vertex> settled;                                        // todo is that still needed??
    StringJoiner chainResult;
    String edgesResult;
    Vertex root;
    private final List<Vertex> vertices;

    public Algorithm(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    protected Vertex dfsAlgorithm(Vertex root) {
        root.visited = true;
        root.getParent().repaint();
        chainResult.add(root.id);
        queue.addLast(root);
        while (true) {
            if (queue.isEmpty()) return null;
            var next = queue.peekLast().connectedEdges.stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> edge.second);
            if (next.isEmpty()) queue.pollLast();
            else return next.get();
        }
    }

    protected Vertex bfsAlgorithm(Vertex root) {
        root.visited = true;
        root.getParent().repaint();
        chainResult.add(root.id);
        queue.addLast(root);
        while (true) {
            if (queue.isEmpty()) return null;
            var next = queue.peekFirst().connectedEdges.stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> edge.second);
            if (next.isEmpty()) queue.removeFirst();
            else return next.get();
        }
    }

    protected void initDijkstraAlgorithm(Vertex rootNode) {
        settled = new HashSet<>();
        queue = new LinkedList<>(vertices);
        rootNode.distance = 0;
        root = rootNode;
        queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
    }

    protected void dijkstraAlgorithm(Vertex current) {
        settled.add(current);                                               // todo is that still needed?/?
        current.visited = true;

        current.connectedEdges.stream()
                .filter(edge -> !settled.contains(edge.second))
                .forEach(edge -> {
                    if (edge.second.distance > edge.first.distance + edge.weight)
                        edge.second.distance = edge.first.distance + edge.weight;
                });
        queue.sort(Comparator.comparingInt(vertex -> vertex.distance));

        if (vertices.size() == settled.size()) {
            edgesResult = settled.stream()
                    .filter(v -> !v.equals(root))
                    .sorted(Comparator.comparing(v -> v.id))
                    .map(v -> String.format("<html><font size=+1 color=white><i><b> %s - </b></i></font>" +
                                            "<font size=+1 color=green><i><b>%d</b></i></font>",
                            v.id, v.distance))
                    .collect(Collectors.joining(","));
        }
    }

    protected void primAlgorithm(Vertex root) {
        var edges = new HashSet<Edge>();            // todo diff formatting. now is : child-->parent edges
        root.visited = true;
        root.getParent().repaint();
        root.connectedEdges.stream()
                .min(Comparator.comparingInt(edge -> edge.weight))
                .map(edge -> edge.second)
                .ifPresent(vertex -> {
                    vertex.visited = true;
                    vertex.connectedEdges.stream()
                            .filter(edge -> edge.second.equals(root))
                            .forEach(edges::add);
                });
        while (true) {
            vertices.stream()
                    .filter(vertex -> !vertex.visited)
                    .map(vertex -> vertex.connectedEdges)
                    .flatMap(Collection::stream)
                    .filter(edge -> !edge.first.visited && edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresent(edge -> {
                        edges.add(edge);
                        edge.first.visited = true;
                    });
            if (vertices.stream().allMatch(v -> v.visited)) {
                edgesResult = "[edges forming the minimum spanning tree] >>> "
                        .concat(edges.stream()
                                .sorted(Comparator.comparing(edge -> edge.first.id))
                                .map(edge -> String.format("%s - %s", edge.first.id, edge.second.id))
                                .collect(Collectors.joining(", ")));
                return;
            }
        }
    }
}