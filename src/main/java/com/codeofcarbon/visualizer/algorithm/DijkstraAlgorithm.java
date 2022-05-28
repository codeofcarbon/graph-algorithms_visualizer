package com.codeofcarbon.visualizer.algorithm;//package com.codeofcarbon.visualizer.algorithms;
//
//import com.codeofcarbon.visualizer.GraphService;
//import com.codeofcarbon.visualizer.view.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class DijkstraAlgorithm extends Algorithm {
//
//    @Override
//    public void execute(GraphService service) {
//        if (!queue.isEmpty()) {
//            var current = queue.removeFirst();
//            current.getConnectedEdges().stream()
//                    .filter(edge -> !edge.getTarget().visited)
//                    .forEach(edge -> {
//                        if (edge.getTarget().distance > edge.getSource().distance + edge.getWeight()) {
//                            edge.getTarget().distance = edge.getSource().distance + edge.getWeight();
//                            paths.get(edge.getTarget()).clear();
//                            paths.get(edge.getTarget()).addAll(paths.get(current));
//                            paths.get(edge.getTarget()).add(edge);
//                        }
//                    });
//            current.visited = true;
//            queue.sort(Comparator.comparingInt(Node::getDistance));
//        }
//        if (service.getNodes().stream().allMatch(Node::isVisited)) {
//            paths.values().forEach(edgeResult::addAll);
//        }
//    }
//
//    @Override
//    public String getResult(GraphService service) {
//        var joinedResult = service.getNodes().stream()
//                .filter(node -> !node.equals(root))
//                .sorted(Comparator.comparing(Node::getId))
//                .map(node -> "<font color=#0062ff> " + node.getId() + "<font color=red> &#8680 " +
//                             (node.distance == Integer.MAX_VALUE ? "inf" : node.distance))
//                .collect(Collectors.joining("<font color=gray>,"));
//
//        TipManager.setToolTipText(service.getGraph(),
//                "<html><font color=rgb(128,128,128)>click on a node to see the shortest path");
//        service.getNodes().forEach(node -> TipManager.setToolTipText(node,
//                "<html><font color=rgb(128,128,128)>click to see the shortest path"));
//
//        return "<html><font color=gray>shortest distances from node " +
//               "<font size=+1 color=#5afa46><b>" + root.getId() + "</b></font>" +
//               "<font color=gray>: " + joinedResult;
//    }
//
//    @Override
//    public String getShortestPath(Node targetNode) {
//        edgeResult.forEach(edge -> edge.path = false);
//        paths.keySet().forEach(node -> node.path = false);
//        var resultJoiner = new StringJoiner("<font color=gray> &rarr ");
//
//        if ((target = targetNode).equals(root)) {
//            target.path = false;
//            edgeResult.forEach(edge -> edge.visited = true);
//            return algorithmResult;
//        }
//
//        paths.get(target).forEach(edge -> {
//            edge.path = true;
//            edge.getSource().path = edge.getSource() != root;
//            edge.getTarget().path = edge.getTarget() != root;
//            resultJoiner.add(String.format("<font color=0062ff> %s &#8644 %s",
//                    edge.getSource().getId(), edge.getTarget().getId()));
//        });
//
//        return "<html><font color=gray>shortest path from " +
//               "<b><font size=+1 color=#5afa46>" + root.getId() + "</b><font color=gray> to " +
//               "<b><font size=+1 color=#5afa46>" + target.getId() + "</b><font color=gray>: " +
//               resultJoiner + "<font size=+1 color=red>   &#8680 total distance: " +
//               (target.distance == Integer.MAX_VALUE ? "inf" : target.distance);
//    }
//}