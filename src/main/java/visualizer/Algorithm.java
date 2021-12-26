package visualizer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.Timer;

public class Algorithm {
    LinkedList<Vertex> queue;
    StringJoiner chainResult;
    String edgesResult;
    private final Graph graph;
    private Timer timer;

    public Algorithm(Graph graph) {
        this.graph = graph;
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

    protected void dijkstraAlgorithm(Vertex root) {
        var settled = new LinkedList<Vertex>();
        root.distance = 0;
        settled.add(root);
        root.visited = true;
        graph.repaint();
        root.connectedEdges.stream()
                .sorted(Comparator.comparing(edge -> edge.weight))
                .peek(edge -> edge.second.distance = edge.weight)
                .map(edge -> edge.second)
                .forEach(queue::addLast);

        timer = new Timer(1000, event -> {
            if (!queue.isEmpty()) {
                graph.edges.stream()
                        .filter(edge -> edge.first.equals(settled.peekLast()) && edge.second.equals(queue.peekFirst())
                                || edge.second.equals(settled.peekLast()) && edge.first.equals(queue.peekFirst()))
                        .forEach(edge -> edge.visited = true);
                queue.peekFirst().visited = true;
                queue.peekFirst().connectedEdges.stream()
                        .filter(edge -> !settled.contains(edge.second))
                        .peek(edge -> {
                            if (edge.second.distance > edge.first.distance + edge.weight)
                                edge.second.distance = edge.first.distance + edge.weight;
                        })
                        .map(edge -> edge.second)
                        .sorted(Comparator.comparing(v -> v.distance))
                        .filter(v -> !v.equals(queue.peekFirst()) && !queue.contains(v))
                        .forEach(queue::addLast);
                settled.add(queue.pollFirst());
                graph.repaint();

                if (graph.vertices.size() == settled.size()) {
                    timer.stop();
                    edgesResult = settled.stream()
                            .filter(v -> !v.equals(root))
                            .sorted(Comparator.comparing(v -> v.id))
                            .map(v -> String.format("<html><font size=+1 color=white><i><b> %s - </b></i></font>" +
                                                    "<font size=+1 color=green><i><b>%d</b></i></font>",
                                    v.id, v.distance))
                            .collect(Collectors.joining(","));
                    graph.displayLabel.setText(
                            "<html><font color=blue><i>shortest distances from </i></font>" +
                            "<font size=+2 color=blue>" + root.id + ":   </font>" + edgesResult);
                }
            }
        });
        timer.start();
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
            graph.vertices.stream()
                    .filter(vertex -> !vertex.visited)
                    .map(vertex -> vertex.connectedEdges)
                    .flatMap(Collection::stream)
                    .filter(edge -> !edge.first.visited && edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresent(edge -> {
                        edges.add(edge);
                        edge.first.visited = true;
                    });
            if (graph.vertices.stream().allMatch(v -> v.visited)) {
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

/*

    protected Vertex dijkstraAlgorithm(Vertex next) {
        if (rootNode == null) {
            rootNode = next;
            next.distance = 0;
            next.settled = true;
            next.getParent().repaint();
            next.connectedEdges.stream()
                    .sorted(Comparator.comparing(edge -> edge.weight))
                    .peek(edge -> edge.second.distance = edge.weight)
                    .map(edge -> edge.second)
                    .forEach(queue::addLast);
        }
            next
                    .connectedEdges.stream()
                    .filter(edge -> edge.second.settled = false)
                    .peek(edge -> {
                        if (edge.second.distance > edge.first.distance + edge.weight)
                            edge.second.distance = edge.first.distance + edge.weight;
                    })
                    .map(edge -> edge.second)
                    .filter(v -> !v.equals(queue.peekFirst()) && !queue.contains(v))
                    .min(Comparator.comparing(v -> v.distance))
                    .forEach(queue::addLast);
            next.settled = true;

        if (vertices.stream().allMatch(v -> v.settled)) {
            edgesResult = vertices.stream()
                    .filter(v -> !v.equals(rootNode))
                    .sorted(Comparator.comparing(v -> v.id))
                    .map(v -> String.format("%s - %d", v.id, v.distance))
                    .collect(Collectors.joining(", "));
            return null;
        } else return queue.pollFirst();
    }
 */