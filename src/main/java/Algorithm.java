import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    protected LinkedList<Vertex> queue;
    protected StringJoiner chainResult;
    protected String edgesResult;
    private final Map<Vertex, List<Edge>> connects;

    public Algorithm(Map<Vertex, List<Edge>> connects) {
        this.connects = connects;
    }

    protected Vertex dfsAlgorithm(Vertex root) {
        root.visited = true;
        chainResult.add(root.id);
        queue.addLast(root);
        root.revalidate();
        root.repaint();
        while (true) {
            if (queue.isEmpty()) return null;
            var next = connects.get(queue.peekLast()).stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> edge.second);
            if (next.isPresent()) return next.get();
            else queue.pollLast();
        }
    }

    protected Vertex bfsAlgorithm(Vertex root) {
        root.visited = true;
        chainResult.add(root.id);
        queue.addLast(root);
        root.revalidate();
        root.repaint();
        while (true) {
            if (queue.isEmpty()) return null;
            var next = connects.get(queue.peekFirst()).stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .map(edge -> edge.second);
            if (next.isPresent()) return next.get();
            else queue.removeFirst();
        }
    }

    protected void dijkstraAlgorithm(Vertex root) {
        var settled = new HashSet<Vertex>();
        root.distance = 0;
        settled.add(root);
        root.revalidate();
        root.repaint();
        connects.get(root).stream()
                .sorted(Comparator.comparing(edge -> edge.weight))
                .peek(edge -> edge.second.distance = edge.weight)
                .map(edge -> edge.second)
                .forEach(queue::addLast);
        while (true) {
            connects.get(queue.peekFirst()).stream()
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
            if (connects.size() == settled.size()) {
                edgesResult = settled.stream()
                        .filter(v -> !v.equals(root))
                        .sorted(Comparator.comparing(v -> v.id))
                        .map(v -> String.format("%s=%d", v.id, v.distance))
                        .collect(Collectors.joining(", "));
                return;
            }
        }
    }

    protected void primAlgorithm(Vertex root) {
        var edges = new HashSet<Edge>(); // child-->parent edges
        root.visited = true;
        root.revalidate();
        root.repaint();
        connects.get(root).stream()
                .min(Comparator.comparingInt(edge -> edge.weight))
                .map(edge -> edge.second)
                .ifPresent(v -> {
                    v.visited = true;
                    connects.get(v).stream()
                            .filter(edge -> edge.second.equals(root))
                            .forEach(edges::add);
                });
        while (true) {
            connects.entrySet().stream()
                    .filter(entry -> !entry.getKey().visited)
                    .map(Map.Entry::getValue)
                    .flatMap(Collection::stream)
                    .filter(edge -> !edge.first.visited && edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight))
                    .ifPresent(edge -> {
                        edges.add(edge);
                        edge.first.visited = true;
                    });
            if (connects.keySet().stream().allMatch(v -> v.visited)) {
                edgesResult = edges.stream()
                        .sorted(Comparator.comparing(edge -> edge.first.id))
                        .map(edge -> String.format("%s=%s", edge.first.id, edge.second.id))
                        .collect(Collectors.joining(", "));
                return;
            }
        }
    }
}