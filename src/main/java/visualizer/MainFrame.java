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

        Toolbar toolbar = new Toolbar(fileChooser);
        add(toolbar, BorderLayout.NORTH);

        Graph graph = new Graph(toolbar);
        add(graph);

        MenuBar menuBar = new MenuBar(graph.service);
        setJMenuBar(menuBar);
    }
}