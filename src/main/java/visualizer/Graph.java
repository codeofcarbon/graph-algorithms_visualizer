package visualizer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Graph extends JPanel {
    private GraphService service;

    public Graph() {
        setName("Graph");
        setPreferredSize(new Dimension(960, 600));
        setSize(getPreferredSize());
//        setBackground(new Color(0, 0, 0, 0));
        setBackground(Color.BLACK);
        createFrame();

        setLayout(null);
    }

    private void createFrame() {
        JFrame mainFrame = new JFrame("Graph-Algorithms Visualizer");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1000, 700));
        mainFrame.setSize(mainFrame.getPreferredSize());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException
                | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        mainFrame.setLocationRelativeTo(null);

        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(2000);
        JFileChooser fileChooser = new JFileChooser(new File("src/main/java/visualizer/data"));
        Toolbar toolbar = new Toolbar(fileChooser);
        MenuBar menuBar = new MenuBar(toolbar);
        this.service = new GraphService(this, toolbar);

        mainFrame.add(fileChooser);
        mainFrame.add(toolbar, BorderLayout.NORTH);
        mainFrame.setJMenuBar(menuBar);
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(3f));
        for (var e : service.getEdges()) {
            e.getState().coloring(g, g2d, e);
            g.setFont(new Font("Courier", Font.PLAIN, 20));
            boolean align = Math.abs(e.source.getX() - e.target.getX()) > Math.abs(e.source.getY() - e.target.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.source.getX() + e.source.radius + e.target.getX() + e.target.radius) / 2 + (align ? -10 : 10),
                    (e.source.getY() + e.source.radius + e.target.getY() + e.target.radius) / 2 + (align ? 25 : 10));
        }

        g2d.setStroke(new BasicStroke(1f));
        for (var v : service.getNodes()) {
            v.getState().coloring(g, v);
            g.setFont(new Font("Courier", Font.ITALIC, 30));
            g.drawString(v.id, v.getX() + 16, v.getY() + 36);
        }
    }
}