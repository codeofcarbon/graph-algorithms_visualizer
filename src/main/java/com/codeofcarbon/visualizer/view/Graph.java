package com.codeofcarbon.visualizer.view;

import com.codeofcarbon.visualizer.*;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class Graph extends JPanel {
    private static final Image backImage = IconMaker.loadBackgroundImage();
    private final GraphService service;
    @Getter
    private final Toolbar toolbar;

    public Graph() {
        setName("Graph");
        setPreferredSize(new Dimension(1100, 600));
        setSize(getPreferredSize());
        setOpaque(true);
        setLayout(null);
        service = new GraphService(this);
        toolbar = new Toolbar(service);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(backImage, 0, 0, Color.BLACK, null);
        g2D.setStroke(new BasicStroke(1f));
        service.getNodes().stream()
                .flatMap(node -> node.getConnectedEdges().stream())
                .forEach(edge -> edge.getState().draw(g2D, edge));
        if (service.getNodes().stream().anyMatch(node -> node.getY() < 0)) {
            g2D.setColor(new Color(120, 120, 120, 50));
            g2D.drawLine(0, 0, getWidth(), 0);
        }
    }
}