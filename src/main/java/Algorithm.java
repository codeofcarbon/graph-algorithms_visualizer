import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    protected StringJoiner nodesResult = new StringJoiner(" -> ");
    protected String dijkstraResult;
    protected LinkedList<Vertex> queue = new LinkedList<>();
    private final Map<Vertex, List<Edge>> connects;

    public Algorithm(Map<Vertex, List<Edge>> connects) {
        this.connects = connects;
    }

    protected Vertex dfsAlgorithm(Vertex root) {
        root.visited = true;
        nodesResult.add(root.id);
        queue.addLast(root);
        root.revalidate();
        root.repaint();
        while (true) {
            if (queue.isEmpty()) return null;
            var nextEdge = connects.get(queue.peekLast()).stream()
                    .filter(edge -> !edge.second.visited)
                    .min(Comparator.comparingInt(edge -> edge.weight));
            if (nextEdge.isPresent()) return nextEdge.get().second;
            else queue.pollLast();
        }
    }

    protected LinkedList<Vertex> bfsAlgorithm(LinkedList<Vertex> rootNodes) {
        rootNodes.stream()
                .peek(v -> nodesResult.add(v.id))
                .peek(JComponent::revalidate)
                .peek(JComponent::repaint)
                .peek(v -> queue.addLast(v))
                .forEach(v -> v.visited = true);
        while (true) {
            if (queue.isEmpty()) return null;
            var neighbours = connects.get(queue.peekFirst()).stream()
                    .filter(edge -> !edge.second.visited)
                    .sorted(Comparator.comparing(edge -> edge.weight))
                    .map(edge -> edge.second)
                    .collect(Collectors.toCollection(LinkedList::new));
            if (!neighbours.isEmpty()) return neighbours;
            else queue.removeFirst();
        }
    }

    protected void dijkstraAlgorithm(Vertex root) {
        var settled = new HashSet<Vertex>();
        var unsettled = new LinkedList<Vertex>();
        root.distance = 0;
        settled.add(root);
        root.revalidate();
        root.repaint();
        connects.get(root).stream()
                .sorted(Comparator.comparing(edge -> edge.weight))
                .peek(edge -> edge.second.distance = edge.weight)
                .map(edge -> edge.second)
                .forEach(unsettled::addLast);
        while (true) {
            connects.get(unsettled.peekFirst()).stream()
                    .filter(edge -> !settled.contains(edge.second))
                    .peek(edge -> {
                        if (edge.second.distance > edge.first.distance + edge.weight)
                            edge.second.distance = edge.first.distance + edge.weight;
                    })
                    .map(edge -> edge.second)
                    .sorted(Comparator.comparing(v -> v.distance))
                    .filter(v -> !v.equals(unsettled.peekFirst()) && !unsettled.contains(v))
                    .forEach(unsettled::addLast);
            settled.add(unsettled.pollFirst());
            if (connects.size() == settled.size()) {
                dijkstraResult = settled.stream()
                        .filter(v -> !v.equals(root))
                        .sorted(Comparator.comparing(v -> v.id))
                        .map(v -> String.format("%s=%d", v.id, v.distance))
                        .collect(Collectors.joining(", "));
                return;
            }
        }
    }
}
