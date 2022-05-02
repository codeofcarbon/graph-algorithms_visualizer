package com.codeofcarbon.visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Getter
public class ButtonPanel extends JPanel {
    private final JComboBox<String> algModeComboBox, graphModeComboBox;
    private final MenuButton algModeButton, graphModeButton, toolsButton;
    private final ButtonGroup buttonGroup;

    public ButtonPanel(GraphService service, JPanel toolsPanel) {
        setLayout(new GridLayout(1, 0, 0, 0));
        setPreferredSize(new Dimension(200, 70));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setOpaque(false);
        graphModeComboBox = new ModeList<>(GraphMode.values(), service, SwingConstants.RIGHT);
        algModeComboBox = new ModeList<>(AlgMode.values(), service, SwingConstants.LEFT);
        graphModeButton = new MenuButton("graph", "GRAPH MODE", this, graphModeComboBox);
        toolsButton = new MenuButton("menu", "TOOLS", this, toolsPanel);
        algModeButton = new MenuButton("algorithm", "ALGORITHM MODE", this, algModeComboBox);

        buttonGroup = new ButtonGroup();
        List.of(graphModeButton, toolsButton, algModeButton).forEach(buttonGroup::add);
    }
}