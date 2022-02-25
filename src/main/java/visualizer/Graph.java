package visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class Graph extends JPanel {
    private static final Image backImage = new ImageIcon("src/main/resources/icons/special/background.png").getImage();
    private final GraphService service;
    private final Toolbar toolbar;

    public Graph() {
        setName("Graph");
        setPreferredSize(new Dimension(1000, 600));
        setSize(getPreferredSize());
        setOpaque(true);
        setLayout(null);
        service = new GraphService(this);
        toolbar = new Toolbar(service);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backImage, 0, 0, Color.BLACK, null);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2f));
        service.getEdges()
                .forEach(e -> e.getState().draw(g, g2d, e));
        g2d.setStroke(new BasicStroke(0f));
        service.getNodes().stream()
                .peek(v -> {
                    if (v.getY() < 0) {
                        g2d.setColor(Color.DARK_GRAY.darker());
                        g2d.drawLine(0, 0, getWidth(), 0);
                    }
                }).forEach(v -> v.getState().draw(g, g2d, v));
    }
}