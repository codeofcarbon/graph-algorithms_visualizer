package visualizer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException
                | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        final File graphDataDirectory = new File("src/main/java/visualizer/data/");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(graphDataDirectory);
        add(fileChooser);

        Toolbar toolbar = new Toolbar(fileChooser);
        add(toolbar, BorderLayout.NORTH);

        JLabel displayLabel = new JLabel("", SwingConstants.CENTER);
        displayLabel.setPreferredSize(new Dimension(this.getWidth(), 50));
        displayLabel.setBackground(Color.BLACK);
        displayLabel.setForeground(Color.WHITE);
        displayLabel.setOpaque(true);
        add(displayLabel, BorderLayout.SOUTH, SwingConstants.CENTER);

        Graph graph = new Graph(toolbar, displayLabel);
        add(graph);

        MenuBar menuBar = new MenuBar(graph.service);
        setJMenuBar(menuBar);
    }
}