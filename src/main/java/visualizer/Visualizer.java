package visualizer;

import javax.swing.*;
import java.awt.*;

public class Visualizer extends JFrame {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(Visualizer::new);
    }

    public Visualizer() {
        super("Graph-Algorithms Visualizer");
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setReshowDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException
                | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        var graph = new Graph();
        add(graph, BorderLayout.CENTER);
        add(graph.getToolbar(), BorderLayout.NORTH);
        setJMenuBar(new MenuBar(graph.getToolbar(), this));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}