package visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    LinkedList<Vertex> queue;
    //    Set<Vertex> settled;                                        // todo is that still needed??
    Set<Edge> edgeSet;                                                // todo is that still needed??
    StringJoiner chainResult;
    String edgesResult;
    Vertex root;
    private final List<Vertex> vertices;
    private final List<Edge> edges;                                   // todo maybe make a map for storing edges

    public Algorithm(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    protected Vertex dfsAlgorithm(Vertex root) {
        root.visited = true;
        chainResult.add(root.id);
        queue.addLast(root);
        while (true) {
            if (queue.isEmpty()) {
                hideEdges();
                return null;
            }
            var next = queue.peekLast().connectedEdges.stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> {
                        edge.visited = true;
                        edgeSet.add(edge);
                        edge.second.connectedEdges.stream()                                 // todo sth??
                                .filter(e -> e.second.equals(edge.first))
                                .peek(edgeSet::add)
                                .forEach(e -> e.visited = true);
                        return edge.second;
                    });
            if (next.isEmpty()) queue.pollLast();
            else return next.get();
        }
    }

    protected Vertex bfsAlgorithm(Vertex root) {
        root.visited = true;
        chainResult.add(root.id);
        queue.addLast(root);
        while (true) {
            if (queue.isEmpty()) {
                hideEdges();
                return null;
            }
            var next = queue.peekFirst().connectedEdges.stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> {
                        edge.visited = true;
                        edgeSet.add(edge);
                        edge.second.connectedEdges.stream()                                 // todo sth??
                                .filter(e -> e.second.equals(edge.first))
                                .peek(edgeSet::add)
                                .forEach(e -> e.visited = true);
                        return edge.second;
                    });
            if (next.isEmpty()) queue.removeFirst();
            else return next.get();
        }
    }

    protected void dijkstraAlgorithm() {
//        settled.add(current);                                               // todo is that still needed?/?
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.visited = true;
            current.connectedEdges.stream()
                    .filter(edge -> !edge.second.visited)                      //todo !settled.contains(edge.second))
                    .peek(edge -> {
                        if (edge.second.distance > edge.first.distance + edge.weight)
                            edge.second.distance = edge.first.distance + edge.weight;
                    })
                    .min(Comparator.comparingInt(edge -> edge.second.distance))
                    .ifPresent(edge -> {
                        edge.visited = true;
                        edge.second.connectedEdges.stream()                                 // todo sth??
                                .filter(e -> e.second.equals(edge.first))
                                .forEach(e -> e.visited = true);
                    });
            queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
        }

        if (vertices.stream().allMatch(v -> v.visited)) {
            vertices.stream()
                    .sorted(Comparator.comparingInt(vertex -> vertex.distance))
                    .map(v -> v.connectedEdges)
                    .forEach(connected -> connected.stream()
                            .filter(edge -> !edge.second.visited)
                            .min(Comparator.comparingInt(edge -> edge.second.distance))
                            .ifPresent(edge -> {
                                edgeSet.add(edge);
                                edge.second.visited = true;
                                edge.second.connectedEdges.stream()                                 // todo sth??
                                        .filter(e -> e.second.equals(edge.first))
                                        .peek(e -> e.second.visited = true)
                                        .forEach(edgeSet::add);
                            }));
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

    private void hideEdges() {
        edges.stream()
                .filter(edge -> !edgeSet.contains(edge))
                .peek(edge -> edge.setVisible(false))
                .forEach(edge -> edge.hidden = true);
    }

    protected void initAlgorithm(Vertex rootNode) {
        edgeSet = new HashSet<>();
        queue = new LinkedList<>(vertices);
        rootNode.distance = 0;                                           // todo could it be here not in vertex field?
        rootNode.visited = true;                                         // todo?
        root = rootNode;                                                 // todo remove??
//        settled = new HashSet<>();                                     // todo is that still needed?/?
//        settled.add(rootNode);                                         // todo is that still needed?/?
        queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
    }

    protected void primAlgorithm() {
        edges.stream()
                .filter(edge -> edge.first.visited && !edge.second.visited)
                .min(Comparator.comparingInt(edge -> edge.weight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edgeSet.add(edge);
                    edge.second.visited = true;
                    edge.second.connectedEdges.stream()                   // todo sth??
                            .filter(e -> e.second.equals(edge.first))
                            .forEach(e -> e.visited = true);
                });

        if (vertices.stream().allMatch(v -> v.visited)) {
            hideEdges();
            edgesResult = edgeSet.stream()
                    .sorted(Comparator.comparing(edge -> edge.first.id))
                    .map(e -> String.format("<html><font size=+1 color=white><i><b> %s-%s </b></i></font>",
                            e.first.id, e.second.id))
                    .collect(Collectors.joining(","));
        }
    }
}