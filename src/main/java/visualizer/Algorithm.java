package visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    LinkedList<Vertex> queue;
//    LinkedList<Vertex> settled;                                             // todo is that still needed??
    Set<Edge> edgeSet;                                                      // todo is that still needed??
    StringJoiner chainResult;
    String edgesResult;
    Vertex root;
    private final List<Vertex> vertices;
    private final List<Edge> edges;                                         // todo maybe make a map for storing edges

    public Algorithm(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    protected Vertex dfsAlgorithm(Vertex rootNode) {
        root = rootNode;
        rootNode.visited = true;
        chainResult.add(rootNode.id);
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
        chainResult.add(rootNode.id);
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

    protected void dijkstraAlgorithm() {
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.visited = true;
//            settled.add(current);                                           // todo is that still needed?/?
            current.connectedEdges.stream()
                    .filter(edge -> !edge.target.visited)                     // todo !settled.contains(edge.second))
                    .peek(edge -> {
                        if (edge.target.distance > edge.source.distance + edge.weight)
                            edge.target.distance = edge.source.distance + edge.weight;
                    })
                    .min(Comparator.comparingInt(edge -> edge.target.distance))
                    .ifPresent(edge -> {
                        edge.visited = true;
                        edge.mirrorEdge.visited = true;
                    });
            queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
        }

        if (vertices.stream().allMatch(v -> v.visited)) {

//            for (int i = 0; i + 1 < settled.size(); i++) {
//                var current = settled.get(i);
//                var next = settled.get(i + 1);
//                current.connectedEdges.stream()                             // todo sth??
//                        .filter(e -> e.target.equals(next))
//                        .forEach(edgeSet::add);
//                next.connectedEdges.stream()                                // todo sth??
//                        .filter(e -> e.target.equals(current))
//                        .forEach(edgeSet::add);
//                }

//            vertices.stream()
//                    .sorted(Comparator.comparingInt(vertex -> vertex.distance))
//                    .map(v -> v.connectedEdges)
//                    .forEach(connected -> connected.stream()
//                            .filter(edge -> !edge.second.visited)
//                            .min(Comparator.comparingInt(edge -> edge.second.distance))
//                            .ifPresent(edge -> {
//                                edgeSet.add(edge);
//                                edge.second.visited = true;
//                                edge.second.connectedEdges.stream()       // todo sth??
//                                        .filter(e -> e.second.equals(edge.first))
//                                        .peek(e -> e.second.visited = true)
//                                        .forEach(edgeSet::add);
//                            }));
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
                .peek(edge -> edge.setVisible(false))                       // todo still visible!
                .forEach(edge -> edge.hidden = true);
    }

    protected void initAlgorithm(Vertex rootNode) {
        edgeSet = new HashSet<>();
        queue = new LinkedList<>(vertices);
        rootNode.distance = 0;                                              // todo could it be here not in vertex field?
        rootNode.visited = true;                                            // todo?
        root = rootNode;                                                    // todo remove??
//        settled = new LinkedList<>();                                       // todo is that still needed?/?
//        settled.add(rootNode);                                              // todo is that still needed?/?
        queue.sort(Comparator.comparingInt(vertex -> vertex.distance));
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