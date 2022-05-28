package com.codeofcarbon.visualizer.algorithm;//package com.codeofcarbon.visualizer.algorithms;
//
//import com.codeofcarbon.visualizer.GraphService;
//import com.codeofcarbon.visualizer.view.*;
//
//import java.util.*;
//import java.util.List;
//
//public abstract class Algorithm {
//    protected static final LinkedList<Node> queue = new LinkedList<>();
//    protected static final LinkedList<Node> nodeResult = new LinkedList<>();
//    protected static final LinkedList<Edge> edgeResult = new LinkedList<>();
//    protected static final Map<Node, List<Edge>> paths = new HashMap<>();
//    public static String algorithmResult;
//    public static Node root, target;
//
//    public abstract void execute(GraphService service);
//
//    public abstract String getResult(GraphService service);
//
//    public String getShortestPath(Node targetNode) {
//        return "";
//    }
//
//    public static Algorithm getCurrentAlgorithm(AlgMode algMode) {
//        return algMode == AlgMode.DEPTH_FIRST_SEARCH ? new DepthFirstSearch() :
//                algMode == AlgMode.BREADTH_FIRST_SEARCH ? new BreadthFirstSearch() :
//                algMode == AlgMode.DIJKSTRA_ALGORITHM ? new DijkstraAlgorithm() :
//                algMode == AlgMode.PRIM_ALGORITHM ? new PrimAlgorithm() : new BellmanFordAlgorithm();
////        switch (algMode) {
////            case DEPTH_FIRST_SEARCH -> new DepthFirstSearch();
////            case BREADTH_FIRST_SEARCH -> new BreadthFirstSearch();
////            case DIJKSTRA_ALGORITHM -> new DijkstraAlgorithm();
////
////            case PRIM_ALGORITHM -> new PrimAlgorithm();
////            case BELLMAN_FORD_ALGORITHM -> new BellmanFordAlgorithm();
////            case NONE -> null;
////            default -> throw new IllegalStateException("Unexpected value: " + algMode);
////        };
//    }
//
//    public static void initAlgorithm(Node rootNode, GraphService service) {
//        resetAlgorithmData();
//        service.resetComponentsLists();
//        root = rootNode;
//        rootNode.visited = true;
//        switch (service.getAlgorithmMode()) {
//            case DEPTH_FIRST_SEARCH, BREADTH_FIRST_SEARCH -> {
//                queue.addLast(rootNode);
//                nodeResult.add(rootNode);
//            }
//            case BELLMAN_FORD_ALGORITHM, DIJKSTRA_ALGORITHM, PRIM_ALGORITHM -> {
//                queue.addAll(service.getNodes());
//                rootNode.distance = 0;
//                service.getNodes().forEach(node -> paths.put(node, new ArrayList<>()));
//                root.getConnectedEdges().forEach(edge -> {
//                    edge.getTarget().distance = edge.getWeight();
//                    paths.get(edge.getTarget()).add(edge);
//                });
//                queue.sort(Comparator.comparingInt(Node::getDistance));
//            }
//        }
//    }
//
//    public String getResultIfReady(GraphService service) {
//        if (service.getNodes().stream().allMatch(Node::isVisited)) {
//            service.getNodes().stream()
//                    .flatMap(node -> node.getConnectedEdges().stream())
//                    .forEach(edge -> edge.hidden = true);
//            edgeResult.forEach(edge -> edge.visited = true);
//            algorithmResult = getResult(service);
//        }
//        return algorithmResult;
//    }
//
//    public static void resetAlgorithmData() {
//        List.of(queue, edgeResult, nodeResult).forEach(Collection::clear);
//        paths.clear();
//        algorithmResult = "";
//        target = null;
//        root = null;
//    }
//
//    public static boolean checkIfGraphIsConnected(Node rootNode, GraphService service) {
//        queue.addLast(rootNode);
//        while (!queue.isEmpty()) {
//            queue.peekLast().visited = true;
//            queue.peekLast().getConnectedEdges().stream()
//                    .filter(edge -> !edge.getTarget().visited)
//                    .findAny().ifPresentOrElse(edge -> queue.addLast(edge.getTarget()), queue::pollLast);
//        }
//        return service.getNodes().stream().allMatch(Node::isVisited);
//    }
//}
//
