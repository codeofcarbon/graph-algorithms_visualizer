package visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Graph extends JPanel {
    private static final Image backImage = new ImageIcon("src/main/resources/icons/special/background.png").getImage();
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
    public JToolTip createToolTip() {
        return new ToolTipDealer();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        return ToolTipDealer.getFixedToolTipLocation(e, this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(backImage, 0, 0, Color.BLACK, null);
        g2D.setStroke(new BasicStroke(2f));
        service.getNodes().stream()
                .flatMap(node -> node.getConnectedEdges().stream())
                .forEach(edge -> edge.getState().draw(g2D, edge));
        service.getNodes()
                .forEach(node -> node.getState().draw(g2D, node));
        if (service.getNodes().stream().anyMatch(node -> node.getY() < 0)) {
            g2D.setColor(Color.DARK_GRAY.darker());
            g2D.drawLine(0, 0, getWidth(), 0);
        }
    }
}