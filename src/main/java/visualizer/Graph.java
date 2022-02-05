package visualizer;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;

public class Graph extends JPanel {
    private static final Image backImage = new ImageIcon("src/main/resources/icons/special/background.png").getImage();
    private GraphService service;

    public Graph() {
        setName("Graph");
        setPreferredSize(new Dimension(1000, 720));
        setSize(getPreferredSize());
        createFrame();
        setOpaque(true);
        setLayout(null);
    }

    private void createFrame() {
        JFrame mainFrame = new JFrame("Graph-Algorithms Visualizer");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1000, 720));
        mainFrame.setSize(mainFrame.getPreferredSize());
        mainFrame.setBackground(Color.BLACK);
        mainFrame.setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException
                | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(2000);
        UndoManager manager = new UndoManager();
        Toolbar toolbar = new Toolbar(manager);
        service = new GraphService(this, toolbar, manager);

        mainFrame.add(toolbar, BorderLayout.NORTH);
        mainFrame.setJMenuBar(new MenuBar(toolbar));
        mainFrame.add(this);

        mainFrame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backImage, 0, 0, Color.BLACK, null);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3f));
        service.getEdges().forEach(e -> e.getState().draw(g, g2d, e));
        service.getNodes().forEach(v -> v.getState().draw(g, g2d, v));
    }
}