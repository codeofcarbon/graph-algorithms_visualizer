package visualizer;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JLabel modeLabel = addLabel("Current Mode -> Add a Vertex  ", SwingConstants.RIGHT, "Mode");
        modeLabel.setBounds(580, 0, 200, 30);
        add(modeLabel, BorderLayout.NORTH);

        JLabel displayLabel = addLabel("", SwingConstants.CENTER, "Display");
        displayLabel.setPreferredSize(new Dimension(100, 30));
        add(displayLabel, BorderLayout.SOUTH, SwingConstants.CENTER);

        Graph graph = new Graph(modeLabel, displayLabel);
        add(graph);

        setJMenuBar(new MenuBar(graph.service));
    }

    private JLabel addLabel(String text, int align, String name) {
        var label = new JLabel(text, align);
        label.setName(name);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        return label;
    }
}