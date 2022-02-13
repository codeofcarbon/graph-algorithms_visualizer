package visualizer;

import javax.swing.*;
import java.awt.*;

public class Visualizer extends JFrame {

    public Visualizer() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException
                | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(2000);

        var graph = new Graph();
        add(graph, BorderLayout.CENTER);
        add(graph.getToolbar(), BorderLayout.NORTH);
        setJMenuBar(new MenuBar(graph.getToolbar()));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}