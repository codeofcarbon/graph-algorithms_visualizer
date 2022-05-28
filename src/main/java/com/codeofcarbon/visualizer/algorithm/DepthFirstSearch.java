package com.codeofcarbon.visualizer.algorithm;//package com.codeofcarbon.visualizer.algorithms;
//
//import com.codeofcarbon.visualizer.GraphService;
//import com.codeofcarbon.visualizer.view.Edge;
//
//import java.util.Comparator;
//import java.util.stream.*;
//
//public class DepthFirstSearch extends Algorithm {
//
//    @Override
//    public void execute(GraphService service) {
//        if (!queue.isEmpty()) {
//            queue.removeLast().getConnectedEdges().stream()
//                    .filter(edge -> !edge.getTarget().visited)
//                    .min(Comparator.comparingInt(Edge::getWeight))
//                    .ifPresentOrElse(edge -> {
//                        Stream.of(edge, edge.getMirrorEdge())
//                                .peek(e -> e.visited = true)
//                                .forEach(edgeResult::add);
//                        edge.getTarget().visited = true;
//                        queue.addLast(edge.getTarget());
//                        nodeResult.add(edge.getTarget());
//                    }, queue::pollLast);
//        }
//    }
//
//    @Override
//    public String getResult(GraphService service) {
//        return "<html><font color=gray>DFS for node " +
//               "<font size=+1 color=#5afa46><b>" + root.getId() + "</b></font>" +
//               "<font color=gray>: " + nodeResult.stream()
//                       .map(node -> "<font color=#0062ff><b>" + node.getId() + "</b>")
//                       .collect(Collectors.joining(" &rarr "));
//    }
//}
