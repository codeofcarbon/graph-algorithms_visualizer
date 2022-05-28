package com.codeofcarbon.visualizer.view;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum GraphMode {
    ADD_NODE("Add Node"),
    ADD_AN_EDGE("Add an Edge"),
    REMOVE_NODE("Remove Node"),
    REMOVE_AN_EDGE("Remove an Edge"),
    NONE("None");

    private final String current;
}