package com.codeofcarbon.visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Infobar extends JLabel {
    private final Graph graph;

    public Infobar(Graph graph) {
        super("", SwingConstants.CENTER);
        this.graph = graph;
        setFont(new Font("Stylus BT", Font.PLAIN, 15));
        setBackground(new Color(0, 0, 0, 0));
        setForeground(new Color(204, 204, 204, 255));
        graph.add(this);
        graph.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateLocation();
            }
        });
    }

    void updateInfo(String info, String result) {
        updateLocation();
        setText(String.format("<html><div align='center'>%s",
                info.isBlank() ? result : "<font color=#cccccc>" + info));
    }

    public void updateLocation() {
        setSize(graph.getWidth(), 40);
        setLocation(0, graph.getHeight() - 50);
    }
}