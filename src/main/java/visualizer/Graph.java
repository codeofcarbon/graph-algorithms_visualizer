package visualizer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

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
        g2d.setStroke(new BasicStroke(3f));
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

    @Override
    public JToolTip createToolTip() {                                                   // todo fix that tool tip
        var tip = new JToolTip();
        tip.setBackground(new Color(0, 0, 0, 0));
        tip.setBorder(null);
        tip.setOpaque(false);
        return tip;
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        var point = e.getPoint();
        point.translate(-50, 20);
        return point;
    }
}