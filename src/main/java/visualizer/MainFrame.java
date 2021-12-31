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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/main/java/visualizer/data/"));
        add(fileChooser);

//        JPanel infoPanel = new JPanel();
//        add(infoPanel, BorderLayout.NORTH);

        JLabel displayLabel = new JLabel("", SwingConstants.CENTER);
//        displayLabel.setPreferredSize(new Dimension(this.getWidth(), 30));
        displayLabel.setBackground(new Color(12, 12, 12, 255));
        displayLabel.setForeground(Color.WHITE);
        displayLabel.setOpaque(true);
//        infoPanel.add(displayLabel, BorderLayout.SOUTH, SwingConstants.CENTER);
//        add(displayLabel, BorderLayout.SOUTH);

        Toolbar toolbar = new Toolbar(fileChooser, displayLabel);
//        infoPanel.add(toolbar, BorderLayout.NORTH);
        add(toolbar, BorderLayout.NORTH);

        Graph graph = new Graph(displayLabel, toolbar);
        add(graph);

        MenuBar menuBar = new MenuBar(graph.service);
        setJMenuBar(menuBar);
    }
}