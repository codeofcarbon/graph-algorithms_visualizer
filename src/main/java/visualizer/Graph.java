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
        Graphics2D g2D = (Graphics2D) g;
        g2D.setStroke(new BasicStroke(2f));
        service.getNodes().stream()
                .flatMap(node -> node.connectedEdges.stream())
                .forEach(edge -> edge.getState().draw(g, g2D, edge));
        service.getNodes()
                .forEach(node -> node.getState().draw(g2D, node));
        if (service.getNodes().stream().anyMatch(node -> node.getY() < 0)) {
            g2D.setColor(Color.DARK_GRAY.darker());
            g2D.drawLine(0, 0, getWidth(), 0);
        }
    }
}