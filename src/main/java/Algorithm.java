import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    protected StringJoiner nodesResult = new StringJoiner(" -> ");
    protected StringJoiner edgesResult = new StringJoiner(" -> ");
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

    protected LinkedHashSet<Vertex> bfsAlgorithm(LinkedHashSet<Vertex> rootNodes) {
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
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (!neighbours.isEmpty()) return neighbours;
            else queue.removeFirst();
        }
    }
}
