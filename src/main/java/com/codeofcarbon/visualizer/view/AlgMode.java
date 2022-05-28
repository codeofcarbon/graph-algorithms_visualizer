package com.codeofcarbon.visualizer.view;

public enum AlgMode {
    DEPTH_FIRST_SEARCH("Depth-First Search"),
    BREADTH_FIRST_SEARCH("Breadth-First Search"),
    DIJKSTRA_ALGORITHM("Dijkstra's Algorithm"),
    PRIM_ALGORITHM("Prim's Algorithm"),
    BELLMAN_FORD_ALGORITHM("Bellman-Ford Algorithm"),
    NONE("None");

    public final String current;

    AlgMode(String current) {
        this.current = current;
    }
}
