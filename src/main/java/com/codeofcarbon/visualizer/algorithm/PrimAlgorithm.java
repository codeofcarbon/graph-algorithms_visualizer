package com.codeofcarbon.visualizer.algorithm;//package com.codeofcarbon.visualizer.algorithms;
//
//import com.codeofcarbon.visualizer.GraphService;
//import com.codeofcarbon.visualizer.view.Edge;
//
//import java.util.Comparator;
//import java.util.stream.Collectors;
//
//public class PrimAlgorithm extends Algorithm {
//
//    @Override
//    public void execute(GraphService service) {
//        service.getNodes().stream()
//                .flatMap(node -> node.getConnectedEdges().stream())
//                .filter(edge -> edge.getSource().visited && !edge.getTarget().visited)
//                .min(Comparator.comparingInt(Edge::getWeight))
//                .ifPresent(edge -> {
//                    edge.visited = true;
//                    edge.getMirrorEdge().visited = true;
//                    edge.getTarget().visited = true;
//                    edgeResult.add(edge);
//                });
//    }
//
//    @Override
//    public String getResult(GraphService service) {
//        return "<html><font color=gray>minimum spanning tree: " + edgeResult.stream()
//                .map(edge -> String.format("<font color=#0062ff><b> %s &#8644 %s</b>",
//                        edge.getSource().getId(), edge.getTarget().getId()))
//                .collect(Collectors.joining("<font color=gray>,"));
//    }
//}
