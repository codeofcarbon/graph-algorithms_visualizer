package com.codeofcarbon.visualizer;

import com.codeofcarbon.visualizer.view.*;

import java.util.*;
import java.util.stream.*;

public class Algorithm {
    private static final LinkedList<Node> queue = new LinkedList<>();
    private static final LinkedList<Node> nodeResult = new LinkedList<>();
    private static final LinkedList<Edge> edgeResult = new LinkedList<>();
    private static final Map<Node, List<Edge>> paths = new HashMap<>();
    private static String algorithmResult;
    private final GraphService service;
    public static Node root, target;

    public Algorithm(GraphService service) {
        this.service = service;
    }

    protected void dfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekLast().getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .min(Comparator.comparingInt(Edge::getWeight))
                    .ifPresentOrElse(edge -> {
                        Stream.of(edge, edge.getMirrorEdge())
                                .peek(e -> e.visited = true)
                                .forEach(edgeResult::add);
                        edge.getTarget().visited = true;
                        queue.addLast(edge.getTarget());
                        nodeResult.add(edge.getTarget());
                    }, queue::pollLast);
        }
    }

    protected void bfsAlgorithm() {
        if (!queue.isEmpty()) {
            queue.peekFirst().getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .min(Comparator.comparingInt(Edge::getWeight))
                    .ifPresentOrElse(edge -> {
                        Stream.of(edge, edge.getMirrorEdge())
                                .peek(e -> e.visited = true)
                                .forEach(edgeResult::add);
                        edge.getTarget().visited = true;
                        queue.addLast(edge.getTarget());
                        nodeResult.add(edge.getTarget());
                    }, queue::pollFirst);
        }
    }

    protected void dijkstraAlgorithm() {
        if (!queue.isEmpty()) {
            var current = queue.pollFirst();
            current.getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .forEach(edge -> {
                        if (edge.getTarget().distance > edge.getSource().distance + edge.getWeight()) {
                            edge.getTarget().distance = edge.getSource().distance + edge.getWeight();
                            paths.get(edge.getTarget()).clear();
                            paths.get(edge.getTarget()).addAll(paths.get(current));
                            paths.get(edge.getTarget()).add(edge);
                        }
                    });
            current.visited = true;
            queue.sort(Comparator.comparingInt(Node::getDistance));
        }
        if (service.getNodes().stream().allMatch(Node::isVisited)) {
            paths.values().forEach(edgeResult::addAll);
        }
    }

    protected void primAlgorithm() {
        service.getNodes().stream()
                .flatMap(node -> node.getConnectedEdges().stream())
                .filter(edge -> edge.getSource().visited && !edge.getTarget().visited)
                .min(Comparator.comparingInt(Edge::getWeight))
                .ifPresent(edge -> {
                    edge.visited = true;
                    edge.getMirrorEdge().visited = true;
                    edge.getTarget().visited = true;
                    edgeResult.add(edge);
                });
    }

    protected void bellmanFordAlgorithm() {
//        // Bellman Ford's routine, basically = relax all E edges V-1 times
//        ArrayList<Integer> dist = new ArrayList<>(Collections.nCopies(service.getNodes().size(), Integer.MAX_VALUE));
//        ArrayList<Integer> dist = new ArrayList<>(Collections.nCopies(service.getNodes().size(), Integer.MAX_VALUE));
//        dist.set(s, 0);
//
//        for (int i = 0; i < service.getNodes().size() - 1; ++i) {               // total O(V*E)
//            boolean modified = false;                                           // optimization
//            for (int u = 0; u < service.getNodes().size(); ++u)                 // these two loops = O(E)
//                if (dist.get(u) != Integer.MAX_VALUE)                           // important check
//                    for (IntegerPair v_w : paths.get(u)) {
//                        int v = v_w.first();
//                        int w = v_w.second();
//                        if (dist.get(u) + w >= dist.get(v)) continue;           // not improving, skip
//                        dist.set(v, dist.get(u) + w);                           // relax operation
//                        modified = true;                                        // optimization
//                    }
//            if (!modified) break;
//        }
//
//        boolean hasNegativeCycle = false;
//        for (int u = 0; u < service.getNodes().size(); ++u)                     // one more pass to check
//            if (dist.get(u) != Integer.MAX_VALUE)
//                for (IntegerPair v_w : paths.get(u)) {
//                    int v = v_w.first();
//                    int w = v_w.second();
//                    if (dist.get(v) > dist.get(u) + w)                          // should be false
//                        hasNegativeCycle = true;                                // if true => -ve cycle
//                }
//        System.out.printf("Negative Cycle Exist? %s\n", hasNegativeCycle ? "Yes" : "No");
//
//        if (!hasNegativeCycle)
//            for (int u = 0; u < service.getNodes().size(); ++u)
//                System.out.printf("SSsp(%d, %d) = %d\n", s, u, dist.get(u));
    }

    protected void initAlgorithm(Node rootNode) {
        resetAlgorithmData();
        service.resetComponentsLists();
        root = rootNode;
        rootNode.visited = true;
        switch (service.getAlgorithmMode()) {
            case DEPTH_FIRST_SEARCH, BREADTH_FIRST_SEARCH -> {
                queue.addLast(rootNode);
                nodeResult.add(rootNode);
            }
            case BELLMAN_FORD_ALGORITHM, DIJKSTRA_ALGORITHM, PRIM_ALGORITHM -> {
                queue.addAll(service.getNodes());
                rootNode.distance = 0;
                service.getNodes().forEach(node -> paths.put(node, new ArrayList<>()));
                root.getConnectedEdges().forEach(edge -> {
                    edge.getTarget().distance = edge.getWeight();
                    paths.get(edge.getTarget()).add(edge);
                });
                queue.sort(Comparator.comparingInt(Node::getDistance));
            }
        }
    }

    protected String getResultIfReady() {
        if (service.getNodes().stream().allMatch(Node::isVisited)) {
            service.getNodes().stream()
                    .flatMap(node -> node.getConnectedEdges().stream())
                    .forEach(edge -> edge.hidden = true);
            edgeResult.forEach(edge -> edge.visited = true);
            switch (service.getAlgorithmMode()) {
                case DEPTH_FIRST_SEARCH, BREADTH_FIRST_SEARCH -> algorithmResult =
                        String.format("<html><font color=gray>%s for node " +
                                      "<font size=+1 color=#5afa46><b>" + root.getId() + "</b></font>" +
                                      "<font color=gray>: ",
                                service.getAlgorithmMode() == AlgMode.DEPTH_FIRST_SEARCH ? "DFS" : "BFS") +
                        nodeResult.stream()
                                .map(node -> "<font color=#0062ff><b>" + node.getId() + "</b>")
                                .collect(Collectors.joining(" &rarr "));
                case DIJKSTRA_ALGORITHM -> {
                    var joinedResult = service.getNodes().stream()
                            .filter(node -> !node.equals(root))
                            .sorted(Comparator.comparing(Node::getId))
                            .map(node -> "<font color=#0062ff> " + node.getId() + "<font color=red> &#8680 " +
                                         (node.distance == Integer.MAX_VALUE ? "inf" : node.distance))
                            .collect(Collectors.joining("<font color=gray>,"));
                    algorithmResult = "<html><font color=gray>shortest distances from node " +
                                      "<font size=+1 color=#5afa46><b>" + root.getId() + "</b></font>" +
                                      "<font color=gray>: " + joinedResult;

                    TipManager.setToolTipText(service.getGraph(),
                            "<html><font color=rgb(128,128,128)>click on a node to see the shortest path");
                    service.getNodes().forEach(node -> TipManager.setToolTipText(node,
                            "<html><font color=rgb(128,128,128)>click to see the shortest path"));
                }
                case PRIM_ALGORITHM -> algorithmResult =
                        "<html><font color=gray>minimum spanning tree: " + edgeResult.stream()
                                .map(edge -> String.format("<font color=#0062ff><b> %s &#8644 %s</b>",
                                        edge.getSource().getId(), edge.getTarget().getId()))
                                .collect(Collectors.joining("<font color=gray>,"));
            }
        }
        return algorithmResult;
    }

    protected String getShortestPath(Node targetNode) {
        edgeResult.forEach(edge -> edge.path = false);
        paths.keySet().forEach(node -> node.path = false);
        var resultJoiner = new StringJoiner("<font color=gray> &rarr ");

        if ((target = targetNode).equals(root)) {
            target.path = false;
            edgeResult.forEach(edge -> edge.visited = true);
            return algorithmResult;
        }

        paths.get(target).forEach(edge -> {
            edge.path = true;
            edge.getSource().path = edge.getSource() != root;
            edge.getTarget().path = edge.getTarget() != root;
            resultJoiner.add(String.format("<font color=0062ff> %s &#8644 %s",
                    edge.getSource().getId(), edge.getTarget().getId()));
        });

        return "<html><font color=gray>shortest path from " +
               "<b><font size=+1 color=#5afa46>" + root.getId() + "</b><font color=gray> to " +
               "<b><font size=+1 color=#5afa46>" + target.getId() + "</b><font color=gray>: " +
               resultJoiner + "<font size=+1 color=red>   &#8680 total distance: " +
               (target.distance == Integer.MAX_VALUE ? "inf" : target.distance);
    }

    protected void resetAlgorithmData() {
        List.of(queue, edgeResult, nodeResult).forEach(Collection::clear);
        paths.clear();
        algorithmResult = "";
        target = null;
        root = null;
    }

    boolean checkIfGraphIsConnected(Node rootNode) {
        queue.addLast(rootNode);
        while (!queue.isEmpty()) {
            queue.peekLast().visited = true;
            queue.peekLast().getConnectedEdges().stream()
                    .filter(edge -> !edge.getTarget().visited)
                    .findAny().ifPresentOrElse(edge -> queue.addLast(edge.getTarget()), queue::pollLast);
        }
        return service.getNodes().stream().allMatch(Node::isVisited);
    }
}