import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        var modeLabel = new ModeLabel();
        add(modeLabel);
        var graph = new Graph(modeLabel);
        add(graph);
        setJMenuBar(new MenuBar(graph));
        setVisible(true);
    }
}