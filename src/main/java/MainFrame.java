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
        JLabel modeLabel = new JLabel("Current Mode -> Add a Vertex", SwingConstants.RIGHT);
        modeLabel.setName("Mode");
        modeLabel.setBounds(580, 0, 200, 30);
        modeLabel.setBackground(Color.BLACK);
        modeLabel.setForeground(Color.WHITE);
        modeLabel.setOpaque(true);
        JLabel displayLabel = new JLabel("", SwingConstants.CENTER);
        displayLabel.setName("Display");
        displayLabel.setBounds(0, 550, 800, 30);
        displayLabel.setForeground(Color.BLACK);
        add(modeLabel, BorderLayout.NORTH);
        add(displayLabel, BorderLayout.SOUTH);
        var graph = new Graph(modeLabel, displayLabel);
        add(graph);
        setJMenuBar(new MenuBar(graph.service));
    }
}
