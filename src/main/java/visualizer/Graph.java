package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Graph extends JPanel {
    private GraphService service;

    public Graph() {
        setName("Visualizer");
        setPreferredSize(new Dimension(960, 600));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        createFrame();
        addListeners();
        setLayout(null);
    }

    private void addListeners() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (service.getGraphMode() == GraphMode.ADD_A_VERTEX) service.createNewVertex(e);
                if (service.getGraphMode() == GraphMode.ADD_AN_EDGE) service.createNewEdge(e);
                if (service.getGraphMode() == GraphMode.REMOVE_A_VERTEX) service.removeVertex(e);
                if (service.getGraphMode() == GraphMode.REMOVE_AN_EDGE) service.removeEdge(e);
                if (service.getGraphMode() == GraphMode.NONE) {
                    if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(e);
                }
            }
        });
    }

    private void createFrame() {
        JFrame mainFrame = new JFrame("Graph-Algorithms Visualizer");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException
                | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        mainFrame.setLocationRelativeTo(null);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/main/java/visualizer/data/"));
        Toolbar toolbar = new Toolbar(fileChooser);
        this.service = new GraphService(this, toolbar);
        MenuBar menuBar = new MenuBar(service);

        mainFrame.add(fileChooser);
        mainFrame.add(toolbar, BorderLayout.NORTH);
        mainFrame.add(this);
        mainFrame.setJMenuBar(menuBar);
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
        for (var v : service.getVertices()) {
            v.getState().coloring(g, v);
            g.setFont(new Font("Courier", Font.ITALIC, 30));
            g.drawString(v.id, v.getX() + 16, v.getY() + 36);
        }
    }
}