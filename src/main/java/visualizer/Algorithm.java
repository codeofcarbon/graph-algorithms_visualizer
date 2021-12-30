package visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {
    private LinkedList<Vertex> queue;
    private Set<Edge> edgeSet;
    private final Graph graph;
    private StringJoiner chainResult;
    private String edgesResult;
    static Map<Vertex, List<Edge>> paths;
    static List<Edge> pathResult;
    static Vertex root;

    public Algorithm(Graph graph) {
        pathResult = new LinkedList<>();
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
        if (graph.vertices.stream().allMatch(v -> v.visited)) {
            paths.values().stream()
                    .flatMap(Collection::stream)
//                    .peek(edge -> List.of(edge, edge.mirrorEdge).forEach(e -> e.visited = true))
                    .forEach(edgeSet::add);
        }
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

    protected String getResultIfReady() {
        var algorithmResult = "";
        if (graph.vertices.stream().allMatch(v -> v.visited)) {
            showResultEdges();
            switch (graph.algorithmMode) {
                case DEPTH_FIRST_SEARCH:
                    algorithmResult = "<html><font size=+1 color=gray><i>DFS for </i></font>" +
                                      "<font size=+2 color=#0062ff><b>" + root.id +
                                      ":   </b></font>" + chainResult.toString();
                    break;
                case BREADTH_FIRST_SEARCH:
                    algorithmResult = "<html><font size=+1 color=gray><i>BFS for </i></font>" +
                                      "<font size=+2 color=#0062ff><b>" + root.id +
                                      ":   </b></font>" + chainResult.toString();
                    break;
                case DIJKSTRA_ALGORITHM:
                    edgesResult = graph.vertices.stream()
                            .filter(vertex -> !vertex.equals(root))
                            .sorted(Comparator.comparing(v -> v.id))
                            .map(vertex -> String.format(
                                    "<html><font size=+1><b> %s -<font color=#a0b7db><i> %d</i></b></font>",
                                    vertex.id, vertex.distance))
                            .collect(Collectors.joining(","));

                    algorithmResult = "<html><font size=+1 color=gray><i>shortest paths from </i></font>" +
                                      "<font size=+2 color=#0062ff><b>" + root.id +
                                      ":   </b></font>" + edgesResult;
                    graph.setToolTipText("<html><font size=200%><b>click on node to get it shortest path</b>");
                    break;
                case PRIM_ALGORITHM:
                    edgesResult = edgeSet.stream()
                            .sorted(Comparator.comparing(edge -> edge.source.id))
                            .map(edge -> String.format(
                                    "<html><font size=+1><b> %s - %s</b></font>",
                                    edge.source.id, edge.target.id))
                            .collect(Collectors.joining(","));

                    algorithmResult = "<html><font size=+1 color=gray><i>minimum spanning tree for </i></font>" +
                                      "<font size=+2 color=#0062ff><b>" + root.id +
                                      ":   </b></font>" + edgesResult;
            }
        }
        return algorithmResult;
    }

    String getShortestPath(Vertex target) {
        edgeSet.clear();
        edgeSet.addAll(paths.get(target));
        showResultEdges();
        return "<html><font size=+1 color=gray><i>shortest paths from </i>" +
               "<font size=+2 color=#0062ff><b>" + root.id +
               "<font size=+1 color=gray><i> to </i>" +
               "<font size=+2 color=#0062ff><b>" + target.id +
               ":   </b></font>" + paths.get(target).stream()
                       .map(e -> String.format("<html><font color=white size=+1><b> %s - %s</b></font>",
                               e.source.id, e.target.id))
                       .collect(Collectors.joining("<html><font color=white size=+1><b>,</b></font>"));
    }

    private void showResultEdges() {
        graph.edges.forEach(edge -> {
            edge.hidden = true;
            if (edgeSet.contains(edge))
                List.of(edge, edge.mirrorEdge).forEach(e -> e.visited = true);
        });
    }

    protected void resetAlgorithm() {
        queue = new LinkedList<>();
        edgeSet = new HashSet<>();
        pathResult = new LinkedList<>();
        chainResult = new StringJoiner(" > ");
        edgesResult = "";
        root = null;
    }
}