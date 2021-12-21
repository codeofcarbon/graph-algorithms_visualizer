import javax.swing.*;
import java.awt.*;
import java.util.stream.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    void initComponents() {
        var graph = new JPanel();
        graph.setName("Graph");
        graph.setBackground(Color.BLACK);
        graph.setLayout(null);
        IntStream.rangeClosed(0, 3).mapToObj(Vertex::new).forEach(graph::add);
        add(graph);
    }
}