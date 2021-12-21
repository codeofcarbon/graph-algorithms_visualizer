import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        var modeLabel = new ModeLabel();
        add(modeLabel, BorderLayout.NORTH);
        var displayLabel = new DisplayLabel();
        add(displayLabel, BorderLayout.SOUTH);
        var graph = new Graph(modeLabel, displayLabel);
        add(graph);
        setJMenuBar(new MenuBar(graph.service));
        setVisible(true);
    }
}
